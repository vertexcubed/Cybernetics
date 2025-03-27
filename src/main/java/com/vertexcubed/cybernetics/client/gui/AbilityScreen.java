package com.vertexcubed.cybernetics.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.client.gui.util.ScreenHelper;
import com.vertexcubed.cybernetics.client.gui.widget.CybAbstractWidget;
import com.vertexcubed.cybernetics.client.gui.widget.TextWidget;
import com.vertexcubed.cybernetics.client.task.TweenTask;
import com.vertexcubed.cybernetics.client.util.InputHelper;
import com.vertexcubed.cybernetics.common.ability.Ability;
import com.vertexcubed.cybernetics.common.registry.CybAbilities;
import com.vertexcubed.cybernetics.common.registry.CybAttachments;
import com.vertexcubed.cybernetics.common.registry.CybTags;
import com.vertexcubed.cybernetics.common.storage.AbilityStorage;
import com.vertexcubed.cybernetics.common.util.AbilityHelper;
import com.vertexcubed.cybernetics.common.util.Maath;
import com.vertexcubed.cybernetics.server.network.BidirectionalAbilityEventPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;
import team.lodestar.lodestone.systems.easing.Easing;

import java.util.List;


// TODO: major refactor and cleanup. fix math
// The code of this fucking screen makes my head hurt so much. I hate it. I need to redo it. Fuck.
public class AbilityScreen extends Screen {

    public static final int SLICES = 128;
    private float centerX;
    private float centerY;
    private long time;

    private Player player;
    private TextWidget textWidget;
    public AbilityScreen() {
        super(Component.literal("Abilities"));
    }

    @Override
    protected void init() {
        super.init();
        time = 0L;
        player = Minecraft.getInstance().player;
        centerX = minecraft.getWindow().getGuiScaledWidth() / 2.0f;
        centerY = minecraft.getWindow().getGuiScaledHeight() / 2.0f;

        AbilityStorage abilities = player.getData(CybAttachments.ABILITY_STORAGE);
        List<Ability> filtered = abilities.getAbilities().stream().filter(ability -> !ability.getType().is(CybTags.HIDDEN_ABILITIES)).toList();
        int sections = filtered.size();
        float length = 360.0f / sections;
        for(int i = 0; i < sections; i++) {
            addRenderableWidget(new AbilitySlice(filtered.get(i), 60, 100, length * i, length));
        }
        textWidget = new TextWidget((int) centerX, (int) centerY);
        textWidget.setY(textWidget.getY() - textWidget.getTextHeight() / 2);
        addRenderableWidget(textWidget);
        textWidget.setText(Component.literal("Abilities"));
    }

    @Override
    public void tick() {
        time++;
        textWidget.tick(time);
        textWidget.setX((int) centerX - (textWidget.getTextWidth() / 2));
        this.renderables.forEach(widget -> {
            if(widget instanceof AbilitySlice slice) {
                slice.setSelected(slice.isHoveredOrFocused() && slice.getAbility().getCooldown() == -1);
            }
        });

//        Cybernetics.LOGGER.info("Key down: " + CybKeybinds.PLAYER_CYBERWARE_MENU.isDown());
        if(!InputHelper.isAbilityKeyHeld()) {
            minecraft.setScreen(null);
        }
//        if(!CybKeybinds.PLAYER_CYBERWARE_MENU.isDown()) {
//            minecraft.setScreen(null);
//        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
//        RenderSystem.disableTexture();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);


//        float length = 60;
//        for(int i = 0; i < 6; i++) {
//            RenderSystem.setShaderColor((i + 1) / 6.0f, 0.0f, 0.0f, 1.0f);
//            drawAnnulus(poseStack, 40 + (4 * i), 80 + (4 * i), length * i, length * (i+1));
//        }

//        drawTorus(poseStack, 40, 80, 60, 120);

        RenderSystem.setShaderColor(0.45f, 0.05f, 0.05f, 0.75f);
        drawAnnulus(guiGraphics, 53, 55);


//        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {

    }

