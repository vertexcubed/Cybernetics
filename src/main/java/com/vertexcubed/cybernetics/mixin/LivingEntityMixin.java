package com.vertexcubed.cybernetics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.vertexcubed.cybernetics.common.registry.CybItems;
import com.vertexcubed.cybernetics.common.util.CyberwareHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }



    @WrapMethod(method = "decreaseAirSupply")
    public int cybenetics$decreaseAirSupply(int currentAir, Operation<Integer> original) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if(CyberwareHelper.hasCyberware(CybItems.OXYGEN_RECYCLER.get(), entity)) {
            if(this.random.nextInt(4) > 0) {
                return currentAir;
            }
        }
        return original.call(currentAir);
    }
}
