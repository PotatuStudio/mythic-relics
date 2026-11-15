package io.github.potatustudio.mythicrelics.client;

import com.mojang.serialization.MapCodec;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.entity.FreyrSword;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * Propiedad de modelo {@code mythicrelics:freyr_drawn}: si la espada de Freyr del que la lleva está afuera peleando. Con
 * ella items/freyr_sword.json deja la vaina vacía en la espalda (freyr_sword_sheath_worn) en vez de la espada envainada.
 * Cada loader la registra (Fabric en el ID_MAPPER del juego, NeoForge con su evento).
 */
public record FreyrDrawn() implements ConditionalItemModelProperty {
	public static final Identifier ID = MythicRelics.id("freyr_drawn");
	public static final MapCodec<FreyrDrawn> MAP_CODEC = MapCodec.unit(new FreyrDrawn());

	@Override
	public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed,
			ItemDisplayContext context) {
		if (owner == null) {
			return false;
		}
		int id = owner.getId();
		// La espada nunca se aleja mucho del dueño (FreyrSword: radio de combate de 12 bloques).
		return !owner.level().getEntitiesOfClass(FreyrSword.class, owner.getBoundingBox().inflate(24.0),
				sword -> sword.ownerId() == id && !sword.isRemoved()).isEmpty();
	}

	@Override
	public MapCodec<FreyrDrawn> type() {
		return MAP_CODEC;
	}
}
