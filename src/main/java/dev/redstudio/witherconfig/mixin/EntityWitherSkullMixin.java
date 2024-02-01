package dev.redstudio.witherconfig.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.redstudio.witherconfig.config.WitherConfigConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import static dev.redstudio.witherconfig.ProjectConstants.LOGGER;

/**
 * @author Luna Lage (Desoroxxx)
 * @since 1.0
 */
@Mixin(EntityWitherSkull.class)
public final class EntityWitherSkullMixin {

    @ModifyArg(method = "onImpact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", ordinal = 0))
    private float changeDamage(final float original) {
        return WitherConfigConfig.common.skulls.damage;
    }

    @ModifyArg(method = "onImpact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", ordinal = 1))
    private float changeMagicDamage(final float original) {
        return WitherConfigConfig.common.skulls.magicDamage;
    }

    @ModifyArg(method = "onImpact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;heal(F)V"))
    private float changeHeal(final float original) {
        return WitherConfigConfig.common.skulls.healOnKill;
    }

    @ModifyArg(method = "onImpact", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;newExplosion(Lnet/minecraft/entity/Entity;DDDFZZ)Lnet/minecraft/world/Explosion;"))
    private float changeExplosionStrength(final float original) {
        return WitherConfigConfig.common.skulls.explosionStrength;
    }

    @Redirect(method = "onImpact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;addPotionEffect(Lnet/minecraft/potion/PotionEffect;)V"))
    private void redirectAddPotionEffect(final EntityLivingBase entityLiving, final PotionEffect potionEffect, @Local int durationMultiplier) {
        for (final String line : WitherConfigConfig.common.skulls.effects) {
            final String[] parts = line.split(";");

            // Make sure there are exactly three parts, otherwise the string is malformed
            // Since we need the effect, duration, and level to apply a potion effect
            if (parts.length != 3) {
                LOGGER.error("Malformed potion effect config: {}", line);
                return;
            }

            final Potion potion = Potion.getPotionFromResourceLocation(parts[0]);
            final int duration = Integer.parseInt(parts[1]) * durationMultiplier;
            final int level = Integer.parseInt(parts[2]);

            if (potion != null)
                entityLiving.addPotionEffect(new PotionEffect(potion, duration, level));
            else
                LOGGER.error("Potion/effect was not found {}", parts[0]);
        }
    }
}
