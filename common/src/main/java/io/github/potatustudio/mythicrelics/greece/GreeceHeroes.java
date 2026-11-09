package io.github.potatustudio.mythicrelics.greece;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import io.github.potatustudio.mythicrelics.egypt.EgyptGods;
import io.github.potatustudio.mythicrelics.egypt.EgyptRelics;
import io.github.potatustudio.mythicrelics.minion.Minions;
import io.github.potatustudio.mythicrelics.network.IcarusPayload;
import io.github.potatustudio.mythicrelics.registry.ModEffects;
import io.github.potatustudio.mythicrelics.registry.ModEntities;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

/**
 * Segunda tanda de Grecia (diseño aprobado el 2026-11-09):
 * <ul>
 *   <li><b>Alas de Ícaro</b> (espalda): planeo y aleteos (los decide el cliente y los avisa con {@link IcarusPayload});
 *       sobre la altura {@link #ICARUS_MELT_HEIGHT}, de día y bajo el cielo, la cera se derrite y las alas dejan de
 *       funcionar un rato (la recarga del ítem, que el cliente ve).</li>
 *   <li><b>Vellocino de oro</b> (capa): Héroe de la aldea mientras lo llevas y, al esquilar, alguna pepita de oro.</li>
 *   <li><b>Collar de Cerbero</b> (collar): cuando un mob te hiere aparece un Cerbero cachorro que lo ataca.</li>
 *   <li><b>Agua del Estigia</b>: el efecto «Baño del Estigia» ({@link ModEffects#STYX_BATH}) reduce el daño de frente y
 *       lo duplica por la espalda (el talón).</li>
 *   <li><b>Jarra de Pandora</b> e <b>Hinojo de Prometeo</b>: ítems que se usan ({@link PandoraJarItem},
 *       {@link PrometheusFennelItem}).</li>
 * </ul>
 */
public final class GreeceHeroes {
	public static final int ICARUS_FLAPS = 3;
	public static final double ICARUS_FLAP = 0.6;
	public static final int ICARUS_MELT_HEIGHT = 150;
	public static final int ICARUS_MELT_TICKS = 600;

	public static final float FLEECE_NUGGET_CHANCE = 0.10f;

	public static final int CERBERUS_TICKS = 300;
	public static final int CERBERUS_COOLDOWN = 1200;
	public static final int CERBERUS_BITES = 3;
	public static final int CERBERUS_BURN_SECONDS = 3;

	public static final int PANDORA_COOLDOWN = 1800;
	public static final int PANDORA_EVIL_TICKS = 200;
	public static final int PANDORA_VEXES = 3;
	public static final int PANDORA_VEX_TICKS = 400;
	public static final int PANDORA_HOPE_ABSORPTION_TICKS = 2400;
	public static final int PANDORA_EVIL_CHANCE = 40;
	public static final int PANDORA_VEX_CHANCE = 25;
	public static final int PANDORA_TREASURE_CHANCE = 25;
	public static final int PANDORA_HOPE_CHANCE = 10;

	public static final int STYX_TICKS = 3600;
	public static final int STYX_COOLDOWN = 12000;
	public static final float STYX_FRONT_FACTOR = 0.5f;
	public static final float STYX_HEEL_FACTOR = 2.0f;

	public static final int PROMETHEUS_BURN_SECONDS = 4;
	public static final int PROMETHEUS_COOK_MAX = 8;
	public static final int PROMETHEUS_COOLDOWN = 600;

	private GreeceHeroes() {
	}

	public static void init() {
		NetworkManager.registerReceiver(NetworkManager.c2s(), IcarusPayload.TYPE, IcarusPayload.CODEC,
				(payload, context) -> context.queue(() -> {
					if (context.getPlayer() instanceof ServerPlayer player) {
						icarus(player, payload.flap());
					}
				}));
		EntityEvent.LIVING_HURT.register(GreeceHeroes::onHurt);
		InteractionEvent.INTERACT_ENTITY.register(GreeceHeroes::onInteract);
		TickEvent.SERVER_POST.register(GreeceHeroes::tick);
	}

