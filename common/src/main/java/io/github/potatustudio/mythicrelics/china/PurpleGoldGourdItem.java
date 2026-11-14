package io.github.potatustudio.mythicrelics.china;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.minion.Minions;
import io.github.potatustudio.mythicrelics.relic.Allies;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Calabaza roja de oro púrpura (zijin hong hulu) de los demonios Cuerno de Oro y Cuerno de Plata: quien respondía a su
 * llamado quedaba atrapado dentro y se disolvía. Clic derecho a un mob lo absorbe (uno a la vez, no jefes ni
 * jugadores); clic derecho otra vez lo suelta donde miras. Un hostil que se queda dentro demasiado se disuelve: la
 * calabaza te da su botín y su experiencia.
 *
 * <p>El mob guardado va en los datos del ítem ({@code custom_data}), así que se conserva con el mundo.
 */
public class PurpleGoldGourdItem extends RelicItem {
	private static final String CAPTURED = "captured";
	private static final String CAPTURED_AT = "captured_at";
	private static final String HOSTILE = "hostile";
	private static final String TYPE_NAME = "type_name";

	public PurpleGoldGourdItem(Properties properties) {
		super(properties);
	}

	private static @Nullable CompoundTag data(ItemStack stack) {
		CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
		if (custom == null) {
			return null;
		}
		CompoundTag tag = custom.copyTag();
		return tag.contains(CAPTURED) ? tag : null;
	}

	private static boolean canCapture(Player player, LivingEntity target) {
		// Las invocaciones (aliados y monturas que no se guardan con el mundo) tampoco entran.
		return target instanceof Mob && !(target instanceof EnderDragon) && !(target instanceof WitherBoss) && target.isAlive()
				&& target.getType().canSerialize()
				&& target.getMaxHealth() <= ChinaRelics.GOURD_MAX_HEALTH && Minions.ownerOf(target) == null
				&& !target.isPassenger() && !target.isVehicle() && !Allies.isFriend(player, target);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack ignored, Player player, LivingEntity target, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (data(stack) != null || player.getCooldowns().isOnCooldown(stack)) {
			return InteractionResult.PASS;
		}
		if (!canCapture(player, target)) {
			if (!player.level().isClientSide()) {
				RelicFx.notify(player, "gourd_too_strong", ChatFormatting.RED);
			}
			return InteractionResult.FAIL;
		}
		if (!(player.level() instanceof ServerLevel level)) {
			return InteractionResult.SUCCESS;
		}
		TagValueOutput out = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
		out.putString("id", EntityType.getKey(target.getType()).toString());
		target.saveWithoutId(out);
		CompoundTag saved = out.buildResult();
		boolean hostile = target instanceof Enemy;
		String typeName = target.getType().getDescriptionId();
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
			tag.put(CAPTURED, saved);
			tag.putLong(CAPTURED_AT, level.getGameTime());
			tag.putBoolean(HOSTILE, hostile);
			tag.putString(TYPE_NAME, typeName);
		});
		level.sendParticles(ParticleTypes.REVERSE_PORTAL, target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(), 30,
				target.getBbWidth() * 0.5, target.getBbHeight() * 0.4, target.getBbWidth() * 0.5, 0.05);
		RelicFx.sound(level, target, SoundEvents.BOTTLE_FILL_DRAGONBREATH, 1.0f, 0.7f);
		target.discard();
		player.getCooldowns().addCooldown(stack, ChinaRelics.GOURD_COOLDOWN);
		player.sendOverlayMessage(Component.translatable("message.mythicrelics.gourd_captured", Component.translatable(typeName))
				.withStyle(ChatFormatting.GOLD));
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		CompoundTag tag = data(stack);
		if (tag == null || player.getCooldowns().isOnCooldown(stack)) {
			return InteractionResult.PASS;
		}
		if (!(level instanceof ServerLevel server)) {
			return InteractionResult.SUCCESS;
		}
		HitResult hit = player.pick(6.0, 1.0f, false);
		Vec3 at = hit.getType() == HitResult.Type.BLOCK && hit instanceof BlockHitResult block
				? Vec3.atBottomCenterOf(block.getBlockPos().relative(block.getDirection()))
				: player.position().add(player.getLookAngle().multiply(2, 0, 2));
		Entity released = release(server, stack, tag, at, player.getYRot());
		if (released == null) {
			return InteractionResult.FAIL;
		}
		player.getCooldowns().addCooldown(stack, ChinaRelics.GOURD_COOLDOWN);
		return InteractionResult.SUCCESS;
	}

	/** Saca al mob de la calabaza en {@code at} y vacía la calabaza. */
	private static @Nullable Entity release(ServerLevel level, ItemStack stack, CompoundTag tag, Vec3 at, float yaw) {
		CompoundTag saved = tag.getCompoundOrEmpty(CAPTURED);
		Entity entity = EntityType.loadEntityRecursive(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), saved),
				level, EntitySpawnReason.TRIGGERED, e -> {
					e.snapTo(at.x, at.y, at.z, yaw, 0.0f);
					return e;
				});
		stack.remove(DataComponents.CUSTOM_DATA);
		if (entity == null) {
			return null;
		}
		level.addFreshEntity(entity);
		level.sendParticles(ParticleTypes.PORTAL, at.x, at.y + 0.5, at.z, 30, 0.3, 0.5, 0.3, 0.3);
		level.playSound(null, at.x, at.y, at.z, SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 1.0f, 0.7f);
		return entity;
	}

	/** Un hostil que pasa demasiado tiempo dentro se disuelve: su botín y su experiencia son para quien lleva la calabaza. */
	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity holder, @Nullable EquipmentSlot slot) {
		if (!(holder instanceof ServerPlayer player) || player.tickCount % 20 != 0) {
			return;
		}
		CompoundTag tag = data(stack);
		if (tag == null || !tag.getBooleanOr(HOSTILE, false)
				|| level.getGameTime() - tag.getLongOr(CAPTURED_AT, level.getGameTime()) < ChinaRelics.GOURD_DISSOLVE_TICKS) {
			return;
		}
		Entity mob = release(level, stack, tag, player.position(), player.getYRot());
		if (mob instanceof LivingEntity living) {
			living.setInvulnerableTime(0);
			living.hurtServer(level, level.damageSources().playerAttack(player), Float.MAX_VALUE);
			if (living.isAlive()) {
				living.discard();
			}
			level.sendParticles(ParticleTypes.WITCH, player.getX(), player.getY() + 1, player.getZ(), 20, 0.4, 0.5, 0.4, 0.05);
			player.sendOverlayMessage(Component.translatable("message.mythicrelics.gourd_dissolved",
					Component.translatable(tag.getStringOr(TYPE_NAME, ""))).withStyle(ChatFormatting.DARK_PURPLE));
		}
	}

	@Override
	protected void appendExtraLines(ItemStack stack, Consumer<Component> tooltip) {
		CompoundTag tag = data(stack);
		if (tag != null) {
			tooltip.accept(Component.translatable("tooltip.mythicrelics.gourd_inside", Component.translatable(tag.getStringOr(TYPE_NAME, "")))
					.withStyle(ChatFormatting.LIGHT_PURPLE));
		}
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		// Se llama en cada frame: basta con ver si guarda algo (solo guarda al capturado), sin copiar todo su NBT.
		return stack.has(DataComponents.CUSTOM_DATA);
	}
}
