package io.github.potatustudio.mythicrelics.egypt;

import io.github.potatustudio.mythicrelics.relic.RelicFx;
import io.github.potatustudio.mythicrelics.relic.RelicItem;
import net.minecraft.core.particles.ParticleTypes;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import io.github.potatustudio.mythicrelics.minion.Minions;
import io.github.potatustudio.mythicrelics.relic.Bosses;
import net.minecraft.ChatFormatting;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Cayado de Osiris, el pastor y rey de los muertos. Clic derecho en una cría la vuelve adulta al instante (la crecida del
 * Nilo); clic derecho en un no-muerto hostil lo pone de tu lado {@link EgyptGods#CROOK_RULE_TICKS} ticks. Las dos cosas
 * comparten la recarga. Que los animales de granja lo sigan lo agrega {@link EgyptGods} a cada animal.
 */
public class OsirisCrookItem extends RelicItem {
	public OsirisCrookItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
		boolean baby = target instanceof AgeableMob animal && animal.isBaby();
		boolean undead = target instanceof Mob && target instanceof Enemy && target.typeHolder().is(EntityTypeTags.UNDEAD)
				&& !Bosses.is(target) && Minions.ownerOf(target) == null;
		if (!(baby || undead) || player.getCooldowns().isOnCooldown(stack)) {
			return InteractionResult.PASS;
		}
		if (player instanceof ServerPlayer serverPlayer && player.level() instanceof ServerLevel level) {
			if (!RelicCooldowns.tryStart(serverPlayer, "osiris_crook", EgyptGods.CROOK_GROW_COOLDOWN,
					BuiltInRegistries.ITEM.getKey(stack.getItem()))) {
				return InteractionResult.PASS;
			}
			if (baby) {
				((AgeableMob) target).setAge(0);
				level.sendParticles(ParticleTypes.HAPPY_VILLAGER, target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
						12, 0.4, 0.4, 0.4, 0);
				RelicFx.sound(level, target, SoundEvents.BONE_MEAL_USE, 1.0f, 1.0f);
			} else {
				Minions.convert(level, player, (Mob) target, EgyptGods.CROOK_RULE_TICKS);
				target.addEffect(new MobEffectInstance(MobEffects.GLOWING, EgyptGods.CROOK_RULE_TICKS, 0, false, false));
				level.sendParticles(ParticleTypes.SCULK_SOUL, target.getX(), target.getY() + target.getBbHeight(), target.getZ(),
						8, 0.3, 0.2, 0.3, 0.02);
				RelicFx.sound(level, target, SoundEvents.EVOKER_PREPARE_SUMMON, 0.8f, 0.7f);
				RelicFx.notify(serverPlayer, "osiris_rules", ChatFormatting.GOLD);
			}
		}
		return InteractionResult.SUCCESS;
	}
}
