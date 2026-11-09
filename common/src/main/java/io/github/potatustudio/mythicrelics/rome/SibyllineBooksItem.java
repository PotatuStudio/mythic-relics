package io.github.potatustudio.mythicrelics.rome;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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
	private record Sight(TagKey<Structure> tag, String name) {
	}

	private static final List<Sight> SIGHTS = List.of(new Sight(StructureTags.VILLAGE, "village"),
			new Sight(StructureTags.RUINED_PORTAL, "ruined_portal"), new Sight(StructureTags.ON_DESERT_PYRAMID_MAPS, "desert_pyramid"),
			new Sight(StructureTags.ON_JUNGLE_PYRAMID_MAPS, "jungle_temple"), new Sight(StructureTags.ON_WOODLAND_MANSION_MAPS, "mansion"),
			new Sight(StructureTags.EYE_OF_ENDER_LOCATED, "stronghold"));
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
			BlockPos found = level.findNearestMapStructure(sight.tag(), here, SEARCH_CHUNKS, false);
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
	}
}