    public void updateText(Component text) {
        textWidget.setText(text, true);
        textWidget.setX((int) centerX - (textWidget.getTextWidth() / 2));
    }

    private void drawAnnulus(GuiGraphics guiGraphics, float innerRadius, float outerRadius) {
        drawAnnulus(guiGraphics, innerRadius, outerRadius, 0, 360);
    }
    private void drawAnnulus(GuiGraphics guiGraphics, float innerRadius, float outerRadius, float startAngle, float stopAngle) {
        PoseStack poseStack = guiGraphics.pose();


        float totalAngle = stopAngle - startAngle;

        //percent of total slices to use
        float slices = SLICES * (totalAngle / 360.0F);

        poseStack.pushPose();
        poseStack.translate(centerX, centerY, 0.0f);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder vertexBuffer = tesselator.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION);
        Matrix4f matrix4f = poseStack.last().pose();

        float angle = Mth.DEG_TO_RAD * (totalAngle / slices);
        float startRad = Mth.DEG_TO_RAD * startAngle;
        for(int i = 0; i <= slices; i++) {

            float offset = i;
            if(i + 1 > slices) {
                offset = slices;
            }

            float cos = Mth.cos(-(startRad + angle * offset));
            float sin = Mth.sin(-(startRad + angle * offset));
            vertexBuffer.addVertex(matrix4f, innerRadius * cos, innerRadius * sin, 0);
            vertexBuffer.addVertex(matrix4f, outerRadius * cos, outerRadius * sin, 0);
        }



        //SHAPES DRAW IN COUNTER CLOCKWISE MOTION!

//        vertexBuffer.vertex(matrix4f, 1.0f, 200.0f, 0.0f).endVertex();
//        vertexBuffer.vertex(matrix4f, 100.0f, 200.0f, 0.0f).endVertex();
//        vertexBuffer.vertex(matrix4f, 100.0f, 1.0f, 0.0f).endVertex();
//        vertexBuffer.vertex(matrix4f, 1.0f, 1.0f, 0.0f).endVertex();

        BufferUploader.drawWithShader(vertexBuffer.buildOrThrow());


