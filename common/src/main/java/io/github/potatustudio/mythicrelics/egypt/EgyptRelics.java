package io.github.potatustudio.mythicrelics.egypt;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.minion.Minions;
import io.github.potatustudio.mythicrelics.registry.ModEntities;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.WornRelics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.zombie.Husk;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Primera tanda de Egipto. Criterio: el efecto nace del significado real del símbolo, con licencias de Minecraft.
 * <ul>
 *   <li><b>Escarabajo dorado</b> (cinturón): Jepri, el sol que renace. Al bajar de 3 corazones, absorción y dos
 *       escarabajos aliados. Recarga.</li>
 *   <li><b>Ojo de Horus</b> (cara): el ojo que Thot sanó. Inmunidad a Ceguera y Oscuridad; tras un golpe fuerte,
 *       recuperas la mitad del daño en unos segundos. Recarga.</li>
 *   <li><b>Anj</b> (collar): la vida. Curación lenta constante; los no-muertos te hacen menos daño (mixin).</li>
 *   <li><b>Vendas de momia</b> (guantes): tus golpes dan hambre y lentitud; los husks no te atacan.</li>
 *   <li><b>Set de Anubis</b>, guardián de las tumbas y guía de las almas:
 *     <ul>
 *       <li><b>Cetro was</b> (arma): el juicio. Destruye al no-muerto que golpeas si tenía poca vida y suelta el doble
 *           de experiencia.</li>
 *       <li><b>Máscara de chacal</b> (cara): los no-muertos que matas sueltan un alma que te cura al tocarla.</li>
 *       <li><b>Anillo de los chacales</b> (anillo): de noche o bajo tierra, al recibir un golpe aparecen dos chacales
 *           aliados. Recarga.</li>
 *       <li>Con las tres piezas, los no-muertos no te atacan a menos que tú los ataques primero.</li>
 *     </ul>
 *   </li>
 * </ul>
 */
public final class EgyptRelics {
	public static final float SCARAB_THRESHOLD = 6.0f;
	public static final int SCARAB_COOLDOWN = 1200;
	public static final int SCARAB_ABSORPTION = 1;
	public static final int SCARAB_MINION_TICKS = 300;

	public static final float EYE_BIG_HIT = 8.0f;
	public static final float EYE_HEAL_FRACTION = 0.5f;
	public static final int EYE_HEAL_TICKS = 100;
	public static final int EYE_COOLDOWN = 900;
	public static final Identifier EYE_COOLDOWN_GROUP = MythicRelics.id("eye_of_horus");

	public static final int ANKH_HEAL_EVERY = 50;
	public static final float ANKH_UNDEAD_REDUCTION = 0.25f;

	public static final int WRAPS_HUNGER_TICKS = 100;
	public static final int WRAPS_SLOW_TICKS = 60;

	public static final float SCEPTER_JUDGMENT_HEALTH = 0.25f;
	public static final int SCEPTER_XP_MULTIPLIER = 2;

	public static final float MASK_SOUL_HEAL = 2.0f;
	public static final int MASK_SOUL_TICKS = 200;
	private static final double MASK_SOUL_PICKUP = 1.5;

	public static final int RING_JACKALS = 2;
	public static final int RING_JACKAL_TICKS = 400;
	public static final int RING_COOLDOWN = 1800;

	/** Ticks durante los que un no-muerto que te golpeaste puede seguir atacándote pese al bono del set de Anubis. */
	private static final int ANUBIS_SET_PROVOKED_TICKS = 200;

	private static final DustParticleOptions GOLD = new DustParticleOptions(0xFFD84A, 1.4f);
	/** Etiqueta del no-muerto destruido por el juicio del cetro, para darle el doble de experiencia al morir. */
	private static final String JUDGED_TAG = MythicRelics.MOD_ID + ".judged";


	/** Curación pendiente del Ojo de Horus: jugador -> [vida por tick, ticks restantes]. */
	private record Healing(float perTick, int[] ticksLeft) {
	}

	private static final Map<UUID, Healing> EYE_HEALING = new HashMap<>();

	/** Alma que suelta un no-muerto con la máscara de chacal: solo la puede recoger quien lo mató. */
	private record Soul(ServerLevel level, Vec3 pos, UUID owner, int[] ticksLeft) {
	}

	private static final List<Soul> SOULS = new ArrayList<>();

	private EgyptRelics() {
	}

