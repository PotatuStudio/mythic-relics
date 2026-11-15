package io.github.potatustudio.mythicrelics.client;

import dev.architectury.event.events.client.ClientTickEvent;
import io.github.potatustudio.mythicrelics.greece.GreeceLegends;
import io.github.potatustudio.mythicrelics.norse.NorseSagas;
import io.github.potatustudio.mythicrelics.norse.ThrownRelicItem;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Las armas que se cargan manteniendo clic derecho se ven cargar: alrededor del arma (levantada sobre el hombro) salen
 * partículas que crecen con la carga, y al quedar lista hay un estallido y un sonido para saber que ya se puede soltar.
 * Rayo de Zeus: chispas eléctricas; Mjölnir: chispas y nubes de tormenta; Gungnir: runas que brillan; Gram: brasas
 * doradas; dardo de muérdago: hojas. Lo ve todo el que esté cerca (se dibuja para cada jugador del mundo).
 */
public final class ChargeFeedback {
	private static final DustParticleOptions GOLD = new DustParticleOptions(0xFFD24A, 0.9f);
	private static final DustParticleOptions LEAF = new DustParticleOptions(0x7CC24A, 0.8f);
	private static final DustParticleOptions RUNE = new DustParticleOptions(0x7FF7FF, 0.7f);
	/** Jugadores que ya tuvieron el estallido de "cargado" en este uso. */
	private static final Set<UUID> READY = new HashSet<>();

	private ChargeFeedback() {
	}

	public static void init() {
		ClientTickEvent.CLIENT_POST.register(ChargeFeedback::tick);
	}

	private static void tick(Minecraft client) {
		if (client.level == null || client.isPaused()) {
			return;
		}
		for (Player player : client.level.players()) {
			ItemStack stack = player.getUseItem();
			int needed = needed(stack);
			if (!player.isUsingItem() || needed <= 0 || NorseSagas.isFalconForm(player)) {
				READY.remove(player.getUUID());
				continue;
			}
			float charge = Math.min(1.0f, player.getTicksUsingItem() / (float) needed);
			Vec3 at = stack.is(ModItems.GRAM.get()) ? thrustPoint(player) : weaponPoint(player);
			RandomSource r = player.getRandom();
			int count = 1 + (int) (charge * 3);
			for (int i = 0; i < count; i++) {
				double s = 0.12 + 0.25 * charge;
				client.level.addParticle(particle(stack, r), at.x + (r.nextDouble() - 0.5) * s, at.y + (r.nextDouble() - 0.5) * s,
						at.z + (r.nextDouble() - 0.5) * s, (r.nextDouble() - 0.5) * 0.05, 0.02 + r.nextDouble() * 0.03, (r.nextDouble() - 0.5) * 0.05);
			}
			if (charge >= 1.0f && READY.add(player.getUUID())) {
				for (int i = 0; i < 14; i++) {
					client.level.addParticle(particle(stack, r), at.x, at.y, at.z, (r.nextDouble() - 0.5) * 0.4, r.nextDouble() * 0.25,
							(r.nextDouble() - 0.5) * 0.4);
				}
				client.level.addParticle(ParticleTypes.END_ROD, at.x, at.y, at.z, 0, 0.05, 0);
				client.level.playLocalSound(at.x, at.y, at.z, ready(stack), SoundSource.PLAYERS, 0.8f, 1.4f, false);
			}
		}
	}

	/** Ticks de carga que pide cada arma (0 = no se carga). */
	private static int needed(ItemStack stack) {
		if (stack.is(ModItems.ZEUS_THUNDERBOLT.get())) {
			return GreeceLegends.ZEUS_CHARGE_TICKS;
		}
		if (stack.is(ModItems.MJOLNIR.get()) || stack.is(ModItems.GUNGNIR.get()) || stack.is(ModItems.MISTLETOE_DART.get())) {
			return ThrownRelicItem.MIN_CHARGE;
		}
		return 0;
	}

	private static ParticleOptions particle(ItemStack stack, RandomSource r) {
		if (stack.is(ModItems.ZEUS_THUNDERBOLT.get())) {
			return ParticleTypes.ELECTRIC_SPARK;
		}
		if (stack.is(ModItems.MJOLNIR.get())) {
			return r.nextFloat() < 0.7f ? ParticleTypes.ELECTRIC_SPARK : ParticleTypes.SMOKE;
		}
		if (stack.is(ModItems.GUNGNIR.get())) {
			return r.nextFloat() < 0.6f ? RUNE : ParticleTypes.ENCHANT;
		}
		if (stack.is(ModItems.GRAM.get())) {
			return r.nextFloat() < 0.6f ? GOLD : ParticleTypes.SMALL_FLAME;
		}
		return LEAF;
	}

	private static SoundEvent ready(ItemStack stack) {
		if (stack.is(ModItems.ZEUS_THUNDERBOLT.get()) || stack.is(ModItems.MJOLNIR.get())) {
			return SoundEvents.TRIDENT_THUNDER.value();
		}
		return SoundEvents.AMETHYST_BLOCK_CHIME;
	}

	/** Gram se carga en la pose de estocada: la hoja va adelante, a la altura del pecho. */
	private static Vec3 thrustPoint(Player player) {
		double scale = player.getScale();
		return player.position().add(0.0, 1.3 * scale, 0.0).add(player.getLookAngle().scale(0.9 * scale));
	}

	/** Dónde está el arma: sobre el hombro del brazo que la usa (la pose de lanzar del tridente). */
	private static Vec3 weaponPoint(Player player) {
		float yaw = player.yBodyRot * ((float) Math.PI / 180.0f);
		boolean right = (player.getUsedItemHand() == InteractionHand.MAIN_HAND) == (player.getMainArm() == HumanoidArm.RIGHT);
		double side = right ? -0.38 : 0.38;
		double scale = player.getScale();
		Vec3 sideways = new Vec3(Math.cos(yaw) * side, 0.0, Math.sin(yaw) * side);
		return player.position().add(sideways.scale(scale)).add(0.0, 2.05 * scale, 0.0);
	}
}