        poseStack.popPose();
    }

    @Override
    public void removed() {
        this.renderables.forEach(widget -> {
            if(widget instanceof AbilitySlice slice && slice.isSelected()) {
                BidirectionalAbilityEventPayload.Mode opCode;
                if(slice.getAbility().isEnabled()) {
                    opCode = BidirectionalAbilityEventPayload.Mode.DISABLE;
                    AbilityHelper.disableAbility(player, slice.getAbility().getType());
                }
                else {
                    opCode = BidirectionalAbilityEventPayload.Mode.ENABLE;
                    AbilityHelper.enableAbility(player, slice.getAbility().getType());
                }
                PacketDistributor.sendToServer(new BidirectionalAbilityEventPayload(opCode, slice.getAbility().getType(), player.getId()));
            }
        });
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }



    public class AbilitySlice extends CybAbstractWidget {

        private boolean selected;
        private float inner;
        private float outer;
        private float startAngle;
        private float totalAngle;
        private final Ability ability;
        public AbilitySlice(Ability ability, float inner, float outer, float startAngle, float totalAngle) {
            super((int) centerX, (int) centerY, 1, 1);
            this.playSound = false;
            this.inner = inner;
            this.outer = outer;
            this.startAngle = startAngle;
            this.totalAngle = totalAngle;
            this.alpha = 0.75f;
            this.ability = ability;
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

            float mouseR = Maath.toRadius(pMouseX - centerX, -(pMouseY - centerY));
            float mouseT = Mth.RAD_TO_DEG * Maath.toAngle(pMouseX - centerX, -(pMouseY - centerY));

            this.isHovered = (mouseR >= 55) && (mouseT >= startAngle) && (mouseT < (startAngle + totalAngle));

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
//            RenderSystem.disableTexture();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

            if(ability.isEnabled()) {
                RenderSystem.setShaderColor(0.0f, 1.0f, 0.968f, alpha);
            }
            else {
                RenderSystem.setShaderColor(0.45f, 0.05f, 0.05f, alpha);
            }
            drawAnnulus(guiGraphics, inner, outer, startAngle, startAngle + totalAngle);

            if(ability.getCooldown() > 0) {
                RenderSystem.setShaderColor(0.25f, 0.25f, 0.25f, alpha);
                drawAnnulus(guiGraphics, inner, outer, startAngle + ((totalAngle / 2) * (1 - ((float) ability.getCooldown() / ability.getType().getMaxCooldown()))), startAngle + totalAngle - ((totalAngle / 2) * (1 - ((float) ability.getCooldown() / ability.getType().getMaxCooldown()))));
            }

//            RenderSystem.enableTexture();
            RenderSystem.disableBlend();

            //render item
            ResourceLocation texture = ability.getType().getTexture();
            if(texture != null) {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, texture);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                float centerAngle = Mth.DEG_TO_RAD * (startAngle + (totalAngle / 2));
                float centerRadius = inner + (outer - inner) / 2;
                float scale = 2.0f;
                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(scale, scale, 1.0f);
                guiGraphics.pose().translate((Maath.toX(centerRadius, centerAngle) + centerX) / scale, (-Maath.toY(centerRadius, centerAngle) + centerY) / scale, 0.0);
                guiGraphics.blit(texture, -8, -8, 200, 0, 0, 16, 16, 16, 16);
                guiGraphics.pose().popPose();
                RenderSystem.applyModelViewMatrix();

            }
        }

        public void setSelected(boolean selected) {
            if(this.selected != selected) {
                if(selected) {
                    ScreenHelper.getTaskManager(AbilityScreen.this).interruptFrameTask("deselected");
                    ScreenHelper.getTaskManager(AbilityScreen.this).addFrameTask(new TweenTask(this::getInner, this::setInner, 70, 7, Easing.CIRC_OUT).withTag("selected"));
                    ScreenHelper.getTaskManager(AbilityScreen.this).addFrameTask(new TweenTask(this::getOuter, this::setOuter, 110, 7, Easing.CIRC_OUT).withTag("selected"));
                    ScreenHelper.getTaskManager(AbilityScreen.this).addFrameTask(new TweenTask(this::getAlpha, this::setAlpha, 0.9f, 7, Easing.CIRC_OUT).withTag("selected"));
                    ResourceLocation abilityRLoc = CybAbilities.ABILITY_TYPE_REGISTRY.getKey(ability.getType());
                    AbilityScreen.this.updateText(Component.translatable("tooltip." + abilityRLoc.getNamespace() + ".ability." + abilityRLoc.getPath()));
                }
                else {
                    ScreenHelper.getTaskManager(AbilityScreen.this).interruptFrameTask("selected");
                    ScreenHelper.getTaskManager(AbilityScreen.this).addFrameTask(new TweenTask(this::getInner, this::setInner, 60, 10, Easing.QUAD_OUT).withTag("deselected"));
                    ScreenHelper.getTaskManager(AbilityScreen.this).addFrameTask(new TweenTask(this::getOuter, this::setOuter, 100, 10, Easing.QUAD_OUT).withTag("deselected"));
                    ScreenHelper.getTaskManager(AbilityScreen.this).addFrameTask(new TweenTask(this::getAlpha, this::setAlpha, 0.75f, 7, Easing.CIRC_OUT).withTag("deselected"));
                }
            }
            this.selected = selected;
        }

        public boolean isSelected() {
            return selected;
        }

        public float getInner() {
            return inner;
        }

        public float getOuter() {
            return outer;
        }
        public float getAlpha() {
            return alpha;
        }

        public void setInner(float inner) {
            this.inner = inner;
        }

        public void setOuter(float outer) {
            this.outer = outer;
        }

        public Ability getAbility() {
            return ability;
        }
    }
}