	public static void init() {
		EntityEvent.LIVING_DAMAGE_POST.register((entity, source, baseDamage, damageTaken, blocked) -> {
			if (blocked || damageTaken <= 0 || !(entity.level() instanceof ServerLevel level)) {
				return;
			}
			if (entity instanceof ServerPlayer player) {
				if (isWearing(player, ModItems.GOLDEN_SCARAB) && player.getHealth() <= SCARAB_THRESHOLD) {
					scarab(level, player);
				}
				if (damageTaken >= EYE_BIG_HIT && isWearing(player, ModItems.EYE_OF_HORUS)) {
					eyeOfHorus(level, player, damageTaken);
				}
				if (source.getEntity() instanceof LivingEntity && isWearing(player, ModItems.JACKAL_RING)) {
					jackals(level, player);
				}
			}
			if (source.getEntity() instanceof ServerPlayer attacker && source.getDirectEntity() == attacker && attacker != entity) {
				if (isWearing(attacker, ModItems.MUMMY_WRAPS)) {
					entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, WRAPS_HUNGER_TICKS, 1), attacker);
					entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, WRAPS_SLOW_TICKS, 0), attacker);
				}
				if (entity.isAlive() && entity.typeHolder().is(EntityTypeTags.UNDEAD)
						&& attacker.getMainHandItem().is(ModItems.ANUBIS_SCEPTER.get())
						&& entity.getHealth() + damageTaken <= entity.getMaxHealth() * SCEPTER_JUDGMENT_HEALTH) {
					judge(level, attacker, entity);
				}
			}
		});
		EntityEvent.LIVING_DEATH.register((victim, source) -> {
			if (source.getEntity() instanceof ServerPlayer player && victim.level() instanceof ServerLevel level
					&& victim.typeHolder().is(EntityTypeTags.UNDEAD)) {
				if (victim.entityTags().contains(JUDGED_TAG)) {
					// La muerte normal ya da la experiencia una vez; aquí se suma el resto.
					ExperienceOrb.award(level, victim.position(),
							victim.getExperienceReward(level, player) * (SCEPTER_XP_MULTIPLIER - 1));
				}
				if (isWearing(player, ModItems.JACKAL_MASK) && Minions.ownerOf(victim) == null) {
					SOULS.add(new Soul(level, victim.position().add(0, 0.8, 0), player.getUUID(), new int[] {MASK_SOUL_TICKS}));
				}
			}
			return EventResult.pass();
		});
		TickEvent.SERVER_POST.register(EgyptRelics::tick);
	}

	public static boolean isWearing(LivingEntity entity, RegistrySupplier<Item> item) {
		return WornRelics.isWearing(entity, item.get());
	}

	/** Set de Anubis completo: máscara y anillo puestos y el cetro en cualquier parte del inventario. */
	public static boolean hasAnubisSet(Player player) {
		return isWearing(player, ModItems.JACKAL_MASK) && isWearing(player, ModItems.JACKAL_RING)
				&& player.getInventory().contains(stack -> stack.is(ModItems.ANUBIS_SCEPTER.get()));
	}

	public static boolean isAnubisSetPiece(String id) {
		return id.equals("anubis_scepter") || id.equals("jackal_mask") || id.equals("jackal_ring");
	}

	/** Números que muestra el tooltip de cada reliquia (%1$s, %2$s… de sus líneas de traducción, en este orden). */
	public static Object[] tooltipArgs(String id) {
		return switch (id) {
			case "golden_scarab" -> new Object[] {fmt(SCARAB_THRESHOLD / 2), roman(SCARAB_ABSORPTION + 1), SCARAB_MINION_TICKS / 20,
					SCARAB_COOLDOWN / 20};
			case "eye_of_horus" -> new Object[] {fmt(EYE_BIG_HIT / 2), Math.round(EYE_HEAL_FRACTION * 100), EYE_HEAL_TICKS / 20,
					EYE_COOLDOWN / 20};
			case "ankh" -> new Object[] {fmt(ANKH_HEAL_EVERY / 20.0), Math.round(ANKH_UNDEAD_REDUCTION * 100)};
			case "mummy_wraps" -> new Object[] {WRAPS_HUNGER_TICKS / 20, WRAPS_SLOW_TICKS / 20};
			case "anubis_scepter" -> new Object[] {Math.round(SCEPTER_JUDGMENT_HEALTH * 100), SCEPTER_XP_MULTIPLIER};
			case "jackal_mask" -> new Object[] {fmt(MASK_SOUL_HEAL / 2), MASK_SOUL_TICKS / 20};
			case "jackal_ring" -> new Object[] {RING_JACKALS, RING_JACKAL_TICKS / 20, RING_COOLDOWN / 20};
			default -> EgyptGods.tooltipArgs(id);
		};
	}

	/** Nivel de efecto como en vanilla (Absorción II). */
	private static String roman(int level) {
		return switch (level) {
			case 1 -> "I";
			case 2 -> "II";
			case 3 -> "III";
			case 4 -> "IV";
			case 5 -> "V";
			default -> Integer.toString(level);
		};
	}

	/** Número sin ".0" cuando es entero (3 en vez de 3.0); con decimales usa punto en todos los idiomas. */
	private static String fmt(double value) {
		return value == Math.floor(value) ? Integer.toString((int) value) : Double.toString(value);
	}

	/**
	 * Escarabajo dorado ante un golpe mortal (lo llama el mixin en la misma comprobación que el tótem): si no está en
	 * recarga, Jepri te deja con {@link #SCARAB_THRESHOLD} de vida y activa lo mismo que al bajar de esa vida.
	 */
	public static boolean scarabSave(LivingEntity self, net.minecraft.world.damagesource.DamageSource source) {
		if (!(self instanceof ServerPlayer player) || source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)
				|| !isWearing(player, ModItems.GOLDEN_SCARAB) || !RelicCooldowns.ready(player, "golden_scarab")) {
			return false;
		}
		player.setHealth(SCARAB_THRESHOLD);
		scarab(player.level(), player);
		return true;
	}

	private static void scarab(ServerLevel level, ServerPlayer player) {
		if (!RelicCooldowns.tryStart(player, "golden_scarab", SCARAB_COOLDOWN, BuiltInRegistries.ITEM.getKey(ModItems.GOLDEN_SCARAB.get()))) {
			return;
		}
		player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 300, SCARAB_ABSORPTION));
		for (int i = 0; i < 2; i++) {
			// Los escarabajos salen de la tierra y vuelven a enterrarse al acabar su tiempo.
			Minions.summon(level, player, ModEntities.SCARAB.get(), player.getX() + (i == 0 ? 1 : -1), player.getY(), player.getZ(),
					SCARAB_MINION_TICKS, Component.translatable("entity.mythicrelics.scarab"), true);
		}
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 0.5f, 1.6f);
		level.sendParticles(GOLD, player.getX(), player.getY() + 1, player.getZ(), 30, 0.5, 0.7, 0.5, 0);
		player.sendOverlayMessage(Component.translatable("message.mythicrelics.scarab").withStyle(ChatFormatting.GOLD));
	}

	/** Tras un golpe fuerte, el Ojo de Horus devuelve parte del daño como curación gradual. */
	private static void eyeOfHorus(ServerLevel level, ServerPlayer player, float damageTaken) {
		// La recarga es del grupo del ítem (componente USE_COOLDOWN), así que basta un stack nuevo para consultarla.
		if (!RelicCooldowns.ready(player, "eye_of_horus")) {
			return;
		}
		RelicCooldowns.start(player, "eye_of_horus", EYE_COOLDOWN, EYE_COOLDOWN_GROUP);
		EYE_HEALING.put(player.getUUID(), new Healing(damageTaken * EYE_HEAL_FRACTION / EYE_HEAL_TICKS, new int[] {EYE_HEAL_TICKS}));
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 0.6f, 1.4f);
		player.sendOverlayMessage(Component.translatable("message.mythicrelics.eye").withStyle(ChatFormatting.GOLD));
	}

	/** Juicio del cetro: el no-muerto con poca vida se destruye; al morir suelta experiencia extra. */
	private static void judge(ServerLevel level, ServerPlayer player, LivingEntity undead) {
		undead.addTag(JUDGED_TAG);
		undead.hurtServer(level, player.damageSources().playerAttack(player), Float.MAX_VALUE);
		level.sendParticles(GOLD, undead.getX(), undead.getY() + undead.getBbHeight() / 2, undead.getZ(), 20, 0.3, 0.5, 0.3, 0);
		level.playSound(null, undead.getX(), undead.getY(), undead.getZ(), SoundEvents.SOUL_ESCAPE.value(), SoundSource.PLAYERS, 1.0f, 0.8f);
	}

	/** Anillo de los chacales: de noche o bajo tierra, los guardianes de la tumba acuden al golpe. */
	private static void jackals(ServerLevel level, ServerPlayer player) {
		boolean dark = level.isDarkOutside() || !level.canSeeSky(player.blockPosition());
		if (!dark || !RelicCooldowns.tryStart(player, "jackal_ring", RING_COOLDOWN, BuiltInRegistries.ITEM.getKey(ModItems.JACKAL_RING.get()))) {
			return;
		}
		for (int i = 0; i < RING_JACKALS; i++) {
			double angle = Math.PI * 2 * i / RING_JACKALS;
			Minions.summon(level, player, ModEntities.JACKAL.get(), player.getX() + Math.cos(angle) * 1.5, player.getY(),
					player.getZ() + Math.sin(angle) * 1.5, RING_JACKAL_TICKS, Component.translatable("entity.mythicrelics.jackal"));
		}
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SOUL_ESCAPE.value(), SoundSource.PLAYERS, 1.0f, 0.5f);
		player.sendOverlayMessage(Component.translatable("message.mythicrelics.jackals").withStyle(ChatFormatting.GOLD));
	}

	private static void tick(MinecraftServer server) {
		// Cetro de Anubis: los no-muertos que ya se pueden juzgar (menos del 25 % de vida) brillan y suena un aviso.
		if (server.getTickCount() % 10 == 0) {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				io.github.potatustudio.mythicrelics.relic.ExecuteWarning.tick(player, "anubis_scepter",
						player.getMainHandItem().is(ModItems.ANUBIS_SCEPTER.get()), 12,
						m -> m.typeHolder().is(EntityTypeTags.UNDEAD) && m.getHealth() <= m.getMaxHealth() * SCEPTER_JUDGMENT_HEALTH,
						"message.mythicrelics.anubis_ready");
			}
		}
		Iterator<Map.Entry<UUID, Healing>> healing = EYE_HEALING.entrySet().iterator();
		while (healing.hasNext()) {
			Map.Entry<UUID, Healing> e = healing.next();
			ServerPlayer player = server.getPlayerList().getPlayer(e.getKey());
			if (player == null || !player.isAlive() || --e.getValue().ticksLeft()[0] < 0) {
				healing.remove();
				continue;
			}
			player.heal(e.getValue().perTick());
			if (e.getValue().ticksLeft()[0] % 10 == 0) {
				player.level().sendParticles(GOLD, player.getX(), player.getY() + 1.2, player.getZ(), 3, 0.3, 0.4, 0.3, 0);
			}
		}

		Iterator<Soul> souls = SOULS.iterator();
		while (souls.hasNext()) {
			Soul soul = souls.next();
			ServerPlayer owner = server.getPlayerList().getPlayer(soul.owner());
			if (--soul.ticksLeft()[0] <= 0 || owner == null) {
				souls.remove();
				continue;
			}
			if (owner.level() == soul.level() && owner.position().add(0, 0.8, 0).distanceTo(soul.pos()) <= MASK_SOUL_PICKUP) {
				owner.heal(MASK_SOUL_HEAL);
				soul.level().sendParticles(ParticleTypes.SOUL, soul.pos().x, soul.pos().y, soul.pos().z, 8, 0.2, 0.3, 0.2, 0.02);
				soul.level().playSound(null, soul.pos().x, soul.pos().y, soul.pos().z, SoundEvents.SOUL_ESCAPE.value(),
						SoundSource.PLAYERS, 0.7f, 1.4f);
				souls.remove();
			} else if (soul.ticksLeft()[0] % 4 == 0) {
				soul.level().sendParticles(ParticleTypes.SOUL_FIRE_FLAME, soul.pos().x, soul.pos().y, soul.pos().z, 1, 0.1, 0.1, 0.1, 0);
			}
		}

		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			ServerLevel level = player.level();
			if (player.tickCount % ANKH_HEAL_EVERY == 0 && player.getHealth() < player.getMaxHealth()
					&& isWearing(player, ModItems.ANKH)) {
				player.heal(1.0f);
				level.sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1.8, player.getZ(), 2, 0.2, 0.1, 0.2, 0);
			}
			if (player.tickCount % 10 == 0 && isWearing(player, ModItems.MUMMY_WRAPS)) {
				for (Husk husk : level.getEntitiesOfClass(Husk.class, player.getBoundingBox().inflate(24),
						h -> h.getTarget() == player)) {
					husk.setTarget(null);
				}
			}
			if (player.tickCount % 5 == 0 && hasAnubisSet(player)) {
				// Protector de los muertos: los no-muertos no te atacan salvo que los hayas golpeado hace poco.
				for (Mob mob : level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(24),
						m -> m.getTarget() == player && m.typeHolder().is(EntityTypeTags.UNDEAD)
								&& !(m.getLastHurtByMob() == player
										&& m.tickCount - m.getLastHurtByMobTimestamp() < ANUBIS_SET_PROVOKED_TICKS))) {
					mob.setTarget(null);
				}
			}
		}
	}

	// Ciclo de vida: qué se olvida al desconectarse un jugador y al cerrar el mundo (relic/ServerState).
	static {
		io.github.potatustudio.mythicrelics.relic.ServerState.perPlayer(EYE_HEALING);
		io.github.potatustudio.mythicrelics.relic.ServerState.clearOnStop(SOULS);
	}
}
