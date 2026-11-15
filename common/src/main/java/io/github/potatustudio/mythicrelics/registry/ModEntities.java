package io.github.potatustudio.mythicrelics.registry;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.entity.Cerberus;
import io.github.potatustudio.mythicrelics.entity.FreyrSword;
import io.github.potatustudio.mythicrelics.entity.Jackal;
import io.github.potatustudio.mythicrelics.entity.LongDragon;
import io.github.potatustudio.mythicrelics.entity.MonkeyWarrior;
import io.github.potatustudio.mythicrelics.entity.OdinRaven;
import io.github.potatustudio.mythicrelics.entity.Pegasus;
import io.github.potatustudio.mythicrelics.entity.Scarab;
import io.github.potatustudio.mythicrelics.entity.SkidbladnirBoat;
import io.github.potatustudio.mythicrelics.entity.Sleipnir;
import io.github.potatustudio.mythicrelics.entity.StoneStatue;
import io.github.potatustudio.mythicrelics.entity.Talos;
import io.github.potatustudio.mythicrelics.entity.ThrownRelic;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/**
 * Entidades del mod. Los aliados no se guardan con el mundo ({@code noSave}): desaparecen al descargarse. Las monturas
 * no pueden ser {@code noSave} (en 26.3 el servidor no deja montar esas entidades): se descartan con
 * {@code shouldBeSaved() = false}.
 */
public final class ModEntities {
	private static final DeferredRegister<EntityType<?>> ENTITIES =
			DeferredRegister.create(MythicRelics.MOD_ID, Registries.ENTITY_TYPE);

	public static final RegistrySupplier<EntityType<Scarab>> SCARAB = ENTITIES.register("scarab",
			() -> EntityType.Builder.of(Scarab::new, MobCategory.MISC)
					.sized(0.5f, 0.35f)
					.clientTrackingRange(8)
					.noSave()
					.build(ResourceKey.create(Registries.ENTITY_TYPE, MythicRelics.id("scarab"))));

	public static final RegistrySupplier<EntityType<Jackal>> JACKAL = ENTITIES.register("jackal",
			() -> EntityType.Builder.of(Jackal::new, MobCategory.MISC)
					.sized(0.5f, 0.75f)
					.clientTrackingRange(10)
					.noSave()
					.build(ResourceKey.create(Registries.ENTITY_TYPE, MythicRelics.id("jackal"))));

	public static final RegistrySupplier<EntityType<Cerberus>> CERBERUS = ENTITIES.register("cerberus",
			() -> EntityType.Builder.of(Cerberus::new, MobCategory.MISC)
					.sized(1.4f, 2.2f)
					.fireImmune()
					.clientTrackingRange(10)
					.noSave()
					.build(ResourceKey.create(Registries.ENTITY_TYPE, MythicRelics.id("cerberus"))));

	/** Mjölnir o Gungnir lanzados. */
	public static final RegistrySupplier<EntityType<ThrownRelic>> THROWN_RELIC = ENTITIES.register("thrown_relic",
			() -> EntityType.Builder.<ThrownRelic>of(ThrownRelic::new, MobCategory.MISC)
					.sized(0.5f, 0.5f)
					.clientTrackingRange(4)
					.updateInterval(20)
					.build(ResourceKey.create(Registries.ENTITY_TYPE, MythicRelics.id("thrown_relic"))));

	/** Sleipnir, invocado con la herradura: no se guarda con el mundo (vuelve a Asgard). */
	public static final RegistrySupplier<EntityType<Sleipnir>> SLEIPNIR = ENTITIES.register("sleipnir",
			() -> EntityType.Builder.of(Sleipnir::new, MobCategory.CREATURE)
					.sized(1.3964844f, 1.6f)
					.eyeHeight(1.52f)
					.clientTrackingRange(10)
					.build(ResourceKey.create(Registries.ENTITY_TYPE, MythicRelics.id("sleipnir"))));

	/** Mono guerrero de los Pelos del Rey Mono (aliado temporal). */
	public static final RegistrySupplier<EntityType<MonkeyWarrior>> MONKEY_WARRIOR = ENTITIES.register("monkey_warrior",
			() -> EntityType.Builder.of(MonkeyWarrior::new, MobCategory.MISC)
					.sized(0.6f, 1.8f)
					.clientTrackingRange(10)
					.noSave()
					.build(ResourceKey.create(Registries.ENTITY_TYPE, MythicRelics.id("monkey_warrior"))));

