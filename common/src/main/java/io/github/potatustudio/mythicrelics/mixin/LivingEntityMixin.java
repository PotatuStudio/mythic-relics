package io.github.potatustudio.mythicrelics.mixin;

import io.github.potatustudio.mythicrelics.egypt.EgyptGods;
import io.github.potatustudio.mythicrelics.egypt.EgyptRelics;
import io.github.potatustudio.mythicrelics.greece.GreeceHeroes;
import io.github.potatustudio.mythicrelics.greece.GreeceRelics;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Efectos de reliquias que Architectury no cubre con eventos:
 * <ul>
 *   <li>daño recibido: Anj (menos daño de no-muertos), Ra (más daño a no-muertos bajo el sol), Sekhmet (frenesí) y
 *       Bastet (caídas); Perseo (lo petrificado) y el Baño del Estigia (de frente y por la espalda);</li>
 *   <li>el Ojo de Horus impide la Ceguera y la Oscuridad;</li>
 *   <li>el renacer de Osiris, en la misma comprobación que el tótem;</li>
 *   <li>la égida de Atenea, al bloquear con escudo.</li>
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
		amount = EgyptGods.modifyIncomingDamage(self, source, amount);
		return GreeceHeroes.modifyIncomingDamage(self, source, GreeceRelics.modifyIncomingDamage(self, source, amount));
	}

	@Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
	private void mythicrelics$eyeOfHorusSight(MobEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
		if ((effect.is(MobEffects.BLINDNESS) || effect.is(MobEffects.DARKNESS))
				&& EgyptRelics.isWearing((LivingEntity) (Object) this, ModItems.EYE_OF_HORUS)) {
			cir.setReturnValue(false);
		}
	}

	/** Égida de Atenea: solo se llama cuando un escudo bloquea un golpe cuerpo a cuerpo (Player llama a super). */
	@Inject(method = "blockUsingItem", at = @At("HEAD"))
	private void mythicrelics$aegis(ServerLevel level, LivingEntity attacker, DamageSource source, float damage, boolean fullyBlocked,
			CallbackInfo ci) {
		GreeceRelics.aegisBlocked((LivingEntity) (Object) this, attacker);
	}

	@Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
	private void mythicrelics$osirisRebirth(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
		if (EgyptGods.osirisRebirth((LivingEntity) (Object) this, source)) {
			cir.setReturnValue(true);
		}
	}
}
