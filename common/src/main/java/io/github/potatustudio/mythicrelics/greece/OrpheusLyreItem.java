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
 * duermen (no se mueven ni atacan). Tocar ocupa las manos y frena un poco (no como tensar un arco: ver
 * {@code use_effects} en ModItems). La melodía es el Epitafio de Seikilos (siglo I), la canción griega completa más
 * antigua que se conserva, leída de la piedra: C=la, O=si, K=do♯, I=re, Z=mi, Φ=sol, X=fa♯, Ɔ=mi grave; sin marca
 * dura un pulso, con raya dos y con raya y punto tres.
 */
public class OrpheusLyreItem extends RelicItem {
	private static final int USE_TICKS = 72000;
	/** Notas del bloque musical (0 = fa♯3 … 24 = fa♯5): mi4 10, fa♯4 12, sol4 13, la4 15, si4 17, do♯5 19, re5 20, mi5 22. */
	private static final int E4 = 10, FS = 12, G = 13, A = 15, B = 17, CS = 19, D = 20, E5 = 22;
	/** Seikilos: (nota, pulsos). «Ὅσον ζῇς φαίνου, μηδὲν ὅλως σὺ λυποῦ· πρὸς ὀλίγον ἐστὶ τὸ ζῆν, τὸ τέλος ὁ χρόνος ἀπαιτεῖ.» */
	private static final int[][] SEIKILOS = {
			{A, 1}, {E5, 2}, {E5, 3}, {CS, 1}, {D, 1}, {E5, 1}, {D, 3}, {CS, 2}, {D, 1}, {E5, 1}, {D, 1}, {CS, 1},
			{B, 1}, {A, 2}, {B, 1}, {G, 2},
			{A, 3}, {CS, 1}, {E5, 1}, {D, 1}, {CS, 1}, {D, 1}, {CS, 1}, {A, 3}, {B, 1}, {G, 3},
			{A, 2}, {CS, 1}, {B, 1}, {D, 1}, {E5, 1}, {CS, 2}, {A, 1}, {A, 2}, {A, 1}, {FS, 1}, {E4, 4}};
	/** Ticks por pulso (un poco más de cinco notas por segundo, tranquilo). */
	private static final int BEAT = 4;
	/** Tick (dentro de la vuelta) en que empieza cada nota, y largo de la vuelta con un silencio al final. */
	private static final int[] STARTS = new int[SEIKILOS.length];
	private static final int LOOP;

	static {
		int t = 0;
		for (int i = 0; i < SEIKILOS.length; i++) {
			STARTS[i] = t;
			t += SEIKILOS[i][1] * BEAT;
		}
		LOOP = t + 4 * BEAT;
	}

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
		int t = played % LOOP;
		for (int i = 0; i < SEIKILOS.length; i++) {
			if (STARTS[i] == t) {
				int note = SEIKILOS[i][0];
				RelicFx.sound(server, user, SoundEvents.NOTE_BLOCK_HARP.value(), 0.8f, (float) Math.pow(2.0, (note - 12) / 12.0));
				// La partícula de nota toma su color del tono, como en el bloque musical.
				server.sendParticles(ParticleTypes.NOTE, user.getX(), user.getY() + 2.2, user.getZ(), 0, note / 24.0, 0.0, 0.0, 1.0);
				break;
			}
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
