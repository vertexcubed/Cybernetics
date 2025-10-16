package com.vertexcubed.cybernetics.common.item;

import com.vertexcubed.cybernetics.Cybernetics;
import net.minecraft.world.entity.player.Player;

public class KineticDischargerItem extends CyberwareItem {

    public KineticDischargerItem(Properties properties) {
        super(properties);
    }

    public static void spike(Player player) {
        player.fallDistance = 0;
        double y = player.getDeltaMovement().y;
        if(y > 0) y = 0.0;
        player.setDeltaMovement(0, (y- 0.6) * 2 , 0);
//        if (player.isSprinting()) {
//            float f = player.getYRot() * ((float)Math.PI / 180F);
//            player.setDeltaMovement(player.getDeltaMovement().add((double)(-Mth.sin(f) * 0.2F), 0.0D, (double)(Mth.cos(f) * 0.2F)));
//        }

        player.hasImpulse = true;
    }
}
