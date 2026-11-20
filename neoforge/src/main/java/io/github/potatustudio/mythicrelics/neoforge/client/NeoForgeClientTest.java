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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
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

	private final Deque<Step> steps = new ArrayDeque<>();
	private final List<String> failures = new ArrayList<>();
	private PrintWriter log;
	private int wait;
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
