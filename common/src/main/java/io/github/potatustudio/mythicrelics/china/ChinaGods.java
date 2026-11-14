package io.github.potatustudio.mythicrelics.china;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.greece.CharonStash;
import io.github.potatustudio.mythicrelics.minion.Minions;
import io.github.potatustudio.mythicrelics.registry.ModEntities;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import io.github.potatustudio.mythicrelics.relic.WornRelics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Segunda tanda china (diseño aprobado el 2026-11-10): Nezha (lanza y aro), Erlang Shen (ojo, alabarda y su Perro
 * Celestial), el qilin, el pixiu, los talismanes, el xirang y los melocotones de la inmortalidad.
 * <ul>
 *   <li>Ítems: {@link FireTippedSpearItem}, {@link UniverseRingItem}, {@link ErlangHalberdItem}, {@link QilinHornItem},
 *       {@link FuTalismansItem}, el saco de xirang (solo pasiva) e {@link ImmortalPeachBranchItem}.</li>
 *   <li>Aquí: el Ojo de Erlang (hostiles e invisibles brillan), el alcance de la alabarda, el set de Erlang Shen (el
 *       perro aparece al recibir daño), el pixiu (pepitas al matar, tesoros al morir) y los talismanes que vencen.</li>
 * </ul>
 */
public final class ChinaGods {
	public static final int SPEAR_BURN_SECONDS = 4;
	public static final int SPEAR_FLAME_RANGE = 6;
	public static final float SPEAR_FLAME_DAMAGE = 16.0f;
	public static final int SPEAR_COOLDOWN = 100;
	public static final int SPEAR_WHEELS_COOLDOWN = 60;

	public static final int RING_TARGETS = 3;
	public static final int RING_RANGE = 8;
	public static final float RING_DAMAGE = 12.0f;
	public static final int RING_STUN_TICKS = 20;
	public static final int RING_COOLDOWN = 80;

	public static final int ERLANG_SIGHT_RADIUS = 16;
	public static final float HALBERD_REACH = 1.0f;
	public static final int HALBERD_SWEEP_RADIUS = 2;
	public static final float HALBERD_SWEEP_DAMAGE = 4.0f;
	public static final int DOG_TICKS = 900;
	public static final int DOG_COOLDOWN = 1800;
	public static final int DOG_HEALTH = 24;
	public static final int DOG_DAMAGE = 6;

	public static final int QILIN_LEAVE_TICKS = 60;
	public static final int QILIN_COOLDOWN = 6000;
	public static final int QILIN_AURA_RADIUS = 8;

	public static final float PIXIU_CHANCE = 0.3f;
	public static final int PIXIU_MAX_NUGGETS = 3;

	public static final int TALISMAN_TICKS = 160;
	public static final int TALISMAN_COOLDOWN = 300;

	/** Saco de xirang: solo la pasiva (te salva de la lava o del vacío), con recarga de 5 min. */
	public static final int XIRANG_RESCUE_COOLDOWN = 6000;

	public static final int PEACH_GROW_TICKS = 72000;
	public static final int PEACH_ABSORPTION_TICKS = 3600;
	public static final int PEACH_REGEN_TICKS = 200;

	public static final String TALISMAN_TAG = MythicRelics.MOD_ID + ".talisman";
	private static final Identifier HALBERD_REACH_ID = MythicRelics.id("erlang_halberd_reach");
	/** Lo que el pixiu no deja escapar al morir. */
	private static final java.util.Set<Item> TREASURE = java.util.Set.of(Items.GOLD_NUGGET, Items.GOLD_INGOT, Items.GOLD_BLOCK, Items.RAW_GOLD,
			Items.RAW_GOLD_BLOCK, Items.EMERALD, Items.EMERALD_BLOCK, Items.DIAMOND, Items.DIAMOND_BLOCK);
	/** No-muerto con un talismán pegado -> tick del juego en que se despega. */
	private static final Map<UUID, Long> SEALED = new HashMap<>();

	private ChinaGods() {
	}

