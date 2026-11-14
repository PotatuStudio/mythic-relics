package io.github.potatustudio.mythicrelics.china;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
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
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Abanico de hoja de plátano de la Princesa Abanico de Hierro: un aleteo levantaba el viento, el segundo traía las
 * nubes y el tercero la lluvia. Es un arma de viento:
 * <ul>
 *   <li>cada golpe lanza también una cuchilla de viento que corta lo que encuentra en línea recta;</li>
 *   <li>clic derecho: un aleteo, un dash de viento de unos 8 bloques que hiere y lanza lejos a los mobs que atraviesas
 *       (tres cargas que se recuperan solas; la barra del ítem muestra cuántas quedan).</li>
 * </ul>
 * Las cargas van en los datos del ítem: cuántas había y desde qué tick se recuperan.
 */
public class BananaLeafFanItem extends RelicItem {
	private static final String CHARGES = "fan_charges";
	private static final String SINCE = "fan_since";
	private static final int DASH_TICKS = 8;
	private static final double DASH_SPEED = 1.8;

	public BananaLeafFanItem(Properties properties) {
		super(properties);
	}

	/** Cargas disponibles ahora, contando las que se recuperaron desde la última vez. */
	private static int charges(ItemStack stack, long now) {
		CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
		if (custom == null) {
			return ChinaRelics.FAN_CHARGES;
		}
		CompoundTag tag = custom.copyTag();
		int stored = tag.getIntOr(CHARGES, ChinaRelics.FAN_CHARGES);
		long since = tag.getLongOr(SINCE, now);
		return (int) Math.min(ChinaRelics.FAN_CHARGES, stored + Math.max(0, now - since) / ChinaRelics.FAN_RECHARGE_TICKS);
	}

	private static void store(ItemStack stack, int charges, long now) {
		CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
		long since = now;
		if (custom != null && charges < ChinaRelics.FAN_CHARGES) {
			// Conserva lo que ya avanzó la recarga de la siguiente carga.
			CompoundTag tag = custom.copyTag();
			int stored = tag.getIntOr(CHARGES, ChinaRelics.FAN_CHARGES);
			long old = tag.getLongOr(SINCE, now);
			if (stored < ChinaRelics.FAN_CHARGES) {
				long gained = Math.max(0, now - old) / ChinaRelics.FAN_RECHARGE_TICKS;
				since = old + gained * ChinaRelics.FAN_RECHARGE_TICKS;
			}
		}
		long from = since;
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
			tag.putInt(CHARGES, charges);
			tag.putLong(SINCE, from);
		});
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!(level instanceof ServerLevel server) || !(player instanceof ServerPlayer serverPlayer)) {
			return InteractionResult.SUCCESS;
		}
		long now = server.getGameTime();
		int left = charges(stack, now);
		if (left <= 0) {
			return InteractionResult.FAIL;
		}
		store(stack, left - 1, now);
		Vec3 look = player.getLookAngle();
		Vec3 flat = new Vec3(look.x, 0, look.z).normalize();
		io.github.potatustudio.mythicrelics.relic.RelicMotion.launch(serverPlayer,
				new Vec3(flat.x * DASH_SPEED, Math.max(0.3, look.y * DASH_SPEED * 0.7), flat.z * DASH_SPEED));
		ChinaRelics.flap(serverPlayer, DASH_TICKS);
		server.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.5, player.getZ(), 25, 0.5, 0.3, 0.5, 0.15);
		server.sendParticles(ParticleTypes.GUST, player.getX(), player.getY() + 1.0, player.getZ(), 2, 0.3, 0.3, 0.3, 0.0);
		server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), SoundSource.PLAYERS,
				0.9f, 1.1f);
		return InteractionResult.SUCCESS;
	}

	/** Cuchilla de viento: cada golpe corta también lo que hay en línea recta frente a ti. */
	@Override
	public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		super.hurtEnemy(stack, target, attacker);
		if (!(attacker instanceof Player player) || !(attacker.level() instanceof ServerLevel server)) {
			return;
		}
		Vec3 from = player.getEyePosition();
		Vec3 dir = player.getLookAngle();
		Vec3 to = from.add(dir.scale(ChinaRelics.FAN_BLADE_RANGE));
		for (LivingEntity hit : server.getEntitiesOfClass(LivingEntity.class, new AABB(from, to).inflate(1.0),
				e -> e != player && e != target && e.isAlive() && !Allies.isFriend(player, e))) {
			if (hit.getBoundingBox().inflate(0.5).clip(from, to).isPresent()) {
				hit.hurtServer(server, server.damageSources().playerAttack(player), ChinaRelics.FAN_BLADE_DAMAGE);
			}
		}
		for (int i = 1; i <= ChinaRelics.FAN_BLADE_RANGE; i++) {
			Vec3 p = from.add(dir.scale(i));
			server.sendParticles(ParticleTypes.SWEEP_ATTACK, p.x, p.y - 0.3, p.z, 1, 0.0, 0.0, 0.0, 0.0);
			server.sendParticles(ParticleTypes.CLOUD, p.x, p.y - 0.3, p.z, 2, 0.1, 0.1, 0.1, 0.02);
		}
		RelicFx.sound(server, player, SoundEvents.BREEZE_SHOOT, 0.6f, 1.4f);
	}

	/** Actualiza las cargas en los datos del ítem para que la barra (en el cliente) muestre las que se recuperaron. */
	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity holder, @Nullable EquipmentSlot slot) {
		if (holder.tickCount % 10 != 0 || stack.get(DataComponents.CUSTOM_DATA) == null) {
			return;
		}
		long now = level.getGameTime();
		int now_ = charges(stack, now);
		CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
		if (tag.getIntOr(CHARGES, ChinaRelics.FAN_CHARGES) != now_) {
			store(stack, now_, now);
		}
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
		return custom != null && custom.copyTag().getIntOr(CHARGES, ChinaRelics.FAN_CHARGES) < ChinaRelics.FAN_CHARGES;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
		int left = custom == null ? ChinaRelics.FAN_CHARGES : custom.copyTag().getIntOr(CHARGES, ChinaRelics.FAN_CHARGES);
		return Mth.clamp(Math.round(13.0f * left / ChinaRelics.FAN_CHARGES), 0, 13);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return 0xBFEFFF;
	}

	@Override
	protected void appendExtraLines(ItemStack stack, Consumer<Component> tooltip) {
		CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
		if (custom != null) {
			int left = custom.copyTag().getIntOr(CHARGES, ChinaRelics.FAN_CHARGES);
			tooltip.accept(Component.translatable("tooltip.mythicrelics.fan_charges", left, ChinaRelics.FAN_CHARGES)
					.withStyle(ChatFormatting.AQUA));
		}
	}
}
