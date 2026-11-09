package io.github.potatustudio.mythicrelics.network;

import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Cliente → servidor: el jugador hizo el doble salto de las sandalias de Hermes. No lleva datos. */
public record HermesJumpPayload() implements CustomPacketPayload {
	public static final HermesJumpPayload INSTANCE = new HermesJumpPayload();
	public static final Type<HermesJumpPayload> TYPE = new Type<>(MythicRelics.id("hermes_jump"));
	public static final StreamCodec<RegistryFriendlyByteBuf, HermesJumpPayload> CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
