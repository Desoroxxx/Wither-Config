package io.redstudioragnarok.witherconfig.mixin;

import io.redstudioragnarok.witherconfig.config.WitherConfigConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Luna Lage (Desoroxxx)
 * @since 1
 */
@Mixin(EntityWither.class)
public abstract class EntityWitherMixin extends EntityMob {

    @Shadow public abstract int getWatchedTargetId(final int head);

    @Shadow public abstract boolean isArmored();

    private EntityWitherMixin(final World world) {
        super(world);
    }

    @ModifyArg(method = "applyEntityAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/attributes/IAttributeInstance;setBaseValue(D)V", ordinal = 0))
    private double changeMaxHealth(final double original) {
        return WitherConfigConfig.common.maxHealth;
    }

    @ModifyArg(method = "applyEntityAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/attributes/IAttributeInstance;setBaseValue(D)V", ordinal = 1))
    private double changeMovementSpeed(final double original) {
        return WitherConfigConfig.common.movementSpeed;
    }

    @ModifyArg(method = "applyEntityAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/attributes/IAttributeInstance;setBaseValue(D)V", ordinal = 2))
    private double changeFollowRange(final double original) {
        return WitherConfigConfig.common.followRange;
    }

    @ModifyArg(method = "applyEntityAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/attributes/IAttributeInstance;setBaseValue(D)V", ordinal = 3))
    private double changeArmor(final double original) {
        return WitherConfigConfig.common.armor;
    }

    @ModifyArg(method = "ignite", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/EntityWither;setInvulTime(I)V"))
    private int changeSummonSequenceLength(final int original) {
        return WitherConfigConfig.common.summonSequence.length;
    }

    @ModifyArg(method = "updateAITasks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;newExplosion(Lnet/minecraft/entity/Entity;DDDFZZ)Lnet/minecraft/world/Explosion;"))
    private float changeEndExplosionStrength(final float original) {
        return WitherConfigConfig.common.summonSequence.endExplosionStrength;
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/EntityWither;getWatchedTargetId(I)I", ordinal = 0))
    private int disableVanillaTargetFollowingLogic(final EntityWither instance, int head) {
        return 0;
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isRemote:Z", shift = At.Shift.AFTER))
    private void newTargetFollowingLogic(final CallbackInfo callbackInfo) {
        if (world.isRemote || getWatchedTargetId(0) <= 0)
            return;

        final Entity target = world.getEntityByID(getWatchedTargetId(0));

        if (target == null)
            return;

        final double movementSpeed = WitherConfigConfig.common.movementSpeed;

        if (posY < target.posY || !isArmored() && posY < target.posY + 5) {
            if (motionY < 0)
                motionY = 0;

            motionY += (0.5 - motionY) * movementSpeed;
        }

        final double deltaX = target.posX - posX;
        final double deltaZ = target.posZ - posZ;
        final double distanceToTarget = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        if (distanceToTarget > WitherConfigConfig.common.followDistance) {
            motionX += (deltaX / distanceToTarget * 0.5 - motionX) * movementSpeed;
            motionZ += (deltaZ / distanceToTarget * 0.5 - motionZ) * movementSpeed;
        }
    }
}
