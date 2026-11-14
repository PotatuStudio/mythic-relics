package io.github.potatustudio.mythicrelics.network;

import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Cliente → servidor: doble toque de salto en el aire con el manto de plumas de halcón puesto. No lleva datos. */
public record FalconPayload() implements CustomPacketPayload {
	public static final FalconPayload INSTANCE = new FalconPayload();
	public static final Type<FalconPayload> TYPE = new Type<>(MythicRelics.id("falcon_form"));
	public static final StreamCodec<RegistryFriendlyByteBuf, FalconPayload> CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