	/** El long de la Perla del dragón: la caja es la del lomo donde va el jinete; el cuerpo largo sigue detrás. */
	public static final RegistrySupplier<EntityType<LongDragon>> LONG_DRAGON = ENTITIES.register("long_dragon",
			() -> EntityType.Builder.of(LongDragon::new, MobCategory.CREATURE)
					.sized(1.6f, 1.0f)
					.clientTrackingRange(12)
					.build(ResourceKey.create(Registries.ENTITY_TYPE, MythicRelics.id("long_dragon"))));

	/** Estatua de piedra de un mob (decorativa, se guarda con el mundo). */
	public static final RegistrySupplier<EntityType<StoneStatue>> STONE_STATUE =
			ENTITIES.register("stone_statue", () -> EntityType.Builder.of(StoneStatue::new,
							MobCategory.MISC)
					.sized(0.8f, 1.9f)
					.clientTrackingRange(10)
					.build(ResourceKey.create(Registries.ENTITY_TYPE, MythicRelics.id("stone_statue"))));

	/** Huginn y Muninn, los cuervos de Odín (mascotas pasivas; no se guardan con el mundo). */
	public static final RegistrySupplier<EntityType<OdinRaven>> ODIN_RAVEN =
			ENTITIES.register("odin_raven", () -> EntityType.Builder.of(OdinRaven::new,
							MobCategory.MISC)
					.sized(0.6f, 0.5f)
					.clientTrackingRange(10)
					.noSave()
					.build(ResourceKey.create(Registries.ENTITY_TYPE, MythicRelics.id("odin_raven"))));

	/** Skíðblaðnir desplegado (bote con modelo propio; se descarta solo, pero no es noSave para poder montarlo). */
	public static final RegistrySupplier<EntityType<SkidbladnirBoat>> SKIDBLADNIR_BOAT =
			ENTITIES.register("skidbladnir_boat", () -> EntityType.Builder.<SkidbladnirBoat>of(
							SkidbladnirBoat::new, MobCategory.MISC)
					.noLootTable()
					.sized(1.375f, 0.5625f)
					.eyeHeight(0.5625f)
					.clientTrackingRange(10)
					.build(ResourceKey.create(Registries.ENTITY_TYPE, MythicRelics.id("skidbladnir_boat"))));

	/** Pegaso, montura de la brida de oro (no es noSave para poder montarlo; no se guarda por shouldBeSaved). */
	public static final RegistrySupplier<EntityType<Pegasus>> PEGASUS =
			ENTITIES.register("pegasus", () -> EntityType.Builder.of(Pegasus::new,
							MobCategory.MISC)
					.sized(1.4f, 1.7f)
					.clientTrackingRange(10)
					.build(ResourceKey.create(Registries.ENTITY_TYPE, MythicRelics.id("pegasus"))));
	/** Talos, el gigante de bronce del clavo (aliado; no se guarda con el mundo). */
	public static final RegistrySupplier<EntityType<Talos>> TALOS =
			ENTITIES.register("talos", () -> EntityType.Builder.of(Talos::new,
							MobCategory.MISC)
					.sized(1.5f, 2.9f)
					.clientTrackingRange(10)
					.fireImmune()
					.noSave()
					.build(ResourceKey.create(Registries.ENTITY_TYPE, MythicRelics.id("talos"))));
	/** La espada de Freyr cuando sale a pelear sola (posición cada tick: se mueve rápido y debe verse suave). */
	public static final RegistrySupplier<EntityType<FreyrSword>> FREYR_SWORD =
			ENTITIES.register("freyr_sword", () -> EntityType.Builder.of(FreyrSword::new,
							MobCategory.MISC)
					.sized(0.5f, 0.5f)
					.clientTrackingRange(10)
					.updateInterval(1)
					.fireImmune()
					.noSave()
					.build(ResourceKey.create(Registries.ENTITY_TYPE, MythicRelics.id("freyr_sword"))));

	private ModEntities() {
	}

	public static void init() {
		ENTITIES.register();
		EntityAttributeRegistry.register(SCARAB, Scarab::createAttributes);
		EntityAttributeRegistry.register(JACKAL, Jackal::createAttributes);
		EntityAttributeRegistry.register(CERBERUS, Cerberus::createAttributes);
		EntityAttributeRegistry.register(SLEIPNIR, Sleipnir::createAttributes);
		EntityAttributeRegistry.register(MONKEY_WARRIOR, MonkeyWarrior::createAttributes);
		EntityAttributeRegistry.register(LONG_DRAGON, LongDragon::createAttributes);
		EntityAttributeRegistry.register(ODIN_RAVEN, OdinRaven::createAttributes);
		EntityAttributeRegistry.register(PEGASUS, Pegasus::createAttributes);
		EntityAttributeRegistry.register(TALOS, Talos::createAttributes);
		EntityAttributeRegistry.register(FREYR_SWORD, FreyrSword::createAttributes);
	}
}
