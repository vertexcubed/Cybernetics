package com.vertexcubed.cybernetics.client.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.systems.rendering.shader.ShaderHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public class CybCoreShaders {

    private static final List<ShaderHolder> shaders = new ArrayList<>();

    public static ShaderHolder
            CIRCLE_PROGRESS = register(modLoc("circle_progress"), DefaultVertexFormat.POSITION_TEX, "Progress")
    ;


    public static ShaderHolder register(ResourceLocation location, VertexFormat vertexFormat, String... uniformsToCache) {
        ShaderHolder holder = new ShaderHolder(location, vertexFormat, uniformsToCache);
        shaders.add(holder);
        return holder;
    }

    public static void registerShaders(RegisterShadersEvent event) {
        shaders.forEach(shaderHolder -> {
            try {
                ResourceProvider provider = event.getResourceProvider();
                event.registerShader(shaderHolder.createInstance(provider), shaderHolder::setShaderInstance);
            } catch (IOException e) {
                LodestoneLib.LOGGER.error("Error registering shader", e);
                e.printStackTrace();
            }
        });
    }
}
