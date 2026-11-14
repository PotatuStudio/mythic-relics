package io.github.potatustudio.mythicrelics.greece;

import io.github.potatustudio.mythicrelics.relic.Bosses;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import io.github.potatustudio.mythicrelics.relic.RelicItem;
import io.github.potatustudio.mythicrelics.relic.TargetVeto;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

/**
 * Lira de Orfeo: se toca manteniendo clic derecho. Mientras suena, los animales cercanos te siguen y los hostiles se
 * duermen (no se mueven ni atacan). Tocar ocupa las manos y frena, como tensar un arco: no se puede atacar a la vez.
 */
public class OrpheusLyreItem extends RelicItem {
	private static final int USE_TICKS = 72000;
	private static final float[] NOTES = {0.5f, 0.63f, 0.75f, 0.84f, 1.0f, 1.26f, 1.5f};

	public OrpheusLyreItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		player.startUsingItem(hand);
		return InteractionResult.CONSUME;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return USE_TICKS;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.TOOT_HORN;
	}

	@Override
	public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remaining) {
		if (!(level instanceof ServerLevel server)) {
			return;
		}
		int played = USE_TICKS - remaining;
		if (played % 5 == 0) {
			float pitch = NOTES[server.getRandom().nextInt(NOTES.length)];
			RelicFx.sound(server, user, SoundEvents.NOTE_BLOCK_HARP.value(), 0.7f, pitch);
			server.sendParticles(ParticleTypes.NOTE, user.getX(), user.getY() + 2.2, user.getZ(), 1, 0.4, 0.1, 0.4, 1.0);
		}
		if (played % 10 != 0) {
			return;
		}
		for (Animal animal : server.getEntitiesOfClass(Animal.class, user.getBoundingBox().inflate(GreeceRelics.ORPHEUS_ANIMALS))) {
			if (animal.distanceToSqr(user) > 9) {
				animal.getNavigation().moveTo(user, 1.0);
			}
		}
		for (Mob mob : server.getEntitiesOfClass(Mob.class, user.getBoundingBox().inflate(GreeceRelics.ORPHEUS_HOSTILES),
				m -> m instanceof Enemy && !Bosses.is(m))) {
			// Dormido: no se mueve ni ataca mientras suena la lira.
			// Dormido mientras siga sonando (TargetVeto le niega cualquier objetivo).
			TargetVeto.lull(mob, server.getGameTime() + 25);
			mob.setTarget(null);
			mob.getNavigation().stop();
			mob.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 20, 9, false, false), user);
			mob.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 9, false, false), user);
			if (played % 40 == 0) {
				server.sendParticles(ParticleTypes.NOTE, mob.getX(), mob.getY() + mob.getBbHeight() + 0.3, mob.getZ(), 1, 0.1, 0.1, 0.1, 0.5);
			}
		}
	}
}
