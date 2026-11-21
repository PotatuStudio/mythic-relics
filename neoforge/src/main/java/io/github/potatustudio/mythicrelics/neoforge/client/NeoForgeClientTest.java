package io.github.potatustudio.mythicrelics.neoforge.client;

import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.relic.WornRelics;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

/**
 * Prueba automática de cliente en NeoForge, el equivalente de {@code :fabric:runClientGameTest} (NeoForge no trae una):
 * con la variable de entorno {@code RELIC_TEST=1} abre el mundo {@code saves/relictest}, arma el tooltip de cada
 * reliquia, se pone cada accesorio con Curios (comprobando que {@link WornRelics} lo ve) y saca capturas de frente y de
 * espalda; al terminar cierra el juego. Sin la variable no hace nada. Salida en run/relic-test (registro.txt y capturas
 * en run/screenshots/relic-test). Las entradas son del juego, nunca del sistema.
 */
public final class NeoForgeClientTest {
	private static final String WORLD = "relictest";
	private static final int Y = 150;
	private static final boolean GALLERY = "1".equals(System.getenv("RELIC_GALLERY"));

	private final Deque<Step> steps = new ArrayDeque<>();
	private final List<String> failures = new ArrayList<>();
	private PrintWriter log;
	private int wait;
	/** Mientras el servidor hace algo largo (buscar terreno en la galería), los pasos esperan. */
	private volatile boolean busy;
	private boolean worldRequested;
	private int ticksInWorld;

	private record Step(int delay, Consumer<Minecraft> action) {
	}

	private NeoForgeClientTest() {
	}

