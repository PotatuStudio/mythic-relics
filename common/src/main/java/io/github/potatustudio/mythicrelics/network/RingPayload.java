package io.github.potatustudio.mythicrelics.network;

import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Cliente → servidor: la tecla del aro del universo (lanzarlo desde la muñeca). No lleva datos. */
public record RingPayload() implements CustomPacketPayload {
	public static final RingPayload INSTANCE = new RingPayload();
	public static final Type<RingPayload> TYPE = new Type<>(MythicRelics.id("universe_ring"));
	public static final StreamCodec<RegistryFriendlyByteBuf, RingPayload> CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
