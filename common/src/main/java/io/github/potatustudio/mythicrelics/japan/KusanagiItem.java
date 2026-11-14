package io.github.potatustudio.mythicrelics.japan;

import io.github.potatustudio.mythicrelics.relic.Allies;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import io.github.potatustudio.mythicrelics.relic.RelicItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Kusanagi no Tsurugi, la «cortadora de hierba»: con clic derecho da un tajo de viento en arco que daña y empuja a los
 * mobs, siega la hierba y las flores y apaga el fuego, como hizo Yamato Takeru en el campo en llamas.
 */
public class KusanagiItem extends RelicItem {
	private static final double ARC_COS = Math.cos(Math.toRadians(60));

	public KusanagiItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.getCooldowns().isOnCooldown(stack)) {
			return InteractionResult.PASS;
		}
		if (!(level instanceof ServerLevel server)) {
			return InteractionResult.SUCCESS;
		}
		RelicCooldowns.start(player, stack, JapanRelics.KUSANAGI_COOLDOWN);
		Vec3 look = JapanRelics.flat(player.getLookAngle());
		Vec3 origin = player.position();
		int r = JapanRelics.KUSANAGI_SLASH_RANGE;
		for (LivingEntity target : Allies.enemiesAround(player, player.getBoundingBox().inflate(r, 2, r))) {
			Vec3 to = target.position().subtract(origin);
			if (to.horizontalDistance() > r || JapanRelics.flat(to).dot(look) < ARC_COS) {
				continue;
			}
			if (target.hurtServer(server, server.damageSources().playerAttack(player), JapanRelics.KUSANAGI_SLASH_DAMAGE)) {
				target.push(look.x * 1.2, 0.35, look.z * 1.2);
				target.needsSync = true;
			}
		}
		// Siega la hierba y apaga el fuego en el arco.
		BlockPos center = player.blockPosition();
		for (BlockPos pos : BlockPos.betweenClosed(center.offset(-r, -1, -r), center.offset(r, 2, r))) {
			Vec3 to = Vec3.atCenterOf(pos).subtract(origin);
			if (to.horizontalDistance() > r || JapanRelics.flat(to).dot(look) < ARC_COS) {
				continue;
			}
			BlockState state = server.getBlockState(pos);
			if (state.is(BlockTags.FIRE)) {
				server.removeBlock(pos, false);
			} else if (state.is(BlockTags.REPLACEABLE_BY_TREES) || state.is(BlockTags.FLOWERS) && state.canBeReplaced()) {
				server.destroyBlock(pos, true, player);
			}
		}
		for (int i = -3; i <= 3; i++) {
			double a = Math.atan2(look.z, look.x) + i * 0.3;
			server.sendParticles(ParticleTypes.SWEEP_ATTACK, origin.x + Math.cos(a) * 2.5, origin.y + 1.0, origin.z + Math.sin(a) * 2.5, 1,
					0.0, 0.0, 0.0, 0.0);
		}
		server.sendParticles(ParticleTypes.GUST, origin.x + look.x * 3, origin.y + 1, origin.z + look.z * 3, 2, 0.6, 0.2, 0.6, 0.0);
		RelicFx.sound(server, player, SoundEvents.PLAYER_ATTACK_SWEEP, 1.2f, 0.8f);
		server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), SoundSource.PLAYERS,
				0.6f, 1.2f);
		return InteractionResult.SUCCESS;
	}
}
