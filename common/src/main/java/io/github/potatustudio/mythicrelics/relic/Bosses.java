package io.github.potatustudio.mythicrelics.relic;

import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

/**
 * Jefes: lo que las reliquias no pueden petrificar, decapitar, encadenar ni enemistar. Es el tag
 * {@code mythicrelics:bosses} (dragón, wither, guardián anciano y warden, más {@code #c:bosses} de otros mods).
 */
public final class Bosses {
	public static final TagKey<EntityType<?>> TAG = TagKey.create(Registries.ENTITY_TYPE, MythicRelics.id("bosses"));

	private Bosses() {
	}

	public static boolean is(Entity entity) {
		return entity.typeHolder().is(TAG);
	}
}
