package com.vertexcubed.cybernetics.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.vertexcubed.cybernetics.client.shader.CybCoreShaders;
import com.vertexcubed.cybernetics.client.util.HUDAnchor;
import com.vertexcubed.cybernetics.common.ability.Ability;
import com.vertexcubed.cybernetics.common.ability.OpticsAbility;
import com.vertexcubed.cybernetics.common.registry.CybAttachments;
import com.vertexcubed.cybernetics.common.storage.AbilityStorage;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import team.lodestar.lodestone.systems.rendering.shader.ExtendedShaderInstance;

import java.util.ArrayList;
import java.util.List;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public class AbilityHUD implements HUDRenderable {

    private static final ResourceLocation TEXTURE = modLoc("textures/gui/hud/ability_hud.png");
    private final HUDAnchor anchor;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final List<SubElement> elements = new ArrayList<>();

    private int v;
    private int xOffset, yOffset;
    private int leftOrRight, upOrDown; // -1 = left/up, 1 = right/down

    public AbilityHUD(HUDAnchor anchor, int x, int y) {
        this.anchor = anchor;
        this.x = x;
        this.y = y;
        this.width = 158;
        this.height = 26;
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        RenderSystem.enableBlend();
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        Vector2i pos = anchor.getWithOffset(screenWidth, screenHeight, x, y);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(pos.x, pos.y, 0);
        renderElements(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true));
        guiGraphics.pose().popPose();
    }

    private void renderElements(GuiGraphics guiGraphics, float partialTick) {
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        RenderSystem.setShaderTexture(0, TEXTURE);

        if(v != -1) {
            guiGraphics.setColor(1, 1, 1, 0.7f);
            guiGraphics.blit(TEXTURE, 0, 0, 0, v * 27, width, height, 256, 256);
            guiGraphics.setColor(1, 1, 1, 1);
        }

        for(int i = 0; i < elements.size(); i++) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(xOffset + (leftOrRight * (i % 7) * 23), yOffset + (upOrDown * (i / 7) * 23), 0);
            guiGraphics.setColor(1, 1, 1, 0.7f);
            elements.get(i).render(guiGraphics, partialTick);
            guiGraphics.setColor(1, 1, 1, 1);
            guiGraphics.pose().popPose();
        }
    }

    @Override
    public void init() {
        updateElementList();


        switch (anchor) {
            case TOP_RIGHT -> {
                v = 0;
                xOffset = width - 4 - 18;
                yOffset = 4;
                leftOrRight = -1;
                upOrDown = 1;
            }
            case TOP_LEFT -> {
                v = 1;
                xOffset = 4;
                yOffset = 4;
                leftOrRight = 1;
                upOrDown = 1;
            }
            case TOP_MIDDLE -> {
                v = 2;
                xOffset = 4;
                yOffset = 4;
                leftOrRight = 1;
                upOrDown = 1;
            }
            case BOTTOM_RIGHT -> {
                v = 3;
                xOffset = width - 4 - 18;
                yOffset = height - 4 - 18;
                leftOrRight = -1;
                upOrDown = -1;
            }
            case BOTTOM_LEFT -> {
                v = 4;
                xOffset = 4;
                yOffset = height - 4 - 18;
                leftOrRight = 1;
                upOrDown = -1;
            }
            case BOTTOM_MIDDLE -> {
                v = 5;
                xOffset = 4;
                yOffset = height - 4 - 18;
                leftOrRight = 1;
                upOrDown = -1;
            }
            case MIDDLE_RIGHT -> {
                v = 6;
                xOffset = width - 4 - 18;
                yOffset = 4;
                leftOrRight = -1;
                upOrDown = 1;
            }
            case MIDDLE_LEFT -> {
                v = 7;
                xOffset = 4;
                yOffset = 4;
                leftOrRight = 1;
                upOrDown = 1;
            }
            default -> {
                v = -1;
                xOffset = 4;
                yOffset = 4;
                leftOrRight = 1;
                upOrDown = 1;
            }
        }
    }

    public void updateElementList() {
        elements.clear();
        if(Minecraft.getInstance().player == null) return;
        AbilityStorage storage = Minecraft.getInstance().player.getData(CybAttachments.ABILITY_STORAGE);
        storage.getAbilities().forEach(ability -> {
                if(ability instanceof OpticsAbility) return;
                elements.add(new SubElement(ability));
            });
    }

    @Override
    public @NotNull String getSerializedName() {
        return "ability_hud";
    }


    public static class SubElement {

        public static final ResourceLocation BACKGROUND_TEXTURE = modLoc("textures/gui/hud/ability_background.png");
        private final Ability ability;
        public SubElement(Ability ability) {
            this.ability = ability;
        }

        public void render(GuiGraphics guiGraphics, float partialTick) {
//            RenderSystem.setShader(GameRenderer::getPositionTexShader);
//            RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
            int u = ability.isEnabled() ? 19 : 0;
            guiGraphics.setColor(1, 1, 1, 0.7f);
            guiGraphics.blit(BACKGROUND_TEXTURE, 0, 0, u, 0, 18, 18, 48, 48);
            guiGraphics.setColor(1, 1, 1, 1);
//            GuiComponent.blit(poseStack, 0, 0, 10, 0, 0, 18, 18, 24, 24);
            ResourceLocation texture = ability.getType().getTexture();
            if(texture != null) {
//                RenderSystem.setShaderTexture(0, texture);
                guiGraphics.blit(texture, 1, 1, 5, 0, 0, 16, 16, 16, 16);
            }

            drawProgress(guiGraphics.pose(), partialTick, 1, 1, 16, 16);
        }
        private void drawProgress(PoseStack poseStack, float partialTick, float x1, float y1, float width, float height) {
            if(ability.getCooldown() > 0) {



                ExtendedShaderInstance shader = (ExtendedShaderInstance) CybCoreShaders.CIRCLE_PROGRESS.getShaderInstance();
                float progress = (ability.getCooldown() - partialTick) / ability.getType().getMaxCooldown();
                shader.safeGetUniform("Progress").set(progress);



                RenderSystem.setShader(() -> shader);
                RenderSystem.setShaderColor(1, 1, 1, 1);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();


                Matrix4f matrix = poseStack.last().pose();
                BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                bufferbuilder.addVertex(matrix, x1, y1 + height, 20.0f).setUv(0, 0);
                bufferbuilder.addVertex(matrix, x1 + width, y1 + height, 20.0f).setUv(1, 0);
                bufferbuilder.addVertex(matrix, x1 + width, y1, 20.0f).setUv(1, 1);
                bufferbuilder.addVertex(matrix, x1, y1, 20.0f).setUv(0, 1);
                BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());


                shader.setUniformDefaults();
            }
        }
    }
}
