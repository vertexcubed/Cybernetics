package com.vertexcubed.cybernetics.common.item;

import com.vertexcubed.cybernetics.common.ability.DashAbility;
import com.vertexcubed.cybernetics.common.registry.CybAbilities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class DashCyberwareItem extends SimpleAbilityCyberwareItem<DashAbility> {

    public DashCyberwareItem(Properties properties) {
        super(properties, CybAbilities.DASH);
    }

    public static void dash(LivingEntity entity) {
        Vec3 movement = new Vec3(entity.getDeltaMovement().x, 0.0, entity.getDeltaMovement().z);
        float scale = 0.5f;
        if(entity.onGround()) {
            scale = 1.5f;
        }
        entity.setDeltaMovement(movement.add(new Vec3(movement.x, 0.0, movement.z).normalize().scale(scale)));
//        player.hurtMarked = true;
    }
}
