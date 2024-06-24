package dev.redstudio.witherconfig.mixin;

import dev.redstudio.witherconfig.config.WitherConfigConfig;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Luna Lage (Desoroxxx)
 * @since 1.0
 */
@Mixin(EntityWither.class)
public abstract class EntityWitherMixin extends EntityMob {

    private EntityWitherMixin(final World world) {
        super(world);
    }

    @Shadow public abstract int getWatchedTargetId(final int head);

    @Shadow public abstract boolean isArmored();

    @Inject(method = "canDestroyBlock", at = @At(value = "HEAD"), cancellable = true)
    private static void fluidBreakingCheck(final Block blockIn, final CallbackInfoReturnable<Boolean> booleanCallbackInfoReturnable) {
        if (!WitherConfigConfig.common.breakLiquids && blockIn.getDefaultState().getMaterial().isLiquid())
            booleanCallbackInfoReturnable.setReturnValue(false);
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

    @ModifyArg(method = "updateAITasks", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/EntityWither;heal(F)V", ordinal = 0))
    private float changeInvulnerableHealing(final float original) {
        return WitherConfigConfig.common.healing.invulnerableHealing;
    }

    @ModifyArg(method = "updateAITasks", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/EntityWither;heal(F)V", ordinal = 1))
    private float changeVulnerableHealing(final float original) {
        return WitherConfigConfig.common.healing.vulnerableHealing;
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/EntityWither;getWatchedTargetId(I)I", ordinal = 0))
    private int disableVanillaTargetFollowingLogic(final EntityWither instance, int head) {
        return 0;
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "HEAD"))
    private void newTargetFollowingLogic(final CallbackInfo callbackInfo) {
        if (world.isRemote || getWatchedTargetId(0) <= 0)
            return;

        final Entity target = world.getEntityByID(getWatchedTargetId(0));

        if (target == null)
            return;

        if (target instanceof EntityPlayer && WitherConfigConfig.common.breakBlocksWhenTargetingPlayer)
            wither_Config$destroyBlocks(target.posY);

        final double movementSpeed = WitherConfigConfig.common.movementSpeed;

        if (posY < target.posY || !isArmored() && posY < target.posY + WitherConfigConfig.common.unarmoredFlyHeight) {
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

    /**
     * Destroys all blocks that the Wither can destroy around it.
     * <p>
     * <b>Warning:</b> This method does not check for sideness itself, it should only be called on the server side.
     *
     * @param targetY The Y position of the entity being targeted by the Wither
     */
    @Unique
    private void wither_Config$destroyBlocks(final double targetY) {
        if (!ForgeEventFactory.getMobGriefingEvent(world, this))
            return;

        final int x = MathHelper.floor(posX);
        final int y = MathHelper.floor(posY);
        final int z = MathHelper.floor(posZ);

        boolean playSound = false;

        // This prevents the player from being safe from the Wither by just being under it
        final int desiredYOffset = targetY < posY ? -1 : 0;

        for (int xOffset = -1; xOffset <= 1; ++xOffset) {
            for (int zOffset = -1; zOffset <= 1; ++zOffset) {
                for (int yOffset = desiredYOffset; yOffset <= 4; ++yOffset) {
                    final int currentX = x + xOffset;
                    final int currentY = y + yOffset;
                    final int currentZ = z + zOffset;

                    final BlockPos blockPos = new BlockPos(currentX, currentY, currentZ);
                    final IBlockState blockState = world.getBlockState(blockPos);

                    if (blockState.getBlock().canEntityDestroy(blockState, world, blockPos, this) && ForgeEventFactory.onEntityDestroyBlock(this, blockPos, blockState))
                        playSound = world.destroyBlock(blockPos, true) || playSound;
                }
            }
        }

        if (playSound)
            world.playEvent(null, 1022, new BlockPos(this), 0);
    }
}
