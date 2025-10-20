package com.vertexcubed.cybernetics.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.vertexcubed.cybernetics.client.shader.CybCoreShaders;
import com.vertexcubed.cybernetics.client.util.CustomWorldParticleRenderType;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypes;
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType;
import team.lodestar.lodestone.systems.particle.world.LodestoneWorldParticle;
import team.lodestar.lodestone.systems.particle.world.options.WorldParticleOptions;

public class FallingParticle extends LodestoneWorldParticle {
    public static final LodestoneWorldParticleRenderType FALLING_PARTICLE_RENDER_TYPE = new CustomWorldParticleRenderType(
            LodestoneRenderTypes.ADDITIVE_PARTICLE, CybCoreShaders.FALLING_PARTICLE, null,
            GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);


    public FallingParticle(ClientLevel world, WorldParticleOptions options, double x, double y, double z, double xd, double yd, double zd) {
        super(world, options, null, x, y, z, xd, yd, zd);
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(float partialTicks) {
        return AABB.INFINITE;
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTicks) {
        if (lifeDelay > 0) {
            return;
        }
        renderActors.forEach(actor -> actor.accept(this));
        renderParticle(consumer, camera, partialTicks);
    }

    private void renderParticle(VertexConsumer consumer, Camera camera, float partialTicks) {

//        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();

//        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
//        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);


//        RenderSystem.setShader(CybCoreShaders::getFallingParticleShader);
//        RenderHelper.resetShaderColor();


        Vec3 vec3 = camera.getPosition();
        float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());
        float yaw = -camera.getYRot();

        float height = 3.0f + 2 * (age + partialTicks);

        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-0.125F, -height, 0.0F), new Vector3f(-0.125F, height, 0.0F), new Vector3f(0.125F, height, 0.0F), new Vector3f(0.125F, -height, 0.0F)};
        float f3 = this.getQuadSize(partialTicks);

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(Axis.YP.rotationDegrees(yaw));
            vector3f.mul(f3);
            vector3f.add(x, y, z);
        }

        float uMin = 0;
        float uMax = 1;
        float vMin = 0;
        float vMax = 1;
        int j = this.getLightColor(partialTicks);
        consumer.addVertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).setUv(uMax, vMax).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        consumer.addVertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).setUv(uMax, vMin).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        consumer.addVertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).setUv(uMin, vMin).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);
        consumer.addVertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).setUv(uMin, vMax).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(j);

//        Tesselator.getInstance().end();

//        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
    }



    public static class Provider implements ParticleProvider<WorldParticleOptions> {

        @Override
        public @Nullable Particle createParticle(@NotNull WorldParticleOptions data, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new FallingParticle(level, data, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
