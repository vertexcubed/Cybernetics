package com.vertexcubed.cybernetics.mixin;

import com.vertexcubed.cybernetics.common.registry.CybItems;
import com.vertexcubed.cybernetics.common.util.CyberwareHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "getMovementEmission", at = @At("HEAD"), cancellable = true)
    public void cybernetics$getMovementEmission(CallbackInfoReturnable<Entity.MovementEmission> cir) {
        Entity entity = (Entity) (Object) this;
        if(entity instanceof LivingEntity livingEntity) {
            if(CyberwareHelper.hasCyberware(CybItems.SOUND_ABSORBENT_FEET.get(), livingEntity)) {
                cir.setReturnValue(Entity.MovementEmission.EVENTS);
            }
        }
    }

    @Inject(method = "getBlockJumpFactor", at = @At("HEAD"), cancellable = true)
    public void cybernetics$getBlockJumpFactor(CallbackInfoReturnable<Float> cir) {
        Entity e = (Entity) (Object) this;
        if(e instanceof LivingEntity livingEntity) {
            if(CyberwareHelper.hasCyberware(CybItems.FULL_SPEED_FEET.get(), livingEntity)) {
                cir.setReturnValue(1.0f);
            }
        }
    }

    @Inject(method = "getBlockSpeedFactor", at = @At("HEAD"), cancellable = true)
    public void cybernetics$getBlockSpeedFactor(CallbackInfoReturnable<Float> cir) {
        Entity e = (Entity) (Object) this;
        if(e instanceof LivingEntity livingEntity) {
            if(CyberwareHelper.hasCyberware(CybItems.FULL_SPEED_FEET.get(), livingEntity)) {
                cir.setReturnValue(1.0f);
            }
        }
    }
}
