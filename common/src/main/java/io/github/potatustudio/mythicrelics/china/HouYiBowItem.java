package io.github.potatustudio.mythicrelics.china;

import io.github.potatustudio.mythicrelics.registry.ModLoot;
import io.github.potatustudio.mythicrelics.relic.RelicItem;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

/**
 * Arco de Hou Yi, el arquero que derribó nueve de los diez soles que quemaban la Tierra. Dispara como un arco normal,
 * pero las flechas hacen más daño y vuelan más rápido (más recto). Contra los «soles» (mobs de fuego y voladores) el
 * daño se triplica, y lo que matas cae como el cuervo dorado de tres patas con el doble de botín ({@link ChinaRelics}).
 */
public class HouYiBowItem extends BowItem {
	private static final float SPEED_FACTOR = 1.3f;

	public HouYiBowItem(Properties properties) {
		super(properties);
	}

	@Override
	protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean crit) {
		Projectile projectile = super.createProjectile(level, shooter, weapon, ammo, crit);
		if (projectile instanceof AbstractArrow arrow) {
			// Una flecha normal parte de 2 de daño base.
			arrow.setBaseDamage(2.0 * (1.0 + ChinaRelics.HOU_YI_DAMAGE));
		}
		projectile.addTag(ChinaRelics.HOU_YI_TAG);
		return projectile;
	}

	@Override
	protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle,
			LivingEntity target) {
		super.shootProjectile(shooter, projectile, index, velocity * SPEED_FACTOR, inaccuracy * 0.5f, angle, target);
	}

	// Mojang lo marca @Deprecated en el sentido de «sobrescribir, no llamar»: en 26.3 sigue siendo el gancho del tooltip
	// del ítem (ItemStack lo llama). TooltipProvider solo sirve para componentes que vanilla ya conoce.
	@SuppressWarnings("deprecation")
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip,
			TooltipFlag flag) {
		RelicItem.effectLines(this, tooltip);
		ModLoot.appendWhere(this, tooltip);
	}
}
