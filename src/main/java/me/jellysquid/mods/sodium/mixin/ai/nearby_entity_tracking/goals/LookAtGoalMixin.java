package me.jellysquid.mods.sodium.mixin.ai.nearby_entity_tracking.goals;

import me.jellysquid.mods.lithium.common.entity.tracker.nearby.NearbyEntityListenerProvider;
import me.jellysquid.mods.lithium.common.entity.tracker.nearby.NearbyEntityTracker;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.IEntityReader;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LookAtGoal.class)
public class LookAtGoalMixin {
    private NearbyEntityTracker<? extends LivingEntity> tracker;

    @Inject(method = "<init>(Lnet/minecraft/entity/MobEntity;Ljava/lang/Class;FF)V", at = @At("RETURN"))
    private void init(MobEntity entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance, float chanceIn, CallbackInfo ci) {
        this.tracker = new NearbyEntityTracker<>(watchTargetClass, entityIn, maxDistance);

        ((NearbyEntityListenerProvider) entityIn).getListener().addListener(this.tracker);
    }

    @Redirect(
            method = "shouldExecute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getClosestEntity(Ljava/lang/Class;Lnet/minecraft/entity/EntityPredicate;Lnet/minecraft/entity/LivingEntity;DDDLnet/minecraft/util/math/AxisAlignedBB;)Lnet/minecraft/entity/LivingEntity;"
            )
    )
    private <T extends LivingEntity> LivingEntity redirectGetClosestEntity(World world, Class<? extends T> entityClass, EntityPredicate targetPredicate, LivingEntity entity, double x, double y, double z, AxisAlignedBB box) {
        return this.tracker.getClosestEntity(box, targetPredicate);
    }

    @Redirect(
            method = "shouldExecute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getClosestPlayer(Lnet/minecraft/entity/EntityPredicate;Lnet/minecraft/entity/LivingEntity;DDD)Lnet/minecraft/entity/player/PlayerEntity;"
            )
    )
    private PlayerEntity redirectGetClosestPlayer(World world, EntityPredicate targetPredicate, LivingEntity entity, double x, double y, double z) {
        return (PlayerEntity) this.tracker.getClosestEntity(null, targetPredicate);
    }
}
