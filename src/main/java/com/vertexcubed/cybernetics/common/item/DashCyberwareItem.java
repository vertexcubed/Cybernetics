package com.vertexcubed.cybernetics.common.item;

import com.vertexcubed.cybernetics.Cybernetics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class DashCyberwareItem extends CyberwareItem {

    public DashCyberwareItem(Properties properties) {
        super(properties);
    }

    public static void dash(Player player) {
        Vec3 movement = new Vec3(player.getDeltaMovement().x, 0.0, player.getDeltaMovement().z);
        float scale = 0.5f;
        if(player.onGround()) {
            scale = 1.5f;
        }
        player.setDeltaMovement(movement.add(new Vec3(movement.x, 0.0, movement.z).normalize().scale(scale)));
//        player.hurtMarked = true;
    }
}
