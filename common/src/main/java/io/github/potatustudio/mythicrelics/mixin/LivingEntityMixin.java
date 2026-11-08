package io.github.potatustudio.mythicrelics.mixin;

import io.github.potatustudio.mythicrelics.egypt.EgyptRelics;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Efectos de reliquias que Architectury no cubre con eventos: el Anj reduce el daño de los no-muertos y el Ojo de
 * Horus impide la Ceguera y la Oscuridad.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true)
	private float mythicrelics$ankhUndeadReduction(float amount, ServerLevel level, DamageSource source) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (source.getEntity() instanceof LivingEntity attacker && attacker.typeHolder().is(EntityTypeTags.UNDEAD)
				&& EgyptRelics.isWearing(self, ModItems.ANKH)) {
			return amount * (1.0f - EgyptRelics.ANKH_UNDEAD_REDUCTION);
		}
		return amount;
	}

	@Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
	private void mythicrelics$eyeOfHorusSight(MobEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
		if ((effect.is(MobEffects.BLINDNESS) || effect.is(MobEffects.DARKNESS))
				&& EgyptRelics.isWearing((LivingEntity) (Object) this, ModItems.EYE_OF_HORUS)) {
			cir.setReturnValue(false);
		}
	}
}
