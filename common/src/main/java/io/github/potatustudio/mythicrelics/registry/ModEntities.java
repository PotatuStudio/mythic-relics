package io.github.potatustudio.mythicrelics.registry;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.entity.CelestialDog;
import io.github.potatustudio.mythicrelics.entity.Cerberus;
import io.github.potatustudio.mythicrelics.entity.Jackal;
import io.github.potatustudio.mythicrelics.entity.Kitsune;
import io.github.potatustudio.mythicrelics.entity.LongDragon;
import io.github.potatustudio.mythicrelics.entity.MonkeyWarrior;
import io.github.potatustudio.mythicrelics.entity.Qilin;
import io.github.potatustudio.mythicrelics.entity.Scarab;
import io.github.potatustudio.mythicrelics.entity.Sleipnir;
import io.github.potatustudio.mythicrelics.entity.ThrownRelic;
import io.github.potatustudio.mythicrelics.entity.UrashimaTurtle;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/** Entidades del mod. Los aliados no se guardan con el mundo ({@code noSave}): desaparecen al descargarse. */
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
					.sized(0.85f, 1.3f)
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
					.noSave()
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
					.noSave()
					.build(ResourceKey.create(Registries.ENTITY_TYPE, MythicRelics.id("long_dragon"))));

	/** Perro Celestial de Erlang Shen (aliado temporal del set). */
	public static final RegistrySupplier<EntityType<CelestialDog>> CELESTIAL_DOG = ENTITIES.register("celestial_dog",
			() -> EntityType.Builder.of(CelestialDog::new, MobCategory.MISC)
					.sized(0.6f, 0.9f)
					.clientTrackingRange(10)
					.noSave()
					.build(ResourceKey.create(Registries.ENTITY_TYPE, MythicRelics.id("celestial_dog"))));

	/** El qilin del cuerno: montura del tamaño de un caballo, inmune al fuego; no se guarda con el mundo. */
	public static final RegistrySupplier<EntityType<Qilin>> QILIN = ENTITIES.register("qilin",
			() -> EntityType.Builder.of(Qilin::new, MobCategory.CREATURE)
					.sized(1.3f, 1.8f)
					.eyeHeight(1.7f)
					.passengerAttachments(1.5f)
					.fireImmune()
					.clientTrackingRange(10)
					.noSave()
					.build(ResourceKey.create(Registries.ENTITY_TYPE, MythicRelics.id("qilin"))));

	/** Kitsune de nueve colas de la esfera estelar (aliado temporal); se dibuja a 1,5 veces. */
	public static final RegistrySupplier<EntityType<Kitsune>> KITSUNE = ENTITIES.register("kitsune",
			() -> EntityType.Builder.of(Kitsune::new, MobCategory.MISC)
					.sized(0.9f, 1.1f)
					.fireImmune()
					.clientTrackingRange(10)
					.noSave()
					.build(ResourceKey.create(Registries.ENTITY_TYPE, MythicRelics.id("kitsune"))));

	/** Tortuga gigante de Urashima (montura submarina); se dibuja a 1,7 veces; no se guarda con el mundo. */
	public static final RegistrySupplier<EntityType<UrashimaTurtle>> URASHIMA_TURTLE = ENTITIES.register("urashima_turtle",
			() -> EntityType.Builder.of(UrashimaTurtle::new, MobCategory.WATER_CREATURE)
					.sized(1.9f, 1.2f)
					.clientTrackingRange(10)
					.noSave()
					.build(ResourceKey.create(Registries.ENTITY_TYPE, MythicRelics.id("urashima_turtle"))));

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
		EntityAttributeRegistry.register(CELESTIAL_DOG, CelestialDog::createAttributes);
		EntityAttributeRegistry.register(QILIN, Qilin::createAttributes);
		EntityAttributeRegistry.register(KITSUNE, Kitsune::createAttributes);
		EntityAttributeRegistry.register(URASHIMA_TURTLE, UrashimaTurtle::createAttributes);
	}
}
