package com.vertexcubed.cybernetics.common.item;

import com.vertexcubed.cybernetics.common.ability.KineticDischargerAbility;
import com.vertexcubed.cybernetics.common.registry.CybAbilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class KineticDischargerItem extends SimpleAbilityCyberwareItem<KineticDischargerAbility> {

    public KineticDischargerItem(Properties properties) {
        super(properties, CybAbilities.KINETIC_DISCHARGER);
    }

    public static void spike(LivingEntity entity) {
        entity.fallDistance = 0;
        double y = entity.getDeltaMovement().y;
        if(y > 0) y = 0.0;
        entity.setDeltaMovement(0, (y- 0.6) * 2 , 0);
//        if (player.isSprinting()) {
//            float f = player.getYRot() * ((float)Math.PI / 180F);
//            player.setDeltaMovement(player.getDeltaMovement().add((double)(-Mth.sin(f) * 0.2F), 0.0D, (double)(Mth.cos(f) * 0.2F)));
//        }

        entity.hasImpulse = true;
    }


    public static void shockwave(Player player, Level level, int spikeTime) {
//        BlockPos pos = new BlockPos((int)player.position().x, (int)(player.getBoundingBox().minY - 0.5000001D), (int)player.position().z);
        if(spikeTime == -1) return;

        BlockPos pos = player.blockPosition().subtract(new Vec3i(0, 1, 0));
        BlockPos.betweenClosed(pos.offset(3, 0, 3), pos.offset(-3, 0, -3)).forEach(blockPos -> {

            BlockPos difference = blockPos.subtract(pos);
            if(Math.abs(difference.getX()) == 3 && Math.abs(difference.getZ()) == 3) return;

            BlockState block = level.getBlockState(blockPos);
            level.levelEvent(2001, blockPos, Block.getId(block));
        });

        AABB box = new AABB(player.blockPosition()).inflate(4);
        level.getEntities(player, box).forEach(entity -> {
            if (!(entity instanceof LivingEntity) || !entity.isAlive()) {
                return;
            }
            float damage = 8.0f + (0.4f * Math.min(spikeTime, 60));
            entity.hurt(level.damageSources().playerAttack(player), damage);
            entity.push(0, (damage + 2) / 12, 0);
        });


        //todo: make this run 1 tick later
//        if(level instanceof ServerLevel server) {
//            server.sendParticles(new BlastWaveParticleOptions(CybParticles.BLAST_WAVE.get()), player.position().x, player.position().y + 0.01, player.position().z, 1, 0, 0, 0, 0);
//
//            CybPackets.getInstance().send(
//                    PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(player.position().x, player.position().y, player.position().z, 10.0, player.level().dimension())),
//                    new CustomPositionedScreenshakePacket(3, true, player.position(), 5.0f, 10.0f).setIntensity(0.775f).setEasing(Easing.EXPO_OUT, Easing.SINE_IN_OUT)
//            );
//        }

    }
}
