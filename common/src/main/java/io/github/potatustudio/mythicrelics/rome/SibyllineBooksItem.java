package io.github.potatustudio.mythicrelics.rome;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;

/**
 * Libros sibilinos: consultarlos cuesta experiencia. Los hostiles cercanos brillan y la sibila dice hacia dónde y a
 * cuánto queda la estructura más cercana de una lista corta (solo en el Overworld; buscar estructuras es caro).
 */
public class SibyllineBooksItem extends RelicItem {
	private record Sight(TagKey<Structure> tag, String name, net.minecraft.core.Holder<net.minecraft.world.level.saveddata.maps.MapDecorationType> icon) {
	}

	/** Rastro de letras hacia la estructura profetizada: destino y tick en que se apaga. */
	private record Guide(BlockPos target, long until) {
	}

	private static final java.util.Map<java.util.UUID, Guide> GUIDES = new java.util.concurrent.ConcurrentHashMap<>();
	private static final int GUIDE_TICKS = 300;

	private static final List<Sight> SIGHTS = List.of(
			new Sight(StructureTags.VILLAGE, "village", net.minecraft.world.level.saveddata.maps.MapDecorationTypes.PLAINS_VILLAGE),
			new Sight(StructureTags.RUINED_PORTAL, "ruined_portal", net.minecraft.world.level.saveddata.maps.MapDecorationTypes.RED_X),
			new Sight(StructureTags.ON_DESERT_PYRAMID_MAPS, "desert_pyramid", net.minecraft.world.level.saveddata.maps.MapDecorationTypes.DESERT_PYRAMID),
			new Sight(StructureTags.ON_JUNGLE_PYRAMID_MAPS, "jungle_temple", net.minecraft.world.level.saveddata.maps.MapDecorationTypes.JUNGLE_TEMPLE),
			new Sight(StructureTags.ON_WOODLAND_MANSION_MAPS, "mansion", net.minecraft.world.level.saveddata.maps.MapDecorationTypes.WOODLAND_MANSION),
			new Sight(StructureTags.EYE_OF_ENDER_LOCATED, "stronghold", net.minecraft.world.level.saveddata.maps.MapDecorationTypes.TARGET_X));
	private static final int SEARCH_CHUNKS = 32;
	private static final String[] DIRECTIONS = {"south", "southwest", "west", "northwest", "north", "northeast", "east", "southeast"};

