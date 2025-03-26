package com.vertexcubed.cybernetics.mixin;

import com.vertexcubed.cybernetics.common.registry.CybItems;
import com.vertexcubed.cybernetics.common.util.CyberwareHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }



    @Inject(method = "decreaseAirSupply", at = @At("HEAD"), cancellable = true)
    public void cybenetics$decreaseAirSupply(int currentAir, CallbackInfoReturnable<Integer> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if(CyberwareHelper.hasCyberware(CybItems.OXYGEN_RECYCLER.get(), entity)) {
            if(this.random.nextInt(4) > 0) {
                cir.setReturnValue(currentAir);
            }
        }
    }
}
