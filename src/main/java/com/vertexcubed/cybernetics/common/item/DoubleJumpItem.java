package com.vertexcubed.cybernetics.common.item;

import com.vertexcubed.cybernetics.common.registry.CybSoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;

public class DoubleJumpItem extends CyberwareItem{
    public DoubleJumpItem(Properties properties) {
        super(properties);
    }

    //Does this need to be a subclass? No. Does it make things more organized? Yes.
    public static void doubleJump(Player player) {
        player.fallDistance = 0;

        double jumpMotion = (0.5) + player.getJumpBoostPower();
//        double jumpMotion = 3.5;
        Vec3 movement = player.getDeltaMovement();
        player.setDeltaMovement(movement.x, jumpMotion, movement.z);
        if (player.isSprinting()) {
            float f = player.getYRot() * ((float)Math.PI / 180F);
            player.setDeltaMovement(player.getDeltaMovement().add((double)(-Mth.sin(f) * 0.2F), 0.0D, (double)(Mth.cos(f) * 0.2F)));
        }

        player.hasImpulse = true;
        CommonHooks.onLivingJump(player);

        player.awardStat(Stats.JUMP);
        if (player.isSprinting()) {
            player.causeFoodExhaustion(0.2F);
        } else {
            player.causeFoodExhaustion(0.05F);
        }
        float pitch = player.getRandom().nextFloat() * 0.1f + 0.85f;
        player.playSound(CybSoundEvents.DOUBLE_JUMP.get(), 0.75f, pitch);

    }
}
