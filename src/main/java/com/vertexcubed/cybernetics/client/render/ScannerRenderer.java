package com.vertexcubed.cybernetics.client.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.vertexcubed.cybernetics.client.shader.CybCoreShaders;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import team.lodestar.lodestone.systems.rendering.shader.ExtendedShaderInstance;

import java.util.ArrayList;
import java.util.List;

import static team.lodestar.lodestone.handlers.LodestoneRenderHandler.LODESTONE_DEPTH_CACHE;

public class ScannerRenderer {
    private static final ScannerRenderer INSTANCE = new ScannerRenderer();
    public static ScannerRenderer getInstance() {
        return INSTANCE;
    }




    public static final int SCAN_RANGE = 30;
    public static final int DURATION = 200;

    private boolean shouldRender;
    private long startTime;
    private Vector3f center;
    private int duration;
    private final List<Entity> entitiesToGlow = new ArrayList<>();

    private ScannerRenderer() {}

    public void setup(Entity entity, int duration) {
        this.center = entity.position().toVector3f();
        this.duration = duration;
        shouldRender = true;
        this.startTime = Minecraft.getInstance().level.getGameTime();
        entitiesToGlow.clear();
        entitiesToGlow.addAll(entity.level().getEntities(entity, entity.getBoundingBox().inflate(SCAN_RANGE)));
    }

    public void stop() {
        shouldRender = false;
    }

    public void renderScan(PoseStack poseStack, float partialTick, Camera camera, RenderTarget renderTarget) {
        if(!shouldRender) return;

        long current = Minecraft.getInstance().level.getGameTime();
        long elapsed = current - startTime;
        if(elapsed > duration) {
            stop();
            return;
        }


        ExtendedShaderInstance shader = (ExtendedShaderInstance) CybCoreShaders.SCAN.getShaderInstance();
        if(shader == null) return;

        // width and height
        int width = renderTarget.width;
        int height = renderTarget.height;

        // setup render state

        // disabling depth test temporarily
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        ShaderInstance oldShader = RenderSystem.getShader();
        RenderSystem.setShader(() -> shader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);



        // Copy proj mat before setting the ortho matrix
        Matrix4f invProjMat = new Matrix4f(RenderSystem.getProjectionMatrix()).invert();

        // we're replacing the projection matrix with an orthographic matrix w/ screensize as the size.
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, width, 0, height, 0.1f, 1000.0f), VertexSorting.ORTHOGRAPHIC_Z);


        // Copy View mat before setting it to identity
        Matrix4f invViewMat = new Matrix4f(RenderSystem.getModelViewMatrix()).invert();

        // This is a BAD BAD BAD HORRENDOUS IDEA but im sick and tired
        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.mul(invViewMat);
        RenderSystem.applyModelViewMatrix();




        // setup other uniforms
        Vector3f cameraPos = camera.getPosition().toVector3f();

        // non-cached uniforms
        shader.setSampler("SceneDepthBuffer", LODESTONE_DEPTH_CACHE.getDepthTextureId());
        shader.safeGetUniform("InvProjMat").set(invProjMat);
        shader.safeGetUniform("InvViewMat").set(invViewMat);

        // cached uniforms
        shader.safeGetUniform("CameraPos").set(cameraPos);
        shader.safeGetUniform("Center").set(center);
        shader.safeGetUniform("Radius").set((elapsed + partialTick) * 1.5f);



        // render
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(0, 0, -50).setUv(0, 0);
        buffer.addVertex(width, 0, -50).setUv(1, 0);
        buffer.addVertex(width, height, -50).setUv(1, 1);
        buffer.addVertex(0, height, -50).setUv(0, 1);

        BufferUploader.drawWithShader(buffer.buildOrThrow());

        // clean up view matrix
        modelViewStack.popMatrix();
        RenderSystem.applyModelViewMatrix();

        // end
        shader.setUniformDefaults();
        RenderSystem.restoreProjectionMatrix();
        RenderSystem.setShader(() -> oldShader);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

    }

    public boolean shouldRenderGlowingEntiy(Entity entity) {
        if(!(entity instanceof LivingEntity)) return false;
        return shouldRender && entitiesToGlow.contains(entity);
    }
}
