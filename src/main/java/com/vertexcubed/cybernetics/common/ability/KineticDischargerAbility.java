package com.vertexcubed.cybernetics.common.ability;

import com.vertexcubed.cybernetics.client.particle.FallingParticle;
import com.vertexcubed.cybernetics.common.item.KineticDischargerItem;
import com.vertexcubed.cybernetics.common.registry.CybParticles;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;

import java.awt.*;

public class KineticDischargerAbility extends Ability {

    public KineticDischargerAbility(AbilityType<?> type) {
        super(type);
    }

    @Override
    public void onEnable(LivingEntity parent) {
        KineticDischargerItem.spike(parent);
    }

    @Override
    public void abilityTick(LivingEntity parent) {
        if(parent.level().isClientSide && this.getRunningTime() % 10 == 0) {
            WorldParticleBuilder builder = WorldParticleBuilder.create(CybParticles.FALLING_PARTICLE)
                    .setRenderType(FallingParticle.FALLING_PARTICLE_RENDER_TYPE)
                    .setScaleData(GenericParticleData.create(1, 1, 1).build())
                    .setColorData(ColorParticleData.create(new Color(255, 232, 28), new Color(255, 232, 28)).build())
                    .setTransparencyData(GenericParticleData.create(0.4f, 0).setEasing(Easing.QUAD_IN).build())
//                    .setShouldCull(false)
                    .setLifetime(20)
                    .setNoClip(true)
                    .setRandomOffset(5.0, 0.0, 5.0)
                    .setMotion(0.0, parent.getDeltaMovement().y, 0.0);


            float max = 4.0f;
            float min = 1.5f;
            RandomSource random = parent.level().random;

            for(int i = 0; i < 10; i++) {

                double yaw2 = random.nextFloat() * Math.PI * 2, pitch2 = random.nextFloat() * Math.PI - Math.PI / 2, xDist = random.nextFloat() * (max - min) + min, yDist = 0, zDist = random.nextFloat() * (max - min) + min;
                double xPos = Math.sin(yaw2) * Math.cos(pitch2) * xDist;
                double zPos = Math.cos(yaw2) * Math.cos(pitch2) * zDist;
                builder.spawn(parent.level(), parent.position().x + xPos, parent.position().y - 3.0, parent.position().z + zPos);
            }
        }
    }

    @Override
    public void onDisable(LivingEntity parent) {

    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {

    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {

    }
}
