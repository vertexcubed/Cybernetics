package com.vertexcubed.cybernetics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.vertexcubed.cybernetics.common.registry.CybItems;
import com.vertexcubed.cybernetics.common.util.CyberwareHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class EntityMixin {

    @WrapMethod(method = "getMovementEmission")
    public Entity.MovementEmission cybernetics$getMovementEmission(Operation<Entity.MovementEmission> original) {
        Entity entity = (Entity) (Object) this;
        if(entity instanceof LivingEntity livingEntity) {
            if(CyberwareHelper.hasCyberware(CybItems.SOUND_ABSORBENT_FEET.get(), livingEntity)) {
                return Entity.MovementEmission.EVENTS;
            }
        }
        return original.call();
    }

    @WrapMethod(method = "isSteppingCarefully")
    public boolean cybernetics$isSteppingCarefully(Operation<Boolean> original) {
        Entity entity = (Entity) (Object) this;
        if(entity instanceof LivingEntity livingEntity) {
            if(CyberwareHelper.hasCyberware(CybItems.SOUND_ABSORBENT_FEET.get(), livingEntity)) {
                return true;
            }
        }


        return original.call();
    }


    @WrapMethod(method = "getBlockJumpFactor")
    public float cybernetics$getBlockJumpFactor(Operation<Float> original) {
        Entity e = (Entity) (Object) this;
        if(e instanceof LivingEntity livingEntity) {
            if(CyberwareHelper.hasCyberware(CybItems.FULL_SPEED_FEET.get(), livingEntity)) {
                return 1.0f;
            }
        }
        return original.call();
    }

    @WrapMethod(method = "getBlockSpeedFactor")
    public float cybernetics$getBlockSpeedFactor(Operation<Float> original) {
        Entity e = (Entity) (Object) this;
        if(e instanceof LivingEntity livingEntity) {
            if(CyberwareHelper.hasCyberware(CybItems.FULL_SPEED_FEET.get(), livingEntity)) {
                return 1.0f;
            }
        }
        return original.call();
    }
}