	public SibyllineBooksItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player.getCooldowns().isOnCooldown(player.getItemInHand(hand))) {
			return InteractionResult.PASS;
		}
		if (!(player instanceof ServerPlayer serverPlayer) || !(level instanceof ServerLevel server)) {
			return InteractionResult.SUCCESS;
		}
		if (!player.isCreative() && player.experienceLevel < RomeRelics.SIBYL_LEVELS) {
			serverPlayer.sendOverlayMessage(Component.translatable("message.mythicrelics.sibyl_price", RomeRelics.SIBYL_LEVELS)
					.withStyle(ChatFormatting.GRAY));
			return InteractionResult.PASS;
		}
		if (!RelicCooldowns.tryStart(serverPlayer, "sibylline_books", RomeRelics.SIBYL_COOLDOWN, BuiltInRegistries.ITEM.getKey(this))) {
			return InteractionResult.PASS;
		}
		if (!player.isCreative()) {
			player.giveExperienceLevels(-RomeRelics.SIBYL_LEVELS);
		}
		for (LivingEntity mob : server.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(RomeRelics.SIBYL_RADIUS),
				e -> e instanceof Enemy && e.isAlive())) {
			mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, RomeRelics.SIBYL_GLOW_TICKS, 0, false, false));
		}
		server.sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 1.5, player.getZ(), 40, 0.5, 0.5, 0.5, 0.5);
		server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0f, 0.8f);
		prophecy(server, serverPlayer);
		return InteractionResult.SUCCESS;
	}

	private static void prophecy(ServerLevel level, ServerPlayer player) {
		if (level.dimension() != Level.OVERWORLD) {
			player.sendSystemMessage(Component.translatable("message.mythicrelics.sibyl_nothing").withStyle(ChatFormatting.DARK_PURPLE));
			return;
		}
		BlockPos here = player.blockPosition();
		BlockPos best = null;
		Sight bestSight = null;
		for (Sight sight : SIGHTS) {
			// Como los mapas del cartógrafo: lo ya profetizado no se repite, la siguiente consulta busca otra.
			BlockPos found = level.findNearestMapStructure(sight.tag(), here, SEARCH_CHUNKS, true);
			if (found != null && (best == null || found.distSqr(here) < best.distSqr(here))) {
				best = found;
				bestSight = sight;
			}
		}
		if (best == null) {
			player.sendSystemMessage(Component.translatable("message.mythicrelics.sibyl_nothing").withStyle(ChatFormatting.DARK_PURPLE));
			return;
		}
		double dx = best.getX() - here.getX();
		double dz = best.getZ() - here.getZ();
		int distance = (int) Math.round(Math.sqrt(dx * dx + dz * dz));
		// Ángulo desde el sur (+Z) girando hacia el oeste, como el yaw de Minecraft.
		double angle = Math.toDegrees(Math.atan2(-dx, dz));
		int sector = (int) Math.floorMod(Math.round(angle / 45.0), 8);
		player.sendSystemMessage(Component.translatable("message.mythicrelics.sibyl_sees",
				Component.translatable("structure.mythicrelics." + bestSight.name()),
				Component.translatable("direction.mythicrelics." + DIRECTIONS[sector]), distance).withStyle(ChatFormatting.DARK_PURPLE));
		// El mapa de la profecía, ya dibujado, con la estructura marcada.
		ItemStack map = net.minecraft.world.item.MapItem.create(level, best.getX(), best.getZ(), (byte) 2, true, true);
		net.minecraft.world.item.MapItem.renderBiomePreviewMap(level, map);
		net.minecraft.world.level.saveddata.maps.MapItemSavedData.addTargetDecoration(map, best, "+", bestSight.icon());
		map.set(net.minecraft.core.component.DataComponents.ITEM_NAME, Component.translatable("item.mythicrelics.sibyl_map",
				Component.translatable("structure.mythicrelics." + bestSight.name())));
		if (!player.getInventory().add(map)) {
			player.drop(map, false, net.minecraft.util.Prediction.SERVER_ONLY);
		}
		GUIDES.put(player.getUUID(), new Guide(best, level.getGameTime() + GUIDE_TICKS));
	}

	/** Las letras de la sibila: un rastro de runas encantadas que sale de ti hacia la estructura, solo para ti. */
	public static void init() {
		dev.architectury.event.events.common.TickEvent.SERVER_POST.register(server -> {
			if (server.getTickCount() % 4 != 0 || GUIDES.isEmpty()) {
				return;
			}
			long now = server.overworld().getGameTime();
			GUIDES.entrySet().removeIf(e -> e.getValue().until() <= now || server.getPlayerList().getPlayer(e.getKey()) == null);
			GUIDES.forEach((id, guide) -> {
				ServerPlayer player = server.getPlayerList().getPlayer(id);
				if (player == null || player.level().dimension() != Level.OVERWORLD) {
					return;
				}
				net.minecraft.world.phys.Vec3 from = player.position().add(0, 1.2, 0);
				net.minecraft.world.phys.Vec3 dir = new net.minecraft.world.phys.Vec3(guide.target().getX() + 0.5 - from.x, 0,
						guide.target().getZ() + 0.5 - from.z);
				if (dir.lengthSqr() < 16) {
					return;
				}
				dir = dir.normalize();
				for (int i = 1; i <= 8; i++) {
					net.minecraft.world.phys.Vec3 p = from.add(dir.scale(i * 1.2)).add(0, Math.sin((now / 4.0 + i) * 0.8) * 0.15, 0);
					player.level().sendParticles(player, ParticleTypes.ENCHANT, true, false, p.x, p.y, p.z, 2, 0.05, 0.05, 0.05, 0.0);
				}
			});
		});
	}

	// Ciclo de vida: qué se olvida al desconectarse un jugador y al cerrar el mundo (relic/ServerState).
	static {
		io.github.potatustudio.mythicrelics.relic.ServerState.perPlayer(GUIDES);
	}
}
