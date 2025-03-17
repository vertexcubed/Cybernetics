package com.vertexcubed.cybernetics.client.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.vertexcubed.cybernetics.Cybernetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;

public class RenderHelper {

    public static void renderEntity(Entity entity, PoseStack poseStack, float x, float y, float scale, float rotation) {
        renderEntity(entity, poseStack, x, y, 50.0f, scale, rotation);
    }

    /**
     * A lot of this was adopted from The One Probe by McJty!!! I have no idea what I'm doing lol
     */
    public static void renderEntity(Entity entity, PoseStack poseStack, float x, float y, float z, float scale, float rotation) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.scale(-scale, scale, scale);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        //store entity rotations
        float xRot = entity.getXRot();
        float xRotO = entity.xRotO;
        float yRot = entity.getYRot();
        float yRotO = entity.yRotO;

        float yBodyRotO = 0.0f;
        float yBodyRot = 0.0f;
        float yHeadRot = 0.0f;
        float yHeadRotO = 0.0f;

        //modify entity rotations
        entity.setXRot(0.0F);
        entity.xRotO = 0.0F;
        entity.setYRot(0.0F);
        entity.yRotO = 0.0F;

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            yBodyRotO = livingEntity.yBodyRotO;
            yBodyRot = livingEntity.yBodyRot;
            yHeadRot = livingEntity.yHeadRot;
            yHeadRotO = livingEntity.yHeadRotO;
            livingEntity.yBodyRotO = 0.0F;
            livingEntity.yBodyRot = 0.0F;
            livingEntity.yHeadRot = 0.0F;
            livingEntity.yHeadRotO = 0.0F;
        }


        RenderSystem.applyModelViewMatrix();
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        try {
            Quaternionf quaternion = Axis.XP.rotationDegrees(180.0f).conjugate();
            dispatcher.overrideCameraOrientation(quaternion);
            MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
            dispatcher.setRenderShadow(false);
            RenderSystem.runAsFancy(() -> {
                dispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, poseStack, buffer, LightTexture.FULL_BRIGHT);
            });
            buffer.endBatch();
        }
        catch(Exception e) {
            Cybernetics.LOGGER.error("Could not render entity!", e);
        }

        //restore entity rotations
        entity.setXRot(xRot);
        entity.xRotO = xRotO;
        entity.setYRot(yRot);
        entity.yRotO = yRotO;
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            livingEntity.yBodyRotO = yBodyRotO;
            livingEntity.yBodyRot = yBodyRot;
            livingEntity.yHeadRot = yHeadRot;
            livingEntity.yHeadRotO = yHeadRotO;
        }

        dispatcher.setRenderShadow(true);
        poseStack.popPose();
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();


    }

}
