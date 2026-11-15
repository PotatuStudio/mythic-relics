package io.github.potatustudio.mythicrelics.client;

import com.mojang.serialization.MapCodec;
import io.github.potatustudio.mythicrelics.MythicRelics;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * Propiedad de modelo {@code mythicrelics:icarus_pose}: qué pose tienen las alas de Ícaro puestas, según cómo se mueve
 * quien las lleva (sirve igual para el jugador local y para los demás, porque solo mira su posición tick a tick).
 * <p>
 * Las poses son una grilla de ángulos (cuánto sube el ala, {@code lift}, y cuánto se echa atrás, {@code back}) generada
 * por worn_models.py (ICARUS_LIFTS e ICARUS_BACKS; si cambian allá, hay que cambiar {@link #LIFT_MIN},
 * {@link #LIFT_STEP}, {@link #LIFT_COUNT} y {@link #BACKS}). El índice de una pose es {@code iLift * BACKS.length + iBack}
 * y items/icarus_wings.json la elige con un range_dispatch.
 * <p>
 * Cada estado tiene una pose objetivo (en el suelo, el agua, trepando o montado: plegadas; subiendo: aleteo continuo;
 * bajando despacio: abiertas, planeando; cayendo rápido: echadas atrás, en picada). La pose base y la fuerza del aleteo se
 * acercan al objetivo con el tiempo real (suavizado exponencial, {@link #SMOOTHING} s), guardadas por entidad, y encima
 * se suma el aleteo, un seno de {@link #FLAP_HZ} Hz. Así las transiciones no saltan y el aleteo entra y sale de a poco.
 * Cada loader la registra (Fabric en el ID_MAPPER del juego, NeoForge con su evento).
 */
public record IcarusPose() implements RangeSelectItemModelProperty {
	public static final Identifier ID = MythicRelics.id("icarus_pose");
	public static final MapCodec<IcarusPose> MAP_CODEC = MapCodec.unit(new IcarusPose());

	/** La grilla de worn_models.py: lift = LIFT_MIN + i * LIFT_STEP (i < LIFT_COUNT) y los valores de back. */
	private static final float LIFT_MIN = -62, LIFT_STEP = 6;
	private static final int LIFT_COUNT = 19;
	private static final float[] BACKS = {6, 12, 22, 38, 58};

	/** Aleteos por segundo y amplitud del aleteo (grados de lift alrededor del centro, y de back). */
	private static final float FLAP_HZ = 2.2f, FLAP_LIFT = 37, FLAP_BACK = 3;
	/** Constante de tiempo del suavizado: en ese tiempo recorre ~63 % del camino al objetivo (~95 % en el triple). */
	private static final float SMOOTHING = 0.12f;

	private static final Map<LivingEntity, State> STATES = new WeakHashMap<>();

	/** Pose actual (suavizada) de las alas de una entidad. */
	private static final class State {
		float lift, back, flap, phase;
		long time;
	}

	@Override
	public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
		LivingEntity entity = owner == null ? null : owner.asLivingEntity();
		if (entity == null) {
			return index(-62, 22);
		}
		// Objetivo según el estado: lift y back de la pose base, y cuánto aletea (0 a 1) encima de ella.
		float lift, back, flap;
		if (entity.onGround() || entity.isInWater() || entity.onClimbable() || entity.isPassenger()) {
			lift = -62;
			back = 22;
			flap = 0;
		} else {
			double dy = entity.getY() - entity.yo;
			if (dy > 0.05) {          // subiendo: aleteo
				lift = 9;
				back = 12;
				flap = 1;
			} else if (dy > -0.35) {  // planeo
				lift = 6;
				back = 6;
				flap = 0;
			} else {                  // picada
				lift = 34;
				back = 58;
				flap = 0;
			}
		}

		long now = System.nanoTime();
		State s = STATES.get(entity);
		if (s == null) {
			s = new State();
			s.lift = lift;
			s.back = back;
			s.flap = flap;
			s.time = now;
			STATES.put(entity, s);
		}
		// Segundos desde la última vez (acotado: tras una pausa o si deja de verse, no da un salto raro).
		float dt = Math.min((now - s.time) / 1.0e9f, 0.1f);
		s.time = now;
		float k = 1 - (float) Math.exp(-dt / SMOOTHING);
		s.lift += (lift - s.lift) * k;
		s.back += (back - s.back) * k;
		s.flap += (flap - s.flap) * k;
		s.phase = (s.phase + dt * FLAP_HZ * Mth.TWO_PI) % Mth.TWO_PI;

		return index(s.lift + s.flap * FLAP_LIFT * Mth.sin(s.phase), s.back + s.flap * FLAP_BACK * Mth.cos(s.phase));
	}

	/** Índice de la pose de la grilla más cercana a (lift, back). */
	private static int index(float lift, float back) {
		int i = Mth.clamp(Math.round((lift - LIFT_MIN) / LIFT_STEP), 0, LIFT_COUNT - 1);
		int j = 0;
		for (int b = 1; b < BACKS.length; b++) {
			if (Math.abs(BACKS[b] - back) < Math.abs(BACKS[j] - back)) {
				j = b;
			}
		}
		return i * BACKS.length + j;
	}

	@Override
	public MapCodec<IcarusPose> type() {
		return MAP_CODEC;
	}
}
