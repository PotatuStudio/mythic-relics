package io.github.potatustudio.mythicrelics.greece;

import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import io.github.potatustudio.mythicrelics.relic.RelicItem;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;

/**
 * Hinojo de Prometeo: el fuego robado. Lo que golpeas arde, y con clic derecho cocina o funde al instante lo que llevas
 * en la otra mano (con las recetas del horno).
 */
public class PrometheusFennelItem extends RelicItem {
	public PrometheusFennelItem(Properties properties) {
		super(properties);
	}

	@Override
	public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		super.hurtEnemy(stack, target, attacker);
		target.igniteForSeconds(GreeceHeroes.PROMETHEUS_BURN_SECONDS);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack fennel = player.getItemInHand(hand);
		ItemStack other = player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
		if (other.isEmpty() || player.getCooldowns().isOnCooldown(fennel)) {
			return InteractionResult.PASS;
		}
		if (!(player instanceof ServerPlayer serverPlayer) || !(level instanceof ServerLevel server)) {
			return InteractionResult.SUCCESS;
		}
		SingleRecipeInput input = new SingleRecipeInput(other);
		Optional<RecipeHolder<SmeltingRecipe>> recipe = server.recipeAccess().getRecipeFor(RecipeType.SMELTING, input, server);
		if (recipe.isEmpty()) {
			RelicFx.notify(serverPlayer, "prometheus_nothing", ChatFormatting.GRAY);
			return InteractionResult.PASS;
		}
		int count = other.getCount();
		int cooldown = Math.max(GreeceHeroes.PROMETHEUS_COOLDOWN_MIN, count * GreeceHeroes.PROMETHEUS_COOLDOWN_PER_ITEM);
		if (!RelicCooldowns.tryStart(serverPlayer, "prometheus_fennel", cooldown, BuiltInRegistries.ITEM.getKey(this))) {
			return InteractionResult.PASS;
		}
		ItemStack one = recipe.get().value().assemble(input);
		int total = one.getCount() * count;
		other.shrink(count);
		// El stack entero: el resultado se reparte en stacks llenos.
		while (total > 0) {
			ItemStack result = one.copyWithCount(Math.min(total, one.getMaxStackSize()));
			total -= result.getCount();
			if (!player.getInventory().add(result)) {
				// Lo que no cabe en el inventario cae a los pies, sin demora para recogerlo.
				ItemEntity drop = new ItemEntity(server, player.getX(), player.getY() + 0.5, player.getZ(), result);
				drop.setNoPickUpDelay();
				server.addFreshEntity(drop);
			}
		}
		server.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 1.2, player.getZ(), 16, 0.3, 0.3, 0.3, 0.02);
		RelicFx.sound(server, player, SoundEvents.FIRECHARGE_USE, 0.6f, 1.2f);
		return InteractionResult.SUCCESS;
	}
}
