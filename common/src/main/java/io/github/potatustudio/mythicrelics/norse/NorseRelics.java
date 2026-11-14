package io.github.potatustudio.mythicrelics.norse;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.entity.ThrownRelic;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import io.github.potatustudio.mythicrelics.relic.ServerState;
import io.github.potatustudio.mythicrelics.relic.TooltipArgs;
import io.github.potatustudio.mythicrelics.relic.WornRelics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Primera tanda nórdica (diseño aprobado el 2026-11-09), casi todo obra de los enanos:
 * <ul>
 *   <li><b>Mjölnir</b> y <b>Gungnir</b>: armas que se lanzan y vuelven ({@link ThrownRelicItem},
 *       {@link ThrownRelic}).</li>
 *   <li><b>Járngreipr</b> (guantes): puños de hierro y los rayos no te dañan. <b>Megingjörð</b> (cinturón): más daño
 *       cuerpo a cuerpo y menos retroceso. Con Mjölnir forman el <b>set de Thor</b>: cada lanzamiento llama un rayo.</li>
 *   <li><b>Draupnir</b> (anillo): cada novena noche, ocho lingotes de oro ({@link DraupnirNights}).</li>
 *   <li><b>Gjallarhorn</b>, <b>Gleipnir</b> y las <b>manzanas de Iðunn</b>: ítems que se usan.</li>
 * </ul>
 */
public final class NorseRelics {
	public static final int MJOLNIR_RANGE = 24;
	public static final float MJOLNIR_DAMAGE = 18.0f;
	public static final int GUNGNIR_RANGE = 32;
	public static final float GUNGNIR_DAMAGE = 14.0f;
	public static final int GUNGNIR_CONE_DEGREES = 30;

	public static final float JARNGREIPR_FIST = 3.0f;
	public static final float MEGINGJORD_DAMAGE = 0.25f;
	public static final float MEGINGJORD_KNOCKBACK = 0.5f;

	public static final int DRAUPNIR_NIGHTS = 9;
	public static final int DRAUPNIR_INGOTS = 8;

	public static final int HORN_USE_TICKS = 40;
	public static final int HORN_ALLY_RADIUS = 32;
	public static final int HORN_BUFF_TICKS = 1200;
	public static final int HORN_SIGHT_RADIUS = 64;
	public static final int HORN_GLOW_TICKS = 200;
	public static final int HORN_COOLDOWN = 6000;

	public static final int GLEIPNIR_TICKS = 120;
	public static final int GLEIPNIR_COOLDOWN = 600;
	public static final String BOUND_TAG = MythicRelics.MOD_ID + ".bound";

	public static final int IDUNN_MAX = 3;
	public static final int IDUNN_REGEN_TICKS = 200;
	public static final int IDUNN_ABSORPTION_TICKS = 2400;

	private static final Identifier FIST_ID = MythicRelics.id("jarngreipr_fist");
	private static final Identifier BELT_KNOCKBACK_ID = MythicRelics.id("megingjord_knockback");
	/** Mob atado con Gleipnir -> tick del juego en que se suelta. */
	private static final Map<UUID, Long> BOUND = new HashMap<>();

	private NorseRelics() {
	}

	public static void init() {
		EntityEvent.ADD.register(NorseRelics::onEntityAdded);
		TickEvent.SERVER_POST.register(NorseRelics::tick);
	}

	/** Daño recibido: Járngreipr anula los rayos; Megingjörð da más daño cuerpo a cuerpo a quien lo lleva. */
	public static float modifyIncomingDamage(LivingEntity self, DamageSource source, float amount) {
		if (source.is(DamageTypeTags.IS_LIGHTNING) && WornRelics.isWearing(self, ModItems.JARNGREIPR)) {
			return 0;
		}
		if (source.getEntity() instanceof LivingEntity attacker && source.getDirectEntity() == attacker
				&& WornRelics.isWearing(attacker, ModItems.MEGINGJORD)) {
			return amount * (1.0f + MEGINGJORD_DAMAGE);
		}
		return amount;
	}

	// ---- Gleipnir ---------------------------------------------------------------------------------------------------

	static void bind(ServerLevel level, Mob mob) {
		mob.setNoAi(true);
		mob.addTag(BOUND_TAG);
		BOUND.put(mob.getUUID(), level.getGameTime() + GLEIPNIR_TICKS);
	}

	private static void release(Mob mob) {
		mob.setNoAi(false);
		mob.removeTag(BOUND_TAG);
	}