	// ---- Ícaro -------------------------------------------------------------------------------------------------------

	/** ¿La cera está derretida? (la recarga del ítem, que también ve el cliente). */
	public static boolean waxMelted(Player player) {
		return player.getCooldowns().isOnCooldown(new ItemStack(ModItems.ICARUS_WINGS.get()));
	}

	/** El cliente planea o aletea: el servidor lo valida, reinicia la caída y hace sonar las alas. */
	private static void icarus(ServerPlayer player, boolean flap) {
		if (!EgyptRelics.isWearing(player, ModItems.ICARUS_WINGS) || waxMelted(player) || player.onGround()) {
			return;
		}
		player.resetFallDistance();
		if (flap) {
			ServerLevel level = player.level();
			level.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 1.0, player.getZ(), 6, 0.5, 0.1, 0.5, 0.02);
			level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PHANTOM_FLAP, SoundSource.PLAYERS, 0.6f, 1.3f);
		}
	}

	private static void meltWax(ServerPlayer player) {
		if (player.getY() < ICARUS_MELT_HEIGHT || waxMelted(player) || !EgyptGods.underTheSun(player)) {
			return;
		}
		RelicCooldowns.start(player, "icarus_wings", ICARUS_MELT_TICKS, BuiltInRegistries.ITEM.getKey(ModItems.ICARUS_WINGS.get()));
		ServerLevel level = player.level();
		level.sendParticles(ParticleTypes.FALLING_HONEY, player.getX(), player.getY() + 1.2, player.getZ(), 16, 0.6, 0.3, 0.6, 0.0);
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.5f, 1.6f);
		player.sendOverlayMessage(Component.translatable("message.mythicrelics.icarus_melted").withStyle(ChatFormatting.GOLD));
	}

	// ---- Vellocino ---------------------------------------------------------------------------------------------------

	private static EventResult onInteract(Player player, Entity entity, InteractionHand hand) {
		// Antes de esquilar: si la oveja está lista y se usan tijeras, a veces suelta además una pepita de oro.
		if (player.level() instanceof ServerLevel level && entity instanceof Sheep sheep && sheep.readyForShearing()
				&& player.getItemInHand(hand).is(Items.SHEARS) && EgyptRelics.isWearing(player, ModItems.GOLDEN_FLEECE)
				&& level.getRandom().nextFloat() < FLEECE_NUGGET_CHANCE) {
			ItemEntity nugget = new ItemEntity(level, sheep.getX(), sheep.getY() + 1.0, sheep.getZ(), new ItemStack(Items.GOLD_NUGGET));
			nugget.setDefaultPickUpDelay();
			level.addFreshEntity(nugget);
			level.sendParticles(ParticleTypes.WAX_ON, sheep.getX(), sheep.getY() + 1.0, sheep.getZ(), 8, 0.4, 0.3, 0.4, 0.0);
		}
		return EventResult.pass();
	}

	// ---- Cerbero -----------------------------------------------------------------------------------------------------

	private static EventResult onHurt(LivingEntity target, DamageSource source, float amount) {
		if (target instanceof ServerPlayer player && source.getEntity() instanceof Mob attacker && attacker.isAlive()
				&& Minions.ownerOf(attacker) == null && EgyptRelics.isWearing(player, ModItems.CERBERUS_COLLAR)
				&& RelicCooldowns.tryStart(player, "cerberus_collar", CERBERUS_COOLDOWN,
						BuiltInRegistries.ITEM.getKey(ModItems.CERBERUS_COLLAR.get()))) {
			ServerLevel level = player.level();
			Vec3 at = player.position().add(player.getLookAngle().multiply(-1.2, 0, -1.2));
			Mob hound = Minions.summon(level, player, ModEntities.CERBERUS.get(), at.x, player.getY(), at.z, CERBERUS_TICKS,
					Component.translatable("entity.mythicrelics.cerberus"));
			if (hound != null) {
				hound.setTarget(attacker);
				level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, at.x, player.getY() + 0.4, at.z, 20, 0.4, 0.3, 0.4, 0.02);
				level.playSound(null, at.x, player.getY(), at.z, SoundEvents.ZOGLIN_ANGRY, SoundSource.PLAYERS, 0.8f, 1.4f);
			}
		}
		return EventResult.pass();
	}

	// ---- Estigia (lo llama el mixin de LivingEntity con el daño recibido) -------------------------------------------

	/** Baño del Estigia: la mitad del daño de frente y de costado; el doble por la espalda. Sin dirección, igual. */
	public static float modifyIncomingDamage(LivingEntity self, DamageSource source, float amount) {
		if (!self.hasEffect(ModEffects.STYX_BATH.asHolder())) {
			return amount;
		}
		Vec3 from = source.getSourcePosition();
		if (from == null) {
			return amount;
		}
		Vec3 toSource = from.subtract(self.position()).multiply(1, 0, 1);
		if (toSource.lengthSqr() < 1.0E-4) {
			return amount;
		}
		float yaw = self.getYRot() * ((float) Math.PI / 180f);
		Vec3 facing = new Vec3(-Math.sin(yaw), 0, Math.cos(yaw));
		// Más de 120° de la mirada: viene de atrás.
		boolean heel = facing.dot(toSource.normalize()) < -0.5;
		if (heel && self.level() instanceof ServerLevel level) {
			level.sendParticles(ParticleTypes.DAMAGE_INDICATOR, self.getX(), self.getY() + 0.2, self.getZ(), 4, 0.2, 0.1, 0.2, 0.1);
		}
		return amount * (heel ? STYX_HEEL_FACTOR : STYX_FRONT_FACTOR);
	}

	// ---- Cada tick ---------------------------------------------------------------------------------------------------

	private static void tick(MinecraftServer server) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (EgyptRelics.isWearing(player, ModItems.ICARUS_WINGS) && !player.onGround()) {
				meltWax(player);
			}
			// Rey legítimo: Héroe de la aldea mientras lleves el vellocino.
			if (player.tickCount % 40 == 0 && EgyptRelics.isWearing(player, ModItems.GOLDEN_FLEECE)) {
				MobEffectInstance hero = player.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
				if (hero == null || hero.getDuration() < 100) {
					player.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 220, 0, true, false, true));
				}
			}
		}
	}

	/** Números de los tooltips de la segunda tanda (%1$s, %2$s… de sus líneas de traducción, en este orden). */
	public static Object[] tooltipArgs(String id) {
		return switch (id) {
			case "icarus_wings" -> new Object[] {ICARUS_FLAPS, ICARUS_MELT_HEIGHT, ICARUS_MELT_TICKS / 20};
			case "golden_fleece" -> new Object[] {Math.round(FLEECE_NUGGET_CHANCE * 100)};
			case "cerberus_collar" -> new Object[] {CERBERUS_TICKS / 20, CERBERUS_BITES, CERBERUS_BURN_SECONDS, CERBERUS_COOLDOWN / 20};
			case "pandora_jar" -> new Object[] {PANDORA_COOLDOWN / 20, PANDORA_EVIL_CHANCE, PANDORA_EVIL_TICKS / 20, PANDORA_VEX_CHANCE,
					PANDORA_VEXES, PANDORA_VEX_TICKS / 20, PANDORA_TREASURE_CHANCE, PANDORA_HOPE_CHANCE,
					PANDORA_HOPE_ABSORPTION_TICKS / 20 / 60};
			case "styx_water" -> new Object[] {STYX_TICKS / 20 / 60, Math.round(STYX_FRONT_FACTOR * 100), STYX_COOLDOWN / 20 / 60};
			case "prometheus_fennel" -> new Object[] {PROMETHEUS_BURN_SECONDS, PROMETHEUS_COOK_MAX, PROMETHEUS_COOLDOWN / 20};
			default -> new Object[0];
		};
	}
}
