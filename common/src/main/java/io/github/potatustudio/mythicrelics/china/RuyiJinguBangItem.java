package io.github.potatustudio.mythicrelics.china;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.relic.Allies;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Ruyi Jingu Bang, el báculo del Rey Mono que cambia de tamaño a su voluntad. Clic derecho: se estira de golpe (una onda
 * que golpea todo en línea recta) y queda largo {@link ChinaRelics#RUYI_EXTENDED_TICKS} ticks: más alcance y golpes al
 * doble que barren a los de alrededor. Mientras está estirado se dibuja con su modelo largo (marca de
 * {@code custom_model_data}); el fin del estirón va en los datos del ítem.
 */
public class RuyiJinguBangItem extends RelicItem {
	private static final String UNTIL = "ruyi_until";

	public RuyiJinguBangItem(Properties properties) {
		super(properties);
	}

	/** ¿Está estirado ahora? */
	public static boolean isExtended(ItemStack stack, long now) {
		CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
		return custom != null && custom.copyTag().getLongOr(UNTIL, 0L) > now;
	}

	private static void setExtended(ItemStack stack, long until) {
		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		if (until > 0) {
			tag.putLong(UNTIL, until);
			stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(true), List.of(), List.of()));
		} else {
			tag.remove(UNTIL);
			stack.remove(DataComponents.CUSTOM_MODEL_DATA);
		}
		stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.getCooldowns().isOnCooldown(stack)) {
			return InteractionResult.PASS;
		}
		if (!(level instanceof ServerLevel server)) {
			return InteractionResult.SUCCESS;
		}
		RelicCooldowns.start(player, stack, ChinaRelics.RUYI_COOLDOWN);
		setExtended(stack, server.getGameTime() + ChinaRelics.RUYI_EXTENDED_TICKS);
		// Al estirarse, golpea todo lo que hay en línea recta.
		Vec3 from = player.getEyePosition();
		Vec3 dir = player.getLookAngle();
		Vec3 to = from.add(dir.scale(ChinaRelics.RUYI_LENGTH));
		for (LivingEntity target : Allies.enemiesAround(player, new AABB(from, to).inflate(1.0))) {
			if (target.getBoundingBox().inflate(0.4).clip(from, to).isEmpty() && !target.getBoundingBox().contains(from)) {
				continue;
			}
			if (target.hurtServer(server, server.damageSources().playerAttack(player), ChinaRelics.RUYI_STRETCH_DAMAGE)) {
				target.push(dir.x * 1.2, 0.4, dir.z * 1.2);
				target.needsSync = true;
			}
		}
		for (int i = 1; i <= ChinaRelics.RUYI_LENGTH * 2; i++) {
			Vec3 p = from.add(dir.scale(i * 0.5));
			server.sendParticles(i % 4 == 0 ? ParticleTypes.WAX_ON : ParticleTypes.CRIT, p.x, p.y - 0.2, p.z, 1, 0.02, 0.02, 0.02, 0.0);
		}
		server.sendParticles(ParticleTypes.WAX_ON, player.getX(), player.getY() + 1, player.getZ(), 20, 0.4, 0.6, 0.4, 0.3);
		RelicFx.sound(server, player, SoundEvents.ANVIL_LAND, 0.35f, 1.6f);
		server.playSound(null, to.x, to.y, to.z, SoundEvents.IRON_GOLEM_ATTACK, SoundSource.PLAYERS, 1.0f, 0.8f);
		return InteractionResult.SUCCESS;
	}

	/** Al acabar el estirón vuelve a su tamaño. */
	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity holder, @Nullable EquipmentSlot slot) {
		if (holder.tickCount % 5 == 0 && stack.has(DataComponents.CUSTOM_MODEL_DATA) && !isExtended(stack, level.getGameTime())) {
			setExtended(stack, 0);
			RelicFx.sound(level, holder, SoundEvents.ANVIL_LAND, 0.2f, 2.0f);
		}
	}
}
