package com.vertexcubed.cybernetics.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderType;
import team.lodestar.lodestone.systems.rendering.shader.ShaderHolder;


/**
 * Identical to {@link LodestoneWorldParticleRenderType} except for a null check for the texture
 */
public class CustomWorldParticleRenderType extends LodestoneWorldParticleRenderType {
    public CustomWorldParticleRenderType(LodestoneRenderType renderType, ShaderHolder shaderHolder, ResourceLocation texture, GlStateManager.SourceFactor srcAlpha, GlStateManager.DestFactor dstAlpha) {
        super(renderType, shaderHolder, texture, srcAlpha, dstAlpha);
    }

    @Override
    public BufferBuilder begin(Tesselator tesselator, TextureManager manager) {
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        blendFunction.run();
        RenderSystem.setShader(shader);
        if(texture != null) {
            RenderSystem.setShaderTexture(0, texture);
        }
        return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
    }
}
