package io.github.potatustudio.mythicrelics.network;

import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Cliente → servidor: el jugador planea con las alas de Ícaro ({@code flap} falso, cada pocos ticks) o aletea
 * ({@code flap} verdadero). El movimiento lo decide el cliente; el servidor reinicia la caída y hace sonar las alas.
 */
public record IcarusPayload(boolean flap) implements CustomPacketPayload {
	public static final Type<IcarusPayload> TYPE = new Type<>(MythicRelics.id("icarus"));
	public static final StreamCodec<RegistryFriendlyByteBuf, IcarusPayload> CODEC =
			ByteBufCodecs.BOOL.map(IcarusPayload::new, IcarusPayload::flap).cast();

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
