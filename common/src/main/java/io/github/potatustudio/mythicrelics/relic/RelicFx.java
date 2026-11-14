package io.github.potatustudio.mythicrelics.relic;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Sonidos y avisos de las reliquias. */
public final class RelicFx {
	private RelicFx() {
	}

	/** Sonido de jugador en la posición de la entidad, para todos los que estén cerca. */
	public static void sound(Level level, Entity at, SoundEvent sound, float volume, float pitch) {
		level.playSound(null, at.getX(), at.getY(), at.getZ(), sound, SoundSource.PLAYERS, volume, pitch);
	}

	public static void sound(Level level, Entity at, Holder<SoundEvent> sound, float volume, float pitch) {
		level.playSound(null, at.getX(), at.getY(), at.getZ(), sound, SoundSource.PLAYERS, volume, pitch);
	}

	/** Aviso sobre la barra (nunca en el chat): el texto {@code message.mythicrelics.<key>} con un color. */
	public static void notify(Player player, String key, ChatFormatting color, Object... args) {
		player.sendOverlayMessage(Component.translatable("message.mythicrelics." + key, args).withStyle(color));
	}
}
