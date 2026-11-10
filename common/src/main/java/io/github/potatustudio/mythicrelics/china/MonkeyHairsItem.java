package io.github.potatustudio.mythicrelics.china;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.minion.Minions;
import io.github.potatustudio.mythicrelics.registry.ModEntities;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Pelos del Rey Mono: Sun Wukong se arrancaba pelos, los soplaba, gritaba «¡Cambia!» y cada uno se volvía un mono
 * guerrero igual a él. Aquí salen cuatro entre nubecitas, pelean contra lo que tú golpeas (o lo que te golpea) y al
 * terminar su tiempo vuelven a ser pelos.
 */
public class MonkeyHairsItem extends RelicItem {
	public MonkeyHairsItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player.getCooldowns().isOnCooldown(player.getItemInHand(hand))) {
			return InteractionResult.PASS;
		}
		if (!(level instanceof ServerLevel server) || !(player instanceof ServerPlayer serverPlayer)) {
			return InteractionResult.SUCCESS;
		}
		if (!RelicCooldowns.tryStart(serverPlayer, "monkey_king_hairs", ChinaRelics.HAIRS_COOLDOWN, BuiltInRegistries.ITEM.getKey(this))) {
			return InteractionResult.PASS;
		}
		server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BREEZE_WIND_CHARGE_BURST.value(),
				SoundSource.PLAYERS, 0.8f, 1.4f);
		for (int i = 0; i < ChinaRelics.HAIRS_MONKEYS; i++) {
			double angle = player.getYRot() * Math.PI / 180.0 + Math.PI * 2 * i / ChinaRelics.HAIRS_MONKEYS;
			double x = player.getX() - Math.sin(angle) * 1.8;
			double z = player.getZ() + Math.cos(angle) * 1.8;
			Mob monkey = Minions.summon(server, player, ModEntities.MONKEY_WARRIOR.get(), x, player.getY(), z, ChinaRelics.HAIRS_TICKS,
					Component.translatable("entity.mythicrelics.monkey_warrior"));
			if (monkey != null) {
				monkey.setYRot(player.getYRot());
				server.sendParticles(ParticleTypes.CLOUD, x, player.getY() + 0.8, z, 16, 0.3, 0.5, 0.3, 0.03);
				server.sendParticles(ParticleTypes.WAX_ON, x, player.getY() + 1.0, z, 6, 0.3, 0.4, 0.3, 0.05);
			}
		}
		player.sendOverlayMessage(Component.translatable("message.mythicrelics.monkey_king_hairs").withStyle(ChatFormatting.GOLD));
		return InteractionResult.SUCCESS;
	}
}
