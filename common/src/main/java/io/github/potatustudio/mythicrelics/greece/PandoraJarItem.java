package io.github.potatustudio.mythicrelics.greece;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Jarra de Pandora: abrirla tira al azar. Un mal sobre ti, los males que escapan (vexes, como las keres aladas), un
 * tesoro o, con suerte, la Esperanza que quedó dentro.
 */
public class PandoraJarItem extends RelicItem {
	private static final List<Holder<MobEffect>> EVILS = List.of(MobEffects.POISON, MobEffects.WEAKNESS, MobEffects.SLOWNESS,
			MobEffects.BLINDNESS, MobEffects.NAUSEA, MobEffects.HUNGER, MobEffects.MINING_FATIGUE, MobEffects.WITHER);

	public PandoraJarItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.getCooldowns().isOnCooldown(stack)) {
			return InteractionResult.PASS;
		}
		if (player instanceof ServerPlayer serverPlayer) {
			if (!RelicCooldowns.tryStart(serverPlayer, "pandora_jar", GreeceHeroes.PANDORA_COOLDOWN, BuiltInRegistries.ITEM.getKey(this))) {
				return InteractionResult.PASS;
			}
			open(serverPlayer);
		}
		return InteractionResult.SUCCESS;
	}

	private static void open(ServerPlayer player) {
		ServerLevel level = player.level();
		RandomSource random = level.getRandom();
		level.sendParticles(ParticleTypes.WITCH, player.getX(), player.getY() + 1.4, player.getZ(), 30, 0.4, 0.6, 0.4, 0.05);
		RelicFx.sound(level, player, SoundEvents.DECORATED_POT_INSERT, 0.8f, 0.6f);
		int roll = random.nextInt(100);
		int vexes = GreeceHeroes.PANDORA_EVIL_CHANCE + GreeceHeroes.PANDORA_VEX_CHANCE;
		int treasure = vexes + GreeceHeroes.PANDORA_TREASURE_CHANCE;
		if (roll < GreeceHeroes.PANDORA_EVIL_CHANCE) {
			Holder<MobEffect> evil = EVILS.get(random.nextInt(EVILS.size()));
			player.addEffect(new MobEffectInstance(evil, GreeceHeroes.PANDORA_EVIL_TICKS, 0));
			tell(player, "pandora_evil", ChatFormatting.DARK_PURPLE);
		} else if (roll < vexes) {
			releaseVexes(level, player);
			tell(player, "pandora_vexes", ChatFormatting.DARK_PURPLE);
		} else if (roll < treasure) {
			ItemStack loot = switch (random.nextInt(3)) {
				case 0 -> new ItemStack(Items.EMERALD, 2 + random.nextInt(4));
				case 1 -> new ItemStack(Items.DIAMOND, 1 + random.nextInt(2));
				default -> new ItemStack(Items.GOLDEN_APPLE);
			};
			ItemEntity drop = new ItemEntity(level, player.getX(), player.getY() + 1.0, player.getZ(), loot);
			drop.setNoPickUpDelay();
			level.addFreshEntity(drop);
			RelicFx.sound(level, player, SoundEvents.AMETHYST_BLOCK_CHIME, 1.0f, 1.2f);
			tell(player, "pandora_treasure", ChatFormatting.GOLD);
		} else {
			hope(level, player);
			tell(player, "pandora_hope", ChatFormatting.AQUA);
		}
	}

	private static void releaseVexes(ServerLevel level, ServerPlayer player) {
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.HOSTILE, 1.0f, 1.0f);
		for (int i = 0; i < GreeceHeroes.PANDORA_VEXES; i++) {
			Vex vex = EntityTypes.VEX.create(level, EntitySpawnReason.MOB_SUMMONED);
			if (vex == null) {
				continue;
			}
			double angle = i * (Math.PI * 2 / GreeceHeroes.PANDORA_VEXES);
			vex.snapTo(player.getX() + Math.cos(angle) * 1.5, player.getY() + 1.6, player.getZ() + Math.sin(angle) * 1.5,
					level.getRandom().nextFloat() * 360f, 0f);
			vex.setLimitedLife(GreeceHeroes.PANDORA_VEX_TICKS);
			vex.setTarget(player);
			level.addFreshEntity(vex);
		}
	}

	/** La Esperanza: te cura por completo, te quita los males y te da absorción. */
	private static void hope(ServerLevel level, ServerPlayer player) {
		player.setHealth(player.getMaxHealth());
		List<Holder<MobEffect>> harmful = new ArrayList<>();
		for (MobEffectInstance effect : player.getActiveEffects()) {
			if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
				harmful.add(effect.getEffect());
			}
		}
		harmful.forEach(player::removeEffect);
		player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, GreeceHeroes.PANDORA_HOPE_ABSORPTION_TICKS, 1));
		level.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.2, player.getZ(), 24, 0.4, 0.6, 0.4, 0.05);
		RelicFx.sound(level, player, SoundEvents.BEACON_ACTIVATE, 0.8f, 1.6f);
	}

	private static void tell(ServerPlayer player, String key, ChatFormatting color) {
		player.sendOverlayMessage(Component.translatable("message.mythicrelics." + key).withStyle(color));
	}
}
