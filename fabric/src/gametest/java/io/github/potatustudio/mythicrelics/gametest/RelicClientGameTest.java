package io.github.potatustudio.mythicrelics.gametest;

import eu.pb4.trinkets.api.SlotGroup;
import eu.pb4.trinkets.api.SlotType;
import eu.pb4.trinkets.api.TrinketAttachment;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.client.RelicClientTicks;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotOptions;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.AABB;

/**
 * Recorrido automático de las reliquias en un mundo de prueba: tooltips (texto), cada reliquia en la mano (primera y
 * tercera persona), puesta (frente y espalda), las entidades del mod (frente y costado) y cada habilidad (clic derecho
 * mantenido y soltado, agachado si su texto lo menciona, y un golpe a un zombi). Las entradas son del juego (TestInput),
 * nunca del sistema. Salida: carpeta mythic-test/ en el directorio de la ejecución (capturas y registro.txt).
 */
public class RelicClientGameTest implements FabricClientGameTest {
	private static final int Y = 150;
	private Path out;
	private PrintWriter log;
	/** Lo que salió mal: la prueba falla al final si hay algo (las fases siguen corriendo para dejar todas las capturas). */
	private final List<String> failures = new ArrayList<>();

	@Override
	public void runTest(ClientGameTestContext ctx) {
		out = Path.of("mythic-test").toAbsolutePath();
		try {
			Files.createDirectories(out);
			log = new PrintWriter(Files.newBufferedWriter(out.resolve("registro.txt"), StandardCharsets.UTF_8), true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try (TestSingleplayerContext sp = ctx.worldBuilder().create()) {
			TestServerContext srv = sp.getServer();
			setup(srv);
			sp.getConnection().waitForChunksRender();
			ctx.waitTicks(20);

			List<Item> relics = relics();
			log("reliquias: " + relics.size());
			// MYTHIC_FASES (variable de entorno, separada por comas) corre solo esas fases; sin ella, todas.
			String only = System.getenv().getOrDefault("MYTHIC_FASES", "");
			java.util.function.Predicate<String> run = f -> only.isBlank() || List.of(only.split(",")).contains(f);
			if (run.test("tooltips")) {
				tooltips(ctx, relics);
			}
			if (run.test("manos")) {
				hands(ctx, srv, relics);
			}
			if (run.test("puestas")) {
				worn(ctx, srv, relics);
			}
			if (run.test("una")) {
				wornOneByOne(ctx, srv, relics);
			}
			if (run.test("freyr")) {
				freyr(ctx, srv);
			}
			if (run.test("entidades")) {
				entities(ctx, srv, sp);
			}
			if (run.test("escudos")) {
				vanillaShield(ctx, srv);
			}
			if (run.test("sets")) {
				setTooltip(ctx, srv);
			}
			if (run.test("habilidades")) {
				abilities(ctx, srv, relics);
			}
			if (run.test("aserciones")) {
				assertions(ctx, srv);
			}
			log(failures.isEmpty() ? "FIN: sin fallos" : "FIN: " + failures.size() + " fallos");
		} catch (Throwable t) {
			log("FALLO GENERAL: " + t);
			t.printStackTrace(log);
			throw t;
		} finally {
			log.close();
		}
		if (!failures.isEmpty()) {
			throw new AssertionError("Mythic Relics: " + failures.size() + " fallos: " + failures);
		}
	}

	// ---- Preparación ------------------------------------------------------------------------------------------------

	private void setup(TestServerContext srv) {
		for (String c : List.of("gamerule advance_time false", "gamerule advance_weather false", "gamerule spawn_mobs false",
				"gamerule spawn_monsters false", "gamerule keep_inventory true", "gamerule send_command_feedback false",
				"gamerule spawn_phantoms false", "time set noon", "weather clear", "gamemode survival @a",
				"fill -24 " + (Y - 1) + " -24 24 " + (Y - 1) + " 24 minecraft:grass_block",
				"fill -24 " + Y + " -24 24 " + (Y + 12) + " 24 minecraft:air",
				"effect give @a minecraft:resistance infinite 4 true", "effect give @a minecraft:saturation infinite 0 true",
				"effect give @a minecraft:regeneration infinite 2 true")) {
			srv.runCommand(c);
		}
		place(srv, 0.5, 0.5, 0, 10);
	}

	private void place(TestServerContext srv, double x, double z, float yaw, float pitch) {
		srv.runCommand(String.format(Locale.ROOT, "tp @a %.2f %d %.2f %.1f %.1f", x, Y, z, yaw, pitch));
	}

	private static List<Item> relics() {
		List<Item> list = new ArrayList<>();
		for (Item item : BuiltInRegistries.ITEM) {
			if (BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(MythicRelics.MOD_ID)) {
				list.add(item);
			}
		}
		return list;
	}

	private static String id(Item item) {
		return BuiltInRegistries.ITEM.getKey(item).getPath();
	}

	private static ServerPlayer player(MinecraftServer s) {
		return s.getPlayerList().getPlayers().getFirst();
	}

	private void shot(ClientGameTestContext ctx, String name) {
		// Tras un tp el cuerpo se queda girado respecto de la cabeza (hasta 50°) y las reliquias se ven torcidas:
		// se alinea el cuerpo con la mirada antes de cada captura.
		ctx.runOnClient(mc -> {
			if (mc.player != null) {
				float yaw = mc.player.getYRot();
				mc.player.yBodyRot = yaw;
				mc.player.yBodyRotO = yaw;
				mc.player.yHeadRot = yaw;
				mc.player.yHeadRotO = yaw;
			}
		});
		ctx.takeScreenshot(TestScreenshotOptions.of(name).withSize(640, 360).withDestinationDir(out).disableCounterPrefix());
	}

	private void camera(ClientGameTestContext ctx, CameraType type) {
		ctx.runOnClient(mc -> mc.options.setCameraType(type));
	}

	private void log(String s) {
		log.println(s);
	}

	private void fail(String what) {
		failures.add(what);
		log("FALLO " + what);
	}

	private void check(boolean ok, String what) {
		if (ok) {
			log("OK " + what);
		} else {
			fail(what);
		}
	}

	// ---- Aserciones (arreglos de la auditoría del 2026-11-19) --------------------------------------------------------

	private void assertions(ClientGameTestContext ctx, TestServerContext srv) {
		reset(ctx, srv);
		// Cada %n$s de los tooltips tiene su número registrado.
		List<String> args = io.github.potatustudio.mythicrelics.relic.TooltipArgs.problems();
		check(args.isEmpty(), "tooltipArgs completos " + args);
		// Ninguna pieza en dos sets.
		Map<String, String> owner = new LinkedHashMap<>();
		for (var set : io.github.potatustudio.mythicrelics.relic.RelicSets.values()) {
			for (var piece : set.pieces()) {
				String before = owner.put(piece.id(), set.key());
				check(before == null, "pieza " + piece.id() + " en un solo set" + (before == null ? "" : " (" + before + " y " + set.key() + ")"));
			}
		}
		// B2: un tajo de la espada de Freyr (fuente RelicDamage con la espada como entidad directa) no cobra la maldición
		// de Tyrfing; un golpe de la mano sí (si no, la prueba no distingue nada).
		srv.runOnServer(s -> {
			ServerPlayer p = player(s);
			p.removeAllEffects();
			p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,
					new ItemStack(io.github.potatustudio.mythicrelics.registry.ModItems.TYRFING.get()));
			p.setHealth(p.getMaxHealth());
			var level = p.level();
			var zombie = net.minecraft.world.entity.EntityTypes.HUSK.create(level, net.minecraft.world.entity.EntitySpawnReason.COMMAND);
			zombie.snapTo(p.getX() + 2, p.getY(), p.getZ(), 0, 0);
			zombie.setNoAi(true);
			level.addFreshEntity(zombie);
			var sword = io.github.potatustudio.mythicrelics.registry.ModEntities.FREYR_SWORD.get()
					.create(level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
			float before = p.getHealth();
			zombie.hurtServer(level, io.github.potatustudio.mythicrelics.relic.RelicDamage.byPlayer(level, sword, p), 1.0f);
			check(p.getHealth() == before, "B2: tajo de Freyr sin maldición de Tyrfing (vida " + before + " -> " + p.getHealth() + ")");
			zombie.setInvulnerableTime(0);
			zombie.hurtServer(level, level.damageSources().playerAttack(p), 1.0f);
			check(p.getHealth() < before, "B2: golpe de la mano con Tyrfing sí la cobra (control)");
			zombie.discard();
			p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, ItemStack.EMPTY);
			p.setHealth(p.getMaxHealth());
		});
		// B1: dos mobs heridos por Dáinsleif y uno muere desangrado: el servidor no se cae.
		srv.runOnServer(s -> {
			ServerPlayer p = player(s);
			p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,
					new ItemStack(io.github.potatustudio.mythicrelics.registry.ModItems.DAINSLEIF.get()));
			var level = p.level();
			for (int i = 0; i < 3; i++) {
				var zombie = net.minecraft.world.entity.EntityTypes.HUSK.create(level, net.minecraft.world.entity.EntitySpawnReason.COMMAND);
				zombie.snapTo(p.getX() - 2 + i * 2, p.getY(), p.getZ() + 3, 0, 0);
				zombie.setNoAi(true);
				zombie.addTag("mythic_test_bleed");
				level.addFreshEntity(zombie);
				zombie.hurtServer(level, level.damageSources().playerAttack(p), 1.0f);
				zombie.setHealth(i == 0 ? 0.5f : 20.0f);
			}
			p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, ItemStack.EMPTY);
		});
		ctx.waitTicks(60);
		boolean firstDead = srv.computeOnServer(s -> player(s).level().getEntitiesOfClass(net.minecraft.world.entity.monster.zombie.Husk.class,
				new AABB(-30, Y - 5, -30, 30, Y + 10, 30), z -> z.entityTags().contains("mythic_test_bleed")).size() < 3);
		check(firstDead, "B1: el herido de Dáinsleif se desangra sin tumbar el servidor");
		srv.runCommand("kill @e[tag=mythic_test_bleed]");
	}

	/** Mata lo que no es jugador, baja de la montura, limpia manos, accesorios y recargas, y cura. */
	private void reset(ClientGameTestContext ctx, TestServerContext srv) {
		srv.runCommand("ride @a dismount");
		srv.runCommand("weather clear");
		srv.runCommand("time set noon");
		srv.runCommand("kill @e[type=!minecraft:player]");
		srv.runOnServer(s -> {
			ServerPlayer p = player(s);
			p.getInventory().clearContent();
			unequipAll(p);
			for (Item item : relics()) {
				ItemStack st = new ItemStack(item);
				p.getCooldowns().removeCooldown(p.getCooldowns().getCooldownGroup(st));
			}
			p.setHealth(p.getMaxHealth());
			p.clearFire();
		});
		ctx.waitTicks(2);
	}

	// ---- Accesorios (Trinkets) --------------------------------------------------------------------------------------

	private static List<SlotType> slotTypes(Player p) {
		List<SlotType> types = new ArrayList<>();
		for (SlotGroup g : TrinketsApi.getPlayerSlots(p).values()) {
			types.addAll(g.getSlots());
		}
		return types;
	}

	/** Primer espacio de accesorio que acepta el ítem, o null si no se pone. */
	private static SlotType wornSlot(Player p, ItemStack stack) {
		TrinketAttachment att = TrinketsApi.getAttachment(p);
		for (SlotType t : slotTypes(p)) {
			TrinketSlotAccess access = att.getSlotAccess(t.getId(), 0);
			if (t.validatorCheck(stack, access, p)) {
				return t;
			}
		}
		return null;
	}

	private static boolean equip(Player p, ItemStack stack) {
		SlotType t = wornSlot(p, stack);
		if (t == null) {
			return false;
		}
		TrinketAttachment att = TrinketsApi.getAttachment(p);
		for (int i = 0; i < t.amount(); i++) {
			TrinketSlotAccess a = att.getSlotAccess(t.getId(), i);
			if (a.get().isEmpty()) {
				return a.set(stack.copy());
			}
		}
		return false;
	}

	private static void unequipAll(Player p) {
		TrinketAttachment att = TrinketsApi.getAttachment(p);
		for (SlotType t : slotTypes(p)) {
			for (int i = 0; i < t.amount(); i++) {
				att.getSlotAccess(t.getId(), i).set(ItemStack.EMPTY);
			}
		}
	}

	// ---- 1. Tooltips --------------------------------------------------------------------------------------------------

	private void tooltips(ClientGameTestContext ctx, List<Item> relics) {
		StringBuilder sb = new StringBuilder();
		for (boolean shift : new boolean[] {false, true}) {
			if (shift) {
				ctx.getInput().holdShift();
			}
			ctx.waitTick();
			sb.append(shift ? "\n===== CON SHIFT =====\n" : "===== SIN SHIFT =====\n");
			ctx.runOnClient(mc -> {
				for (Item item : relics) {
					sb.append("--- ").append(id(item)).append('\n');
					for (Component c : new ItemStack(item).getTooltipLines(Item.TooltipContext.of(mc.level), mc.player,
							TooltipFlag.NORMAL)) {
						sb.append("  ").append(c.getString()).append('\n');
					}
				}
			});
			if (shift) {
				ctx.getInput().releaseShift();
			}
		}
		try {
			Files.writeString(out.resolve("tooltips.txt"), sb.toString(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			log("tooltips: " + e);
		}
	}

	// ---- 2. En la mano ------------------------------------------------------------------------------------------------

	private void hands(ClientGameTestContext ctx, TestServerContext srv, List<Item> relics) {
		reset(ctx, srv);
		place(srv, 0.5, 0.5, 0, 10);
		for (Item item : relics) {
			srv.runOnServer(s -> player(s).setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new ItemStack(item)));
			camera(ctx, CameraType.FIRST_PERSON);
			ctx.waitTicks(12);
			shot(ctx, "mano1_" + id(item));
			camera(ctx, CameraType.THIRD_PERSON_FRONT);
			ctx.waitTicks(2);
			shot(ctx, "mano3_" + id(item));
		}
		camera(ctx, CameraType.FIRST_PERSON);
	}

	// ---- 3. Puestas -------------------------------------------------------------------------------------------------

	private void worn(ClientGameTestContext ctx, TestServerContext srv, List<Item> relics) {
		reset(ctx, srv);
		Map<String, List<Item>> bySlot = srv.computeOnServer(s -> {
			Map<String, List<Item>> m = new LinkedHashMap<>();
			for (Item item : relics) {
				SlotType t = wornSlot(player(s), new ItemStack(item));
				if (t != null) {
					m.computeIfAbsent(t.getId(), k -> new ArrayList<>()).add(item);
				}
			}
			return m;
		});
		int rounds = bySlot.values().stream().mapToInt(List::size).max().orElse(0);
		bySlot.forEach((slot, items) -> log("espacio " + slot + ": " + items.stream().map(RelicClientGameTest::id).toList()));
		for (int r = 0; r < rounds; r++) {
			int round = r;
			List<String> names = srv.computeOnServer(s -> {
				ServerPlayer p = player(s);
				unequipAll(p);
				List<String> n = new ArrayList<>();
				for (List<Item> items : bySlot.values()) {
					if (round < items.size() && equip(p, new ItemStack(items.get(round)))) {
						n.add(id(items.get(round)));
					}
				}
				return n;
			});
			log("puestas ronda " + r + ": " + names);
			camera(ctx, CameraType.THIRD_PERSON_FRONT);
			ctx.waitTicks(4);
			shot(ctx, "puestas" + r + "_frente");
			camera(ctx, CameraType.THIRD_PERSON_BACK);
			ctx.waitTicks(2);
			shot(ctx, "puestas" + r + "_espalda");
			// De costado: el jugador gira 90° y la cámara de frente lo ve de lado.
			place(srv, 0.5, 0.5, 90, 10);
			camera(ctx, CameraType.THIRD_PERSON_FRONT);
			ctx.waitTicks(3);
			ctx.runOnClient(mc -> mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT));
			shot(ctx, "puestas" + r + "_lado");
			place(srv, 0.5, 0.5, 0, 10);
		}
		camera(ctx, CameraType.FIRST_PERSON);
	}

	/** Cada reliquia puesta sola, de frente, de espalda y de lado (para revisar su modelo sin otras encima). */
	private void wornOneByOne(ClientGameTestContext ctx, TestServerContext srv, List<Item> relics) {
		reset(ctx, srv);
		for (Item item : relics) {
			boolean worn = srv.computeOnServer(s -> {
				ServerPlayer p = player(s);
				unequipAll(p);
				return equip(p, new ItemStack(item));
			});
			if (!worn) {
				continue;
			}
			place(srv, 0.5, 0.5, 0, 10);
			camera(ctx, CameraType.THIRD_PERSON_FRONT);
			ctx.waitTicks(4);
			shot(ctx, "una_" + id(item) + "_1frente");
			camera(ctx, CameraType.THIRD_PERSON_BACK);
			ctx.waitTicks(2);
			shot(ctx, "una_" + id(item) + "_2espalda");
			place(srv, 0.5, 0.5, 90, 10);
			camera(ctx, CameraType.THIRD_PERSON_FRONT);
			ctx.waitTicks(3);
			shot(ctx, "una_" + id(item) + "_3lado");
			// Desde arriba: mirando hacia abajo, la cámara de espalda queda sobre el jugador.
			place(srv, 0.5, 0.5, 0, 90);
			camera(ctx, CameraType.THIRD_PERSON_BACK);
			ctx.waitTicks(3);
			shot(ctx, "una_" + id(item) + "_4arriba");
		}
		camera(ctx, CameraType.FIRST_PERSON);
	}

	/** Espada de Freyr: envainada en la espalda y, desenvainada, la vaina vacía con la espada al lado (deben medir igual). */
	private void freyr(ClientGameTestContext ctx, TestServerContext srv) {
		reset(ctx, srv);
		srv.runOnServer(s -> equip(player(s), new ItemStack(io.github.potatustudio.mythicrelics.registry.ModItems.FREYR_SWORD.get())));
		place(srv, 0.5, 0.5, 0, 10);
		camera(ctx, CameraType.THIRD_PERSON_BACK);
		ctx.waitTicks(4);
		shot(ctx, "freyr_1envainada_espalda");
		place(srv, 0.5, 0.5, 90, 10);
		camera(ctx, CameraType.THIRD_PERSON_FRONT);
		ctx.waitTicks(3);
		shot(ctx, "freyr_2envainada_lado");
		place(srv, 0.5, 0.5, 0, 10);
		srv.runOnServer(s -> {
			ServerPlayer p = player(s);
			var sword = io.github.potatustudio.mythicrelics.registry.ModEntities.FREYR_SWORD.get()
					.create(p.level(), net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
			sword.draw(p);
			p.level().addFreshEntity(sword);
		});
		camera(ctx, CameraType.THIRD_PERSON_BACK);
		ctx.waitTicks(12);
		shot(ctx, "freyr_3desenvainada_espalda");
		place(srv, 0.5, 0.5, 90, 10);
		camera(ctx, CameraType.THIRD_PERSON_FRONT);
		ctx.waitTicks(3);
		shot(ctx, "freyr_4desenvainada_lado");
		camera(ctx, CameraType.FIRST_PERSON);
	}

	// ---- 4. Entidades -----------------------------------------------------------------------------------------------

	private void entities(ClientGameTestContext ctx, TestServerContext srv, TestSingleplayerContext sp) {
		reset(ctx, srv);
		List<EntityType<?>> types = new ArrayList<>();
		for (EntityType<?> t : BuiltInRegistries.ENTITY_TYPE) {
			if (BuiltInRegistries.ENTITY_TYPE.getKey(t).getNamespace().equals(MythicRelics.MOD_ID)) {
				types.add(t);
			}
		}
		for (EntityType<?> type : types) {
			String name = BuiltInRegistries.ENTITY_TYPE.getKey(type).getPath();
			reset(ctx, srv);
			place(srv, 0.5, 0.5, 0, 10);
			ctx.waitTicks(25);
			srv.runCommand("summon mythicrelics:" + name + " 0.5 " + Y + " 8.5 {NoAI:1b,NoGravity:0b,Rotation:[180f,0f]}");
			ctx.waitTicks(5);
			double[] size = srv.computeOnServer(s -> {
				List<? extends Entity> found = player(s).level().getEntities(type, new AABB(-20, Y - 5, -20, 20, Y + 20, 30),
						e -> true);
				if (found.isEmpty()) {
					return null;
				}
				Entity e = found.getFirst();
				return new double[] {e.getBbWidth(), e.getBbHeight()};
			});
			if (size == null) {
				log("entidad " + name + ": NO QUEDÓ EN EL MUNDO (se descarta sola o no se puede invocar)");
				continue;
			}
			double span = Math.max(size[0], size[1]);
			double back = 8.5 - (2.5 + span * 1.6);
			float pitch = (float) Math.toDegrees(Math.atan2(1.62 - size[1] / 2, 8.5 - back));
			place(srv, 0.5, back, 0, pitch);
			ctx.waitTicks(6);
			shot(ctx, "entidad_" + name + "_frente");
			srv.runCommand("tp @e[type=mythicrelics:" + name + "] 0.5 " + Y + " 8.5 90 0");
			ctx.waitTicks(15);
			shot(ctx, "entidad_" + name + "_lado");
			log("entidad " + name + ": tamaño " + size[0] + " x " + size[1]);
		}
	}

	// ---- Referencias ------------------------------------------------------------------------------------------------

	private void vanillaShield(ClientGameTestContext ctx, TestServerContext srv) {
		reset(ctx, srv);
		place(srv, 0.5, 0.5, 0, 6);
		for (String id : List.of("minecraft:shield", "mythicrelics:achilles_shield", "mythicrelics:ancile", "mythicrelics:athena_aegis", "mythicrelics:medusa_head")) {
			srv.runOnServer(s -> player(s).setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,
					new ItemStack(BuiltInRegistries.ITEM.getValue(net.minecraft.resources.Identifier.parse(id)))));
			String n = id.substring(id.indexOf(':') + 1);
			ctx.waitTicks(12);
			shot(ctx, "escudo_" + n + "_quieto");
			ctx.getInput().holdKey(o -> o.keyUse);
			ctx.waitTicks(12);
			shot(ctx, "escudo_" + n + "_bloqueando");
			camera(ctx, CameraType.THIRD_PERSON_FRONT);
			ctx.waitTicks(3);
			shot(ctx, "escudo_" + n + "_bloqueando3");
			ctx.getInput().releaseKey(o -> o.keyUse);
			camera(ctx, CameraType.FIRST_PERSON);
			ctx.waitTicks(5);
		}
	}

	/** Set de Anubis con la máscara puesta y el anillo en el inventario (no puesto): debe salir ◐. */
	private void setTooltip(ClientGameTestContext ctx, TestServerContext srv) {
		reset(ctx, srv);
		Item mask = BuiltInRegistries.ITEM.getValue(MythicRelics.id("jackal_mask"));
		Item ring = BuiltInRegistries.ITEM.getValue(MythicRelics.id("jackal_ring"));
		srv.runOnServer(s -> {
			ServerPlayer p = player(s);
			equip(p, new ItemStack(mask));
			p.getInventory().setItem(5, new ItemStack(ring));
		});
		ctx.waitTicks(5);
		ctx.getInput().holdShift();
		ctx.waitTick();
		String text = ctx.computeOnClient(mc -> {
			StringBuilder sb = new StringBuilder();
			for (Component c : new ItemStack(ring).getTooltipLines(Item.TooltipContext.of(mc.level), mc.player, TooltipFlag.NORMAL)) {
				sb.append(c.getString()).append('\n');
			}
			return sb.toString();
		});
		ctx.getInput().releaseShift();
		log("set de Anubis (máscara puesta, anillo en el inventario):\n" + text);
	}

	// ---- 5. Habilidades ---------------------------------------------------------------------------------------------

	private void abilities(ClientGameTestContext ctx, TestServerContext srv, List<Item> relics) {
		for (Item item : relics) {
			String id = id(item);
			reset(ctx, srv);
			place(srv, 0.5, 0.5, 0, 6);
			for (String x : List.of("-2.5", "0.5", "3.5")) {
				srv.runCommand("summon minecraft:husk " + x + " " + Y + " 6.5 {NoAI:1b,Rotation:[180f,0f],PersistenceRequired:1b}");
			}
			srv.runCommand("summon minecraft:cow -4.5 " + Y + " 3.5 {NoAI:1b}");
			boolean wornToo = srv.computeOnServer(s -> {
				ServerPlayer p = player(s);
				ItemStack stack = new ItemStack(item);
				p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, stack.copy());
				return equip(p, stack);
			});
			String text = ctx.computeOnClient(mc -> {
				StringBuilder sb = new StringBuilder();
				for (Component c : new ItemStack(item).getTooltipLines(Item.TooltipContext.of(mc.level), mc.player,
						TooltipFlag.NORMAL)) {
					sb.append(c.getString()).append(' ');
				}
				return sb.toString().toLowerCase(Locale.ROOT);
			});
			ctx.waitTicks(5);
			try {
				// Clic derecho mantenido 45 ticks y soltado (cubre los que se cargan: rayo, Gram, Mjölnir, dardo…).
				ctx.getInput().holdKey(o -> o.keyUse);
				ctx.waitTicks(20);
				shot(ctx, "hab_" + id + "_1cargando");
				ctx.waitTicks(25);
				ctx.getInput().releaseKey(o -> o.keyUse);
				ctx.waitTicks(4);
				shot(ctx, "hab_" + id + "_2soltar");
				ctx.waitTicks(16);
				shot(ctx, "hab_" + id + "_3despues");
				if (wornToo && id.equals("falcon_cloak")) {
					ctx.getInput().pressKey(RelicClientTicks.FALCON_KEY);
					ctx.waitTicks(20);
					shot(ctx, "hab_" + id + "_4tecla");
					ctx.getInput().pressKey(RelicClientTicks.FALCON_KEY);
					ctx.waitTicks(10);
				}
				if (wornToo && id.equals("hades_helm")) {
					// Agachado y quieto hasta volverse invisible; de frente no debe verse nada del jugador.
					ctx.getInput().holdKey(o -> o.keyShift);
					ctx.waitTicks(55);
					camera(ctx, CameraType.THIRD_PERSON_FRONT);
					ctx.waitTicks(2);
					shot(ctx, "hab_" + id + "_7invisible");
					camera(ctx, CameraType.FIRST_PERSON);
					ctx.getInput().releaseKey(o -> o.keyShift);
					ctx.waitTicks(4);
				}
				if (text.contains("sneak") || text.contains("crouch") || text.contains("agachado")) {
					srv.runCommand("ride @a dismount");
					srv.runOnServer(s -> {
						ServerPlayer p = player(s);
						p.getCooldowns().removeCooldown(p.getCooldowns().getCooldownGroup(new ItemStack(item)));
					});
					ctx.getInput().holdKey(o -> o.keyShift);
					ctx.waitTicks(3);
					ctx.getInput().pressKey(o -> o.keyUse);
					ctx.waitTicks(8);
					shot(ctx, "hab_" + id + "_5agachado");
					ctx.getInput().releaseKey(o -> o.keyShift);
					ctx.waitTicks(4);
				}
				// Un golpe al zombi del medio.
				srv.runCommand("ride @a dismount");
				place(srv, 0.5, 4.2, 0, 20);
				ctx.waitTicks(3);
				ctx.getInput().pressKey(o -> o.keyAttack);
				ctx.waitTicks(5);
				shot(ctx, "hab_" + id + "_6golpe");
				String state = srv.computeOnServer(s -> {
					ServerPlayer p = player(s);
					return String.format(Locale.ROOT, "vida %.1f/%.1f, recarga %s, montado %s, en mano %s",
							p.getHealth(), p.getMaxHealth(), p.getCooldowns().isOnCooldown(new ItemStack(item)),
							p.getVehicle() == null ? "no" : BuiltInRegistries.ENTITY_TYPE.getKey(p.getVehicle().getType()).getPath(),
							BuiltInRegistries.ITEM.getKey(p.getMainHandItem().getItem()).getPath());
				});
				log("habilidad " + id + (wornToo ? " (puesta)" : "") + ": " + state);
			} catch (Throwable t) {
				fail("habilidad " + id + ": EXCEPCIÓN " + t);
				t.printStackTrace(log);
				ctx.getInput().releaseKey(o -> o.keyUse);
				ctx.getInput().releaseKey(o -> o.keyShift);
			}
		}
	}
}