	private static EventResult onEntityAdded(Entity entity, Level level) {
		// Si el servidor se cerró con un mob atado, al cargarlo se suelta.
		if (!level.isClientSide() && entity instanceof Mob mob && mob.entityTags().contains(BOUND_TAG) && !BOUND.containsKey(mob.getUUID())) {
			release(mob);
		}
		return EventResult.pass();
	}

	// ---- Cada tick ---------------------------------------------------------------------------------------------------

	private static void tick(MinecraftServer server) {
		long now = server.overworld().getGameTime();
		Iterator<Map.Entry<UUID, Long>> it = BOUND.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<UUID, Long> e = it.next();
			boolean expired = e.getValue() <= now;
			for (ServerLevel level : server.getAllLevels()) {
				if (level.getEntity(e.getKey()) instanceof Mob mob) {
					if (expired) {
						release(mob);
					} else if (now % 5 == 0) {
						level.sendParticles(ParticleTypes.END_ROD, mob.getX(), mob.getY() + mob.getBbHeight() * 0.5, mob.getZ(), 2,
								mob.getBbWidth() * 0.5, mob.getBbHeight() * 0.3, mob.getBbWidth() * 0.5, 0.0);
					}
					break;
				}
			}
			if (expired) {
				it.remove();
			}
		}
		long day = server.overworld().getOverworldClockTime() / 24000L;
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			boolean gloves = WornRelics.isWearing(player, ModItems.JARNGREIPR);
			setModifier(player, Attributes.ATTACK_DAMAGE, FIST_ID, gloves && player.getMainHandItem().isEmpty() ? JARNGREIPR_FIST : 0);
			setModifier(player, Attributes.KNOCKBACK_RESISTANCE, BELT_KNOCKBACK_ID,
					WornRelics.isWearing(player, ModItems.MEGINGJORD) ? MEGINGJORD_KNOCKBACK : 0);
			if (player.tickCount % 100 == 0 && DraupnirNights.dawn(player, day, WornRelics.isWearing(player, ModItems.DRAUPNIR))) {
				drip(player);
			}
		}
	}

	/** Draupnir: la novena noche gotean los anillos (aquí, lingotes de oro) a tus pies. */
	private static void drip(ServerPlayer player) {
		ServerLevel level = player.level();
		ItemEntity gold = new ItemEntity(level, player.getX(), player.getY() + 0.5, player.getZ(), new ItemStack(Items.GOLD_INGOT, DRAUPNIR_INGOTS));
		gold.setNoPickUpDelay();
		level.addFreshEntity(gold);
		RelicFx.sound(level, player, SoundEvents.AMETHYST_BLOCK_RESONATE, 1.0f, 1.2f);
		level.sendParticles(ParticleTypes.WAX_ON, player.getX(), player.getY() + 1.0, player.getZ(), 20, 0.4, 0.5, 0.4, 0.0);
		RelicFx.notify(player, "draupnir_drip", ChatFormatting.GOLD);
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

	/** Números de los tooltips nórdicos (%1$s, %2$s… de sus líneas de traducción, en este orden). */
	public static void registerTooltipArgs() {
		TooltipArgs.register("mjolnir", MJOLNIR_RANGE);
		TooltipArgs.register("jarngreipr", (int) JARNGREIPR_FIST);
		TooltipArgs.register("megingjord", Math.round(MEGINGJORD_DAMAGE * 100), Math.round(MEGINGJORD_KNOCKBACK * 100));
		TooltipArgs.register("gungnir", GUNGNIR_RANGE);
		TooltipArgs.register("draupnir", DRAUPNIR_NIGHTS, DRAUPNIR_INGOTS);
		TooltipArgs.register("gjallarhorn", HORN_USE_TICKS / 20, HORN_ALLY_RADIUS, HORN_BUFF_TICKS / 20, HORN_SIGHT_RADIUS,
				HORN_GLOW_TICKS / 20, HORN_COOLDOWN / 20 / 60);
		TooltipArgs.register("gleipnir", GLEIPNIR_TICKS / 20, GLEIPNIR_COOLDOWN / 20);
		TooltipArgs.register("idunn_apples", IDUNN_REGEN_TICKS / 20, IDUNN_ABSORPTION_TICKS / 20 / 60, IDUNN_MAX);
	}

	// Ciclo de vida: qué se olvida al desconectarse un jugador y al cerrar el mundo (relic/ServerState).
	static {
		ServerState.clearOnStop(BOUND);
	}
}
