package io.github.potatustudio.mythicrelics.network;

import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Servidor → cliente: un mob quedó petrificado por la cabeza de Medusa ({@code on}, por {@code ticks} ticks: el cliente
 * lo dibuja cada vez más agrietado) o volvió a la vida.
 */
public record PetrifyPayload(int entityId, boolean on, int ticks) implements CustomPacketPayload {
	public static final Type<PetrifyPayload> TYPE = new Type<>(MythicRelics.id("petrify"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PetrifyPayload> CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, PetrifyPayload::entityId, ByteBufCodecs.BOOL, PetrifyPayload::on, ByteBufCodecs.VAR_INT,
			PetrifyPayload::ticks, PetrifyPayload::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
