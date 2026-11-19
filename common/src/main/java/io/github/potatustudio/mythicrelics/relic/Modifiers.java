package io.github.potatustudio.mythicrelics.relic;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * Modificadores de atributo de las reliquias, transitorios (no se guardan con la entidad: el tick los vuelve a poner).
 * Solo toca el atributo si cambia, para no marcarlo sucio ni reenviarlo al cliente en cada tick.
 */
public final class Modifiers {
	private Modifiers() {
	}

	/** Deja el modificador {@code id} en {@code amount} (0 lo quita). */
	public static void set(LivingEntity entity, Holder<Attribute> attribute, Identifier id, double amount,
			AttributeModifier.Operation operation) {
		AttributeInstance instance = entity.getAttribute(attribute);
		if (instance == null) {
			return;
		}
		AttributeModifier current = instance.getModifier(id);
		if (current != null && current.amount() == amount && current.operation() == operation) {
			return;
		}
		if (current != null) {
			instance.removeModifier(id);
		}
		if (amount != 0) {
			instance.addTransientModifier(new AttributeModifier(id, amount, operation));
		}
	}

	/** Suma simple ({@code ADD_VALUE}). */
	public static void set(LivingEntity entity, Holder<Attribute> attribute, Identifier id, double amount) {
		set(entity, attribute, id, amount, AttributeModifier.Operation.ADD_VALUE);
	}

	/** Pone el modificador con {@code amount} si {@code on}, si no lo quita. */
	public static void toggle(LivingEntity entity, Holder<Attribute> attribute, Identifier id, boolean on, double amount,
			AttributeModifier.Operation operation) {
		set(entity, attribute, id, on ? amount : 0, operation);
	}
}