	public static void init() {
		TickEvent.SERVER_POST.register(ChinaGods::tick);
		EntityEvent.LIVING_HURT.register(ChinaGods::onHurt);
		EntityEvent.LIVING_DEATH.register(ChinaGods::onDeath);
		EntityEvent.ADD.register(ChinaGods::onEntityAdded);
	}

	/** Set de Erlang Shen: el Ojo puesto y la alabarda en la mano. */
	public static boolean hasErlangSet(Player player) {
		return WornRelics.isWearing(player, ModItems.ERLANG_EYE) && player.getMainHandItem().is(ModItems.ERLANG_HALBERD.get());
	}

	/** Nezha completo: la llamarada de la lanza recarga a la mitad con las ruedas puestas. */
	static int spearCooldown(Player player) {
		return WornRelics.isWearing(player, ModItems.WIND_FIRE_WHEELS) ? SPEAR_WHEELS_COOLDOWN : SPEAR_COOLDOWN;
	}

	// ---- Eventos ----------------------------------------------------------------------------------------------------

	private static EventResult onHurt(LivingEntity target, DamageSource source, float amount) {
		if (target instanceof ServerPlayer player && source.getEntity() instanceof LivingEntity attacker && attacker != player
				&& Minions.ownerOf(attacker) == null && hasErlangSet(player)
				&& RelicCooldowns.tryStart(player, "erlang_dog", DOG_COOLDOWN, BuiltInRegistries.ITEM.getKey(ModItems.ERLANG_HALBERD.get()))) {
			ServerLevel level = player.level();
			Mob dog = Minions.summon(level, player, ModEntities.CELESTIAL_DOG.get(), player.getX() + 1.0, player.getY(), player.getZ(),
					DOG_TICKS, Component.translatable("entity.mythicrelics.celestial_dog"));
			if (dog != null) {
				dog.setTarget(attacker);
				level.sendParticles(ParticleTypes.CLOUD, dog.getX(), dog.getY() + 0.5, dog.getZ(), 16, 0.4, 0.3, 0.4, 0.03);
				RelicFx.sound(level, dog, SoundEvents.FOX_AGGRO, 1.0f, 0.5f);
				RelicFx.notify(player, "celestial_dog", ChatFormatting.GOLD);
			}
		}
		return EventResult.pass();
	}

	private static EventResult onDeath(LivingEntity victim, DamageSource source) {
		if (!(victim.level() instanceof ServerLevel level)) {
			return EventResult.pass();
		}
		// Pixiu: come oro; lo que matas suelta pepitas.
		if (victim instanceof Enemy && source.getEntity() instanceof ServerPlayer player && WornRelics.isWearing(player, ModItems.PIXIU_BRACELET)
				&& level.getRandom().nextFloat() < PIXIU_CHANCE) {
			ItemEntity gold = new ItemEntity(level, victim.getX(), victim.getY() + 0.5, victim.getZ(),
					new ItemStack(Items.GOLD_NUGGET, 1 + level.getRandom().nextInt(PIXIU_MAX_NUGGETS)));
			gold.setDefaultPickUpDelay();
			level.addFreshEntity(gold);
		}
		// Pixiu: lo que entra no sale; al morir conservas tus tesoros.
		if (victim instanceof ServerPlayer player && WornRelics.isWearing(player, ModItems.PIXIU_BRACELET)) {
			CharonStash.keepTreasure(player, stack -> TREASURE.contains(stack.getItem()));
		}
		return EventResult.pass();
	}

	// ---- Talismanes -------------------------------------------------------------------------------------------------

	static void seal(ServerLevel level, Mob mob) {
		mob.setNoAi(true);
		mob.addTag(TALISMAN_TAG);
		SEALED.put(mob.getUUID(), level.getGameTime() + TALISMAN_TICKS);
	}

	private static void unseal(Mob mob) {
		mob.setNoAi(false);
		mob.removeTag(TALISMAN_TAG);
	}

