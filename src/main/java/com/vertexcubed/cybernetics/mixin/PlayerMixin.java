package com.vertexcubed.cybernetics.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.vertexcubed.cybernetics.common.registry.CybItems;
import com.vertexcubed.cybernetics.common.util.CyberwareHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

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

    @ModifyVariable(method = "eat", at = @At("HEAD"), argsOnly = true)
    public FoodProperties cybernetics$modifyFoodProperties(FoodProperties original) {
        Player player = (Player) (Object) this;
        if(CyberwareHelper.hasCyberware(CybItems.STOMACH_FILTER.get(), player)) {
            if(player.getRandom().nextInt(4) > 0) {
                return new FoodProperties(
                        original.nutrition() * 2,
                        original.saturation() * 2,
                        original.canAlwaysEat(),
                        original.eatSeconds(),
                        original.usingConvertsTo(),
                        original.effects()
                );
            }
        }
        return original;
    }
}