	public static void initIfRequested() {
		if ("1".equals(System.getenv("RELIC_TEST"))) {
			NeoForgeClientTest test = new NeoForgeClientTest();
			NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> test.tick(Minecraft.getInstance()));
			MythicRelics.LOGGER.info("Prueba automática de NeoForge activada (RELIC_TEST=1)");
		}
	}

	private void tick(Minecraft mc) {
		if (mc.level == null || mc.player == null || mc.getSingleplayerServer() == null) {
			if (!worldRequested && mc.gui.overlay() == null && mc.gui.screen() != null) {
				worldRequested = true;
				mc.createWorldOpenFlows().openWorld(WORLD, () -> fail("no se pudo abrir el mundo " + WORLD));
			}
			return;
		}
		if (log == null) {
			start(mc);
		}
		if (++ticksInWorld < 100) {
			return;
		}
		if (busy) {
			return;
		}
		if (wait > 0) {
			wait--;
			return;
		}
		Step step = steps.poll();
		if (step == null) {
			return;
		}
		try {
			step.action().accept(mc);
		} catch (Throwable t) {
			fail("excepción: " + t);
			t.printStackTrace(log);
		}
		wait = step.delay();
	}

	private void start(Minecraft mc) {
		Path out = mc.gameDirectory.toPath().resolve("relic-test");
		try {
			Files.createDirectories(out);
			Files.createDirectories(mc.gameDirectory.toPath().resolve(Screenshot.SCREENSHOT_DIR).resolve("relic-test"));
			log = new PrintWriter(Files.newBufferedWriter(out.resolve("registro.txt"), StandardCharsets.UTF_8), true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		List<Item> relics = new ArrayList<>();
		for (Item item : BuiltInRegistries.ITEM) {
			if (BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(MythicRelics.MOD_ID)) {
				relics.add(item);
			}
		}
		log("reliquias: " + relics.size());
		if (GALLERY) {
			gallery(mc);
			return;
		}
		onServer(s -> {
			for (String c : List.of("gamerule advance_time false", "gamerule advance_weather false", "gamerule spawn_mobs false",
					"time set noon", "weather clear", "gamemode survival @a",
					"fill -12 " + (Y - 1) + " -12 12 " + (Y - 1) + " 12 minecraft:grass_block",
					"fill -12 " + Y + " -12 12 " + (Y + 8) + " 12 minecraft:air",
					"effect give @a minecraft:resistance infinite 4 true", "tp @a 0.5 " + Y + " 0.5 0 10")) {
				s.getCommands().performPrefixedCommand(s.createCommandSourceStack(), c);
			}
		});
		steps.add(new Step(20, m -> tooltips(m, relics)));
		for (Item item : relics) {
			String id = BuiltInRegistries.ITEM.getKey(item).getPath();
			boolean[] equipped = {false};
			steps.add(new Step(6, m -> onServer(s -> {
				ServerPlayer p = s.getPlayerList().getPlayers().getFirst();
				unequipAll(p);
				equipped[0] = equip(p, new ItemStack(item));
			})));
			steps.add(new Step(0, m -> {
				if (equipped[0]) {
					onServer(s -> check(WornRelics.isWearing(s.getPlayerList().getPlayers().getFirst(), item), "WornRelics ve " + id + " puesta"));
				}
			}));
			steps.add(new Step(4, m -> {
				if (equipped[0]) {
					m.options.setCameraType(CameraType.THIRD_PERSON_FRONT);
					alignBody(m);
				}
			}));
			steps.add(new Step(4, m -> {
				if (equipped[0]) {
					shot(m, "una_" + id + "_1frente");
					m.options.setCameraType(CameraType.THIRD_PERSON_BACK);
					alignBody(m);
				}
			}));
			steps.add(new Step(2, m -> {
				if (equipped[0]) {
					shot(m, "una_" + id + "_2espalda");
				}
			}));
		}
		steps.add(new Step(20, m -> {
			m.options.setCameraType(CameraType.FIRST_PERSON);
			onServer(s -> unequipAll(s.getPlayerList().getPlayers().getFirst()));
		}));
		steps.add(new Step(0, m -> {
			log(failures.isEmpty() ? "FIN: sin fallos" : "FIN: " + failures.size() + " fallos " + failures);
			log.close();
			m.stop();
			// En desarrollo, hilos de otros mods (Sodium, Iris) dejan colgado el apagado: si a los 30 s sigue vivo, se corta.
			Thread exit = new Thread(() -> {
				try {
					Thread.sleep(30_000);
				} catch (InterruptedException ignored) {
				}
				Runtime.getRuntime().halt(0);
			}, "relic-test-exit");
			exit.setDaemon(true);
			exit.start();
		}));
	}

	/** Una montura para la galería: id de la entidad, distancia y altura de la cámara, y los biomas donde se fotografía. */
	private record Mount(String id, double distance, double height, double lift, List<String> biomes) {
	}

	/** Dónde quedó la cámara de la montura en curso (lo calcula el servidor, lo usa el paso siguiente). */
	private volatile double[] spot;

	/**
	 * Modo galería ({@code RELIC_GALLERY=1}): sin interfaz y a media tarde, cada montura sola de tres cuartos de frente,
	 * en un bioma abierto que le calce (pradera o mar). Primero va la cámara y se espera a que cargue el terreno; la
	 * montura aparece justo antes de la foto, porque sin jinete vuelve a su mundo a los 3 s. Capturas en
	 * run/screenshots/relic-test/galeria_*.png.
	 */
	private void gallery(Minecraft mc) {
		if (!mc.gui.hud.isHidden()) {
			mc.gui.hud.toggle();   // como F1
		}
		onServer(s -> {
			for (String c : List.of("gamerule advance_time false", "gamerule advance_weather false", "gamerule spawn_mobs false",
					"time set 10500", "weather clear", "gamemode spectator @a")) {
				s.getCommands().performPrefixedCommand(s.createCommandSourceStack(), c);
			}
		});
		List<String> land = List.of("minecraft:plains", "minecraft:sunflower_plains", "minecraft:meadow", "minecraft:savanna");
		List<Mount> mounts = List.of(new Mount("sleipnir", 5.0, 1.4, 0, land), new Mount("pegasus", 4.2, 1.3, 0, land),
				new Mount("long_dragon", 7.0, 2.0, 1.5, land),
				new Mount("skidbladnir_boat", 6.0, 2.0, 0, List.of("minecraft:ocean", "minecraft:lukewarm_ocean", "minecraft:warm_ocean")));
		for (int i = 0; i < mounts.size(); i++) {
			Mount mount = mounts.get(i);
			int index = i;
			steps.add(new Step(200, m -> {
				busy = true;
				onServer(s -> {
					try {
						placeCamera(s, mount, index);
					} catch (Throwable t) {
						fail("galería " + mount.id() + ": " + t);
					} finally {
						busy = false;
					}
				});
			}));
			steps.add(new Step(12, m -> onServer(s -> summonMount(s, mount))));
			steps.add(new Step(5, m -> shot(m, "galeria_" + mount.id())));
		}
		steps.add(new Step(0, m -> {
			log("FIN galería");
			log.close();
			m.stop();
			Thread exit = new Thread(() -> {
				try {
					Thread.sleep(30_000);
				} catch (InterruptedException ignored) {
				}
				Runtime.getRuntime().halt(0);
			}, "relic-test-exit");
			exit.setDaemon(true);
			exit.start();
		}));
	}

	/** Busca el bioma (cada montura desde un punto distinto, para variar el paisaje) y deja la cámara mirando el lugar. */
	private void placeCamera(MinecraftServer s, Mount mount, int index) {
		ServerLevel level = s.overworld();
		s.getCommands().performPrefixedCommand(s.createCommandSourceStack(), "kill @e[type=!minecraft:player]");
		boolean water = mount.id().equals("skidbladnir_boat");
		// Las de tierra van todas al mismo prado junto al spawn, con un claro parejo y despejado; el barco, al agua abierta
		// más cercana.
		BlockPos from = new BlockPos(-16, 64, 16);
		Pair<BlockPos, Holder<Biome>> found = level.findClosestBiome3d(
				biome -> mount.biomes().stream().anyMatch(id -> biome.is(Identifier.parse(id))), from, 3000, 32, 64);
		BlockPos at = found != null ? found.getFirst() : from;
		BlockPos flat = water ? flatSpot(level, at, true, (int) Math.ceil(mount.distance()) + 2) : at;
		int x = flat.getX(), z = flat.getZ();
		level.getChunk(x >> 4, z >> 4);   // la altura del suelo solo es real con el chunk generado
		int ground = water ? flat.getY() : level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
		if (!water) {
			int r = (int) Math.ceil(mount.distance()) + 4;
			for (String c : List.of(
					"fill " + (x - r) + " " + (ground - 1) + " " + (z - r) + " " + (x + r) + " " + (ground - 1) + " " + (z + r) + " minecraft:grass_block",
					"fill " + (x - r) + " " + ground + " " + (z - r) + " " + (x + r) + " " + (ground + 12) + " " + (z + r) + " minecraft:air")) {
				s.getCommands().performPrefixedCommand(s.createCommandSourceStack(), c);
			}
		}
		double y = ground + mount.lift();
		double angle = Math.toRadians(35);
		double cx = x + 0.5 + Math.sin(angle) * mount.distance(), cz = z + 0.5 + Math.cos(angle) * mount.distance();
		double cy = y + mount.height();
		spot = new double[] {x + 0.5, y, z + 0.5};
		s.getCommands().performPrefixedCommand(s.createCommandSourceStack(), "tp @p " + cx + " " + cy + " " + cz + " facing "
				+ (x + 0.5) + " " + (y + mount.height() * 0.45) + " " + (z + 0.5));
		log("montura " + mount.id() + " en " + x + " " + y + " " + z + (found != null ? " (" + found.getSecond().getRegisteredName() + ")"
				: " (sin bioma)"));
	}

	/**
	 * Cerca de {@code at}, el primer lugar parejo (el suelo varía a lo más un bloque en un radio que incluye la cámara) y
	 * seco, o de agua abierta si {@code water}. Devuelve x, altura del suelo (o de la superficie del agua) y z.
	 */
	private static BlockPos flatSpot(ServerLevel level, BlockPos at, boolean water, int radius) {
		for (int ring = 0; ring <= 96; ring += 8) {
			for (int dx = -ring; dx <= ring; dx += 8) {
				for (int dz = -ring; dz <= ring; dz += 8) {
					if (Math.max(Math.abs(dx), Math.abs(dz)) != ring) {
						continue;
					}
					int x = at.getX() + dx, z = at.getZ() + dz;
					int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
					boolean ok = true;
					for (int sx = -radius; sx <= radius && ok; sx += 2) {
						for (int sz = -radius; sz <= radius && ok; sz += 2) {
							level.getChunk((x + sx) >> 4, (z + sz) >> 4);   // la altura solo es real con el chunk generado
							int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x + sx, z + sz);
							int floor = level.getHeight(Heightmap.Types.OCEAN_FLOOR, x + sx, z + sz);
							boolean wet = top > floor;
							ok = water ? wet && top - floor >= 2 : !wet;
							min = Math.min(min, top);
							max = Math.max(max, top);
						}
					}
					if (ok && max - min <= 1) {
						return new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z);
					}
				}
			}
		}
		return new BlockPos(at.getX(), level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, at.getX(), at.getZ()), at.getZ());
	}

	private void summonMount(MinecraftServer s, Mount mount) {
		double[] p = spot;
		// Rotation 0: mira al sur (+z), hacia la cámara.
		s.getCommands().performPrefixedCommand(s.createCommandSourceStack(), "summon " + MythicRelics.MOD_ID + ":" + mount.id() + " "
				+ p[0] + " " + p[1] + " " + p[2] + " {Rotation:[0f,0f]}");
	}

	private void tooltips(Minecraft mc, List<Item> relics) {
		for (Item item : relics) {
			String id = BuiltInRegistries.ITEM.getKey(item).getPath();
			try {
				int lines = new ItemStack(item).getTooltipLines(Item.TooltipContext.of(mc.level), mc.player, TooltipFlag.NORMAL).size();
				log("tooltip " + id + ": " + lines + " líneas");
			} catch (Throwable t) {
				fail("tooltip " + id + ": " + t);
			}
		}
	}

	private static void onServer(Consumer<MinecraftServer> action) {
		MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
		if (server != null) {
			server.execute(() -> action.accept(server));
		}
	}

	/** Primer espacio de Curios cuya etiqueta (curios:&lt;espacio&gt;) incluye el ítem. */
	private static boolean equip(ServerPlayer p, ItemStack stack) {
		ICuriosItemHandler inv = CuriosApi.getCuriosInventory(p).orElse(null);
		if (inv == null) {
			return false;
		}
		for (Map.Entry<String, ICurioStacksHandler> slot : inv.getCurios().entrySet()) {
			TagKey<Item> tag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("curios", slot.getKey()));
			if (stack.is(tag) && slot.getValue().getSlots() > 0) {
				inv.setEquippedCurio(slot.getKey(), 0, stack.copy());
				return true;
			}
		}
		return false;
	}

	private static void unequipAll(ServerPlayer p) {
		CuriosApi.getCuriosInventory(p).ifPresent(inv -> inv.getCurios().forEach((id, handler) -> {
			for (int i = 0; i < handler.getSlots(); i++) {
				inv.setEquippedCurio(id, i, ItemStack.EMPTY);
			}
		}));
	}

	/** Tras un tp el cuerpo queda girado respecto de la cabeza: se alinea con la mirada antes de cada captura. */
	private static void alignBody(Minecraft mc) {
		if (mc.player != null) {
			float yaw = mc.player.getYRot();
			mc.player.yBodyRot = yaw;
			mc.player.yBodyRotO = yaw;
			mc.player.yHeadRot = yaw;
			mc.player.yHeadRotO = yaw;
		}
	}

	private void shot(Minecraft mc, String name) {
		alignBody(mc);
		Screenshot.grab(mc.gameDirectory, "relic-test/" + name + ".png", mc.gameRenderer.mainRenderTarget(), 1, message -> {
		});
	}

	private synchronized void check(boolean ok, String what) {
		if (ok) {
			log("OK " + what);
		} else {
			fail(what);
		}
	}

	private synchronized void fail(String what) {
		failures.add(what);
		log("FALLO " + what);
	}

	private synchronized void log(String s) {
		if (log != null) {
			log.println(s);
		} else {
			MythicRelics.LOGGER.info("[prueba] {}", s);
		}
	}
}