	private static EventResult onEntityAdded(Entity entity, Level level) {
		// Si el servidor se cerró con un talismán pegado, al cargarlo se despega.
		if (!level.isClientSide() && entity instanceof Mob mob && mob.entityTags().contains(TALISMAN_TAG) && !SEALED.containsKey(mob.getUUID())) {
			unseal(mob);
		}
		return EventResult.pass();
	}

	// ---- Cada tick ---------------------------------------------------------------------------------------------------

	/** Último suelo firme de cada jugador (a donde lo devuelve el xirang si cae al vacío). */
	private static final Map<UUID, net.minecraft.core.GlobalPos> SAFE_GROUND = new HashMap<>();

	/** Saco de xirang: la tierra crece bajo tus pies y te saca de la lava o del vacío. */
	private static void xirangRescue(ServerPlayer player) {
		if (!RelicCooldowns.tryStart(player, "xirang_rescue", XIRANG_RESCUE_COOLDOWN,
				net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(ModItems.XIRANG_SACK.get()))) {
			return;
		}
		ServerLevel level = player.level();
		BlockPos feet;
		if (player.isInLava()) {
			// Sube hasta la superficie de la lava.
			feet = player.blockPosition();
			while (level.getFluidState(feet).is(net.minecraft.tags.FluidTags.LAVA) && feet.getY() < level.getMaxY()) {
				feet = feet.above();
			}
		} else {
			net.minecraft.core.GlobalPos safe = SAFE_GROUND.get(player.getUUID());
			feet = (safe != null && safe.dimension() == level.dimension() ? safe.pos() : level.getRespawnData().pos()).above();
		}
		for (BlockPos p : BlockPos.betweenClosed(feet.offset(-1, -1, -1), feet.offset(1, -1, 1))) {
			if (level.getBlockState(p).canBeReplaced() || level.getFluidState(p).is(net.minecraft.tags.FluidTags.LAVA)) {
				level.setBlockAndUpdate(p, net.minecraft.world.level.block.Blocks.DIRT.defaultBlockState());
			}
		}
		for (BlockPos p : BlockPos.betweenClosed(feet, feet.above())) {
			if (level.getFluidState(p).is(net.minecraft.tags.FluidTags.LAVA)) {
				level.setBlockAndUpdate(p, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
			}
		}
		player.teleportTo(feet.getX() + 0.5, feet.getY(), feet.getZ() + 0.5);
		player.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
		player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket(player));
		player.resetFallDistance();
		player.clearFire();
		player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE, 100, 0));
		level.sendParticles(new net.minecraft.core.particles.BlockParticleOption(net.minecraft.core.particles.ParticleTypes.BLOCK,
				net.minecraft.world.level.block.Blocks.DIRT.defaultBlockState()), player.getX(), player.getY(), player.getZ(), 40, 1.0, 0.3, 1.0, 0.1);
		level.playSound(null, player.getX(), player.getY(), player.getZ(), net.minecraft.sounds.SoundEvents.ROOTED_DIRT_PLACE,
				net.minecraft.sounds.SoundSource.PLAYERS, 1.2f, 0.7f);
		RelicFx.notify(player, "xirang_rescue", ChatFormatting.GOLD);
	}

	private static void tick(MinecraftServer server) {
		long now = server.overworld().getGameTime();
		Iterator<Map.Entry<UUID, Long>> it = SEALED.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<UUID, Long> e = it.next();
			boolean expired = e.getValue() <= now;
			for (ServerLevel level : server.getAllLevels()) {
				if (level.getEntity(e.getKey()) instanceof Mob mob) {
					if (expired || !mob.isAlive()) {
						unseal(mob);
						level.sendParticles(ParticleTypes.ASH, mob.getX(), mob.getEyeY(), mob.getZ(), 10, 0.2, 0.2, 0.2, 0.02);
					} else if (now % 10 == 0) {
						level.sendParticles(ParticleTypes.WAX_ON, mob.getX(), mob.getEyeY() + 0.2, mob.getZ(), 1, 0.1, 0.1, 0.1, 0.0);
					}
					break;
				}
			}
			if (expired) {
				it.remove();
			}
		}
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (player.onGround() && !player.isInLava()) {
				SAFE_GROUND.put(player.getUUID(), net.minecraft.core.GlobalPos.of(player.level().dimension(), player.blockPosition()));
			}
			if ((player.isInLava() || player.getY() < player.level().getMinY() - 8) && !player.isSpectator()
					&& player.getInventory().contains(s -> s.is(ModItems.XIRANG_SACK.get()))) {
				xirangRescue(player);
			}
			setModifier(player, Attributes.ENTITY_INTERACTION_RANGE, HALBERD_REACH_ID,
					player.getMainHandItem().is(ModItems.ERLANG_HALBERD.get()) ? HALBERD_REACH : 0);
			if (player.tickCount % 10 == 0 && WornRelics.isWearing(player, ModItems.ERLANG_EYE)) {
				erlangSight(player);
			}
		}
	}

	/** Ojo de Erlang Shen: nada se esconde. Los hostiles cercanos y los invisibles brillan (también tras las paredes). */
	private static void erlangSight(ServerPlayer player) {
		for (LivingEntity e : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(ERLANG_SIGHT_RADIUS),
				e -> e != player && e.isAlive() && (e instanceof Enemy || e.isInvisible()))) {
			e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 25, 0, false, false));
		}
	}

	private static void setModifier(Player player, Holder<Attribute> attribute, Identifier id, double amount) {
		AttributeInstance instance = player.getAttribute(attribute);
		if (instance == null) {
			return;
		}
		AttributeModifier current = instance.getModifier(id);
		if (current != null && current.amount() == amount) {
			return;
		}
		instance.removeModifier(id);
		if (amount != 0) {
			instance.addTransientModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE));
		}
	}

	/** Números de los tooltips de la segunda tanda china. */
	public static Object[] tooltipArgs(String id) {
		return switch (id) {
			case "fire_tipped_spear" -> new Object[] {SPEAR_BURN_SECONDS, SPEAR_FLAME_RANGE, (int) SPEAR_FLAME_DAMAGE, SPEAR_COOLDOWN / 20,
					SPEAR_WHEELS_COOLDOWN / 20};
			case "universe_ring" -> new Object[] {RING_TARGETS, RING_RANGE, (int) RING_DAMAGE, RING_STUN_TICKS / 20, RING_COOLDOWN / 20};
			case "erlang_eye" -> new Object[] {ERLANG_SIGHT_RADIUS};
			case "erlang_halberd" -> new Object[] {(int) HALBERD_REACH, HALBERD_SWEEP_RADIUS, DOG_TICKS / 20, DOG_COOLDOWN / 20};
			case "qilin_horn" -> new Object[] {QILIN_AURA_RADIUS, QILIN_LEAVE_TICKS / 20, QILIN_COOLDOWN / 20 / 60};
			case "pixiu_bracelet" -> new Object[] {Math.round(PIXIU_CHANCE * 100), PIXIU_MAX_NUGGETS};
			case "fu_talismans" -> new Object[] {TALISMAN_TICKS / 20, TALISMAN_COOLDOWN / 20};
			case "xirang_sack" -> new Object[] {XIRANG_RESCUE_COOLDOWN / 20 / 60};
			case "immortal_peach_branch" -> new Object[] {PEACH_GROW_TICKS / 24000, PEACH_ABSORPTION_TICKS / 20 / 60, PEACH_REGEN_TICKS / 20};
			default -> io.github.potatustudio.mythicrelics.japan.JapanRelics.tooltipArgs(id);
		};
	}

	// Ciclo de vida: qué se olvida al desconectarse un jugador y al cerrar el mundo (relic/ServerState).
	static {
		io.github.potatustudio.mythicrelics.relic.ServerState.perPlayer(SAFE_GROUND);
		io.github.potatustudio.mythicrelics.relic.ServerState.clearOnStop(SEALED);
	}
}
