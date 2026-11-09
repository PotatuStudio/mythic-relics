package io.github.potatustudio.mythicrelics.mixin;

import io.github.potatustudio.mythicrelics.egypt.EgyptGods;
import io.github.potatustudio.mythicrelics.rome.RomeRelics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mayal de Osiris: guarda la carga del ataque al empezar (después se reinicia) y, al terminar, hace el golpe giratorio.
 * Paleta de Thot: más experiencia con la paleta en la mano secundaria. Lámpara de Vesta: menos hambre.
 */
@Mixin(Player.class)
public abstract class PlayerMixin {
	@Unique
	private float mythicrelics$attackStrength;

	@Inject(method = "attack", at = @At("HEAD"))
	private void mythicrelics$rememberStrength(Entity target, CallbackInfo ci) {
		mythicrelics$attackStrength = ((Player) (Object) this).getAttackStrengthScale(0.5f);
	}

	@Inject(method = "attack", at = @At("TAIL"))
	private void mythicrelics$flailSweep(Entity target, CallbackInfo ci) {
		EgyptGods.flailSweep((Player) (Object) this, target, mythicrelics$attackStrength);
	}

	@ModifyVariable(method = "causeFoodExhaustion", at = @At("HEAD"), argsOnly = true)
	private float mythicrelics$vestaHunger(float amount) {
		return RomeRelics.modifyExhaustion((Player) (Object) this, amount);
	}

	@ModifyVariable(method = "giveExperiencePoints", at = @At("HEAD"), argsOnly = true)
	private int mythicrelics$thothExperience(int amount) {
		return EgyptGods.thothExperience((Player) (Object) this, amount);
	}
}
