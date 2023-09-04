package io.redstudioragnarok.witherconfig.mixin;

import io.redstudioragnarok.witherconfig.config.WitherConfigConfig;
import net.minecraft.entity.projectile.EntityWitherSkull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * @author Luna Lage (Desoroxxx)
 * @since 1
 */
@Mixin(EntityWitherSkull.class)
public final class EntityWitherSkullMixin {

    @ModifyArg(method = "onImpact", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;newExplosion(Lnet/minecraft/entity/Entity;DDDFZZ)Lnet/minecraft/world/Explosion;"))
    private float changeExplosionStrength(final float original) {
        return WitherConfigConfig.common.skulls.explosionStrength;
    }
}
