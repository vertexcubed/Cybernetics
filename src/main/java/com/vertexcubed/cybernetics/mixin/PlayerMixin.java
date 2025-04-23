package com.vertexcubed.cybernetics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.vertexcubed.cybernetics.common.registry.CybItems;
import com.vertexcubed.cybernetics.common.util.CyberwareHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public class PlayerMixin {


    //Player overrides this method, so we need to wrap this again...
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
}
