package io.github.potatustudio.mythicrelics.mixin;

import io.github.potatustudio.mythicrelics.egypt.EgyptGods;
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
 * Efectos de reliquias que Architectury no cubre con eventos:
 * <ul>
 *   <li>daño recibido: Anj (menos daño de no-muertos), Ra (más daño a no-muertos bajo el sol), Sekhmet (frenesí) y
 *       Bastet (caídas);</li>
 *   <li>el Ojo de Horus impide la Ceguera y la Oscuridad;</li>
 *   <li>el renacer de Osiris, en la misma comprobación que el tótem.</li>
 * </ul>
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true)
	private float mythicrelics$relicDamage(float amount, ServerLevel level, DamageSource source) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (source.getEntity() instanceof LivingEntity attacker && attacker.typeHolder().is(EntityTypeTags.UNDEAD)
				&& EgyptRelics.isWearing(self, ModItems.ANKH)) {
			amount *= 1.0f - EgyptRelics.ANKH_UNDEAD_REDUCTION;
		}
		return EgyptGods.modifyIncomingDamage(self, source, amount);
	}

	@Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
	private void mythicrelics$eyeOfHorusSight(MobEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
		if ((effect.is(MobEffects.BLINDNESS) || effect.is(MobEffects.DARKNESS))
				&& EgyptRelics.isWearing((LivingEntity) (Object) this, ModItems.EYE_OF_HORUS)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
	private void mythicrelics$osirisRebirth(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
		if (EgyptGods.osirisRebirth((LivingEntity) (Object) this, source)) {
			cir.setReturnValue(true);
		}
	}
}
