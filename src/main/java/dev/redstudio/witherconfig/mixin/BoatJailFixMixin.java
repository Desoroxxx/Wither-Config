package dev.redstudio.witherconfig.mixin;

import dev.redstudio.witherconfig.config.WitherConfigConfig;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fix for the boat jail, the boat jail is an exploit that consists in spawning the wither in a jail of boats that prevents it from moving, allowing it to be easily killed.
 *
 * @author Luna Lage (Desoroxxx)
 * @since 1.1
 */
@Mixin(EntityWither.class)
public final class BoatJailFixMixin extends EntityMob {

    @Unique private byte wither_Config$areaBoatDamageTick;

    private BoatJailFixMixin(final World world) {
        super(world);
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "HEAD"))
    private void areaBoatDamage(final CallbackInfo callbackInfo) {
        if (world.isRemote)
            return;

        if (wither_Config$areaBoatDamageTick != 0) {
            wither_Config$areaBoatDamageTick--;
            return;
        }

        wither_Config$areaBoatDamageTick = WitherConfigConfig.common.boatJailFix.tickDelay;

        final int range = WitherConfigConfig.common.boatJailFix.range;

        for (final EntityBoat nearbyBoatEntity : world.getEntitiesWithinAABB(EntityBoat.class, new AxisAlignedBB(posX - range, posY - range, posZ - range, posX + range, posY + range, posZ + range))) {
            nearbyBoatEntity.attackEntityFrom(DamageSource.WITHER, 1);

            final double x = nearbyBoatEntity.posX;
            final double y = nearbyBoatEntity.posY;
            final double z = nearbyBoatEntity.posZ;

            ((WorldServer) world).spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 8, 0.5, 0.3, 0.5, 0.1F);

            world.playSound(null, new BlockPos(x, y, z), SoundEvents.BLOCK_WOOD_HIT, SoundCategory.HOSTILE, 0.75F + rand.nextFloat() * 0.5F, 0.75F + rand.nextFloat() * 0.5F);
        }
    }
}
