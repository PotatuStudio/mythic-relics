package io.github.potatustudio.mythicrelics.mixin;

import io.github.potatustudio.mythicrelics.china.ChinaRelics;
import io.github.potatustudio.mythicrelics.egypt.EgyptGods;
import io.github.potatustudio.mythicrelics.egypt.EgyptRelics;
import io.github.potatustudio.mythicrelics.greece.GreeceLegends;
import io.github.potatustudio.mythicrelics.greece.GreeceRelics;
import io.github.potatustudio.mythicrelics.norse.NorseSagas;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.DamageModifiers;
import io.github.potatustudio.mythicrelics.relic.WornRelics;
import io.github.potatustudio.mythicrelics.rome.RomeRelics;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Efectos de reliquias que Architectury no cubre con eventos:
 * <ul>
 *   <li>daño recibido: Anj (menos daño de no-muertos), Ra (más daño a no-muertos bajo el sol), Sekhmet (frenesí) y
 *       Bastet (caídas); Perseo (lo petrificado) y el Baño del Estigia (de frente y por la espalda);</li>
 *   <li>el Ojo de Horus impide la Ceguera y la Oscuridad;</li>
 *   <li>el renacer de Osiris, en la misma comprobación que el tótem;</li>
 *   <li>la égida de Atenea, al bloquear con escudo, y el Ancile, que devuelve los proyectiles bloqueados;</li>
 *   <li>la lámpara de Vesta impide congelarse; el Ancile protege a los aldeanos en una invasión;</li>
 *   <li>China: daño de las ruedas, la piedra, el arco de Hou Yi y el elixir; las ruedas pisan la lava corriendo;</li>
 *   <li>Grecia 3 y nórdicos 3: la piel de Nemea, la cólera de Aquiles al bloquear, Talos, Tyrfing y Dáinsleif (que no
 *       deja curarse).</li>
 * </ul>
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true)
	private float mythicrelics$relicDamage(float amount, ServerLevel level, DamageSource source) {
		return DamageModifiers.incoming((LivingEntity) (Object) this, source, amount);
	}

	/** Dáinsleif: sus heridas no sanan; el herido no se cura ni regenera mientras dure. */
	@Inject(method = "heal", at = @At("HEAD"), cancellable = true)
	private void mythicrelics$dainsleifWound(float amount, CallbackInfo ci) {
		if (NorseSagas.healBlocked((LivingEntity) (Object) this)) {
			ci.cancel();
		}
	}

	@Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
	private void mythicrelics$eyeOfHorusSight(MobEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
		if ((effect.is(MobEffects.BLINDNESS) || effect.is(MobEffects.DARKNESS))
				&& WornRelics.isWearing((LivingEntity) (Object) this, ModItems.EYE_OF_HORUS)) {
			cir.setReturnValue(false);
		}
	}

	/** Égida de Atenea: solo se llama cuando un escudo bloquea un golpe cuerpo a cuerpo (Player llama a super). */
	@Inject(method = "blockUsingItem", at = @At("HEAD"))
	private void mythicrelics$aegis(ServerLevel level, LivingEntity attacker, DamageSource source, float damage, boolean fullyBlocked,
			CallbackInfo ci) {
		GreeceRelics.aegisBlocked((LivingEntity) (Object) this, attacker);
	}

	/** Ancile: después de bloquear un proyectil, se lo devuelve a quien lo lanzó. */
	@Inject(method = "applyItemBlocking", at = @At("RETURN"))
	private void mythicrelics$ancile(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Float> cir) {
		RomeRelics.afterBlocking((LivingEntity) (Object) this, source, cir.getReturnValue());
		GreeceLegends.afterBlocking((LivingEntity) (Object) this, source, cir.getReturnValue());
	}

	/** Lámpara de Vesta: el fuego que no se apaga no te deja congelarte. */
	@Inject(method = "canFreeze", at = @At("HEAD"), cancellable = true)
	private void mythicrelics$vestaWarmth(CallbackInfoReturnable<Boolean> cir) {
		if (RomeRelics.warmedByVesta((LivingEntity) (Object) this)) {
			cir.setReturnValue(false);
		}
	}

	/** Ruedas de viento y fuego: corriendo, la lava se pisa como suelo. */
	@Inject(method = "canStandOnFluid", at = @At("HEAD"), cancellable = true)
	private void mythicrelics$wheelsOnLava(FluidState fluid, CallbackInfoReturnable<Boolean> cir) {
		if (fluid.is(FluidTags.LAVA) && ChinaRelics.wheelsOnLava((LivingEntity) (Object) this)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
	private void mythicrelics$osirisRebirth(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
		if (EgyptGods.osirisRebirth((LivingEntity) (Object) this, source)
				|| EgyptRelics.scarabSave((LivingEntity) (Object) this, source)) {
			cir.setReturnValue(true);
		}
	}
}
