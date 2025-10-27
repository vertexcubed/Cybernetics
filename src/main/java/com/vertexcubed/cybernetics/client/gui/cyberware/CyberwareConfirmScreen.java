package com.vertexcubed.cybernetics.client.gui.cyberware;

import com.mojang.blaze3d.systems.RenderSystem;
import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.client.gui.widget.TextWidget;
import com.vertexcubed.cybernetics.common.menu.CyberwareMenu;
import com.vertexcubed.cybernetics.common.registry.CybSoundEvents;
import com.vertexcubed.cybernetics.server.network.C2SApplyCyberwarePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import vertexcubed.vrtex.client.screen.ScreenHelper;
import vertexcubed.vrtex.client.screen.widget.BasicWidget;
import vertexcubed.vrtex.common.math.Easing;
import vertexcubed.vrtex.common.task.TweenTask;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public class CyberwareConfirmScreen extends Screen {

    private static final ResourceLocation TEXTURE = modLoc("textures/gui/cyberware/confirm_background.png");

    private int leftPos;
    private int topPos;
    private int imageWidth;
    private int imageHeight;
    private long time;
    private TextWidget textWidget;

    private BasicWidget barLeft;
    private BasicWidget barRight;
    private int scissorX;
    public CyberwareConfirmScreen() {
        super(Component.literal("Confirmation"));
        imageWidth = 91;
        imageHeight = 65;
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        textWidget = new TextWidget(leftPos + 8, topPos + 12, 85);
        textWidget.setText(Component.translatable("tooltip.cybernetics.confirm"));
        textWidget.setColor(0xff00fff7);

        addRenderableWidget(button(leftPos + 9, topPos + 39, true));
        addRenderableWidget(button(leftPos + 65, topPos + 39, false));

        barLeft = BasicWidget
                .texture(this, leftPos + 45 - 5, topPos, 5, 65,0 ,0, 128, 128, TEXTURE)
                .zOffset(300)
        ;

        barRight = BasicWidget
                .texture(this, leftPos + 46, topPos, 5, 65,86,0, 128, 128, TEXTURE)
                .zOffset(300)
        ;



        Minecraft.getInstance().player.playSound(CybSoundEvents.CYBERWARE_CONFIRM.get(), 1.0f, 1.0f);

        scissorX = 0;
        ScreenHelper.getTaskManager(this).addFrameTask(
                new TweenTask(() -> (float) scissorX, (f) -> scissorX = (int) (float) f, 40, 10, Easing.CUBIC_IN_OUT)
        );
        ScreenHelper.getTaskManager(this).addFrameTask(
                new TweenTask(() -> (float) barLeft.getX(), (f) -> barLeft.setX((int) (float) f), leftPos, 10, Easing.CUBIC_IN_OUT)
        );
        ScreenHelper.getTaskManager(this).addFrameTask(
                new TweenTask(() -> (float) barRight.getX(), (f) -> barRight.setX((int) (float) f), leftPos + imageWidth - 5, 10, Easing.CUBIC_IN_OUT)
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        textWidget.tick(time);
        time++;
    }

    private BasicWidget button(int x, int y, boolean isConfirm) {
        return BasicWidget.create(this, x, y, 17, 17, ((context, guiGraphics, mouseX, mouseY, partialTick) -> {
                    int v = isConfirm ? 30 : 12;
                    guiGraphics.blit(TEXTURE, context.getX(), context.getY(), context.getWidth(), context.getHeight(), 93, v, context.getWidth(), context.getHeight(), 128, 128);
        }))
                .lightOnHover(true)
                .playSoundOnClick(true)
                .customClickSound(CybSoundEvents.CYBERWARE_BUTTON.get(), 0.75f)
                .click((context, guiGraphics, mouseX, mouseY) -> {
                    if(isConfirm) {
                        if(this.minecraft.player.containerMenu instanceof CyberwareMenu menu) {
                            menu.applyChanges(this.minecraft.player);
                            PacketDistributor.sendToServer(new C2SApplyCyberwarePayload());
                        }
                        else {
                            Cybernetics.LOGGER.error("Couldn't apply cyberware changes: menu is not CyberwareMenu!");
                        }
                    }
                    this.minecraft.popGuiLayer();
                    this.minecraft.player.closeContainer();
                    this.minecraft.popGuiLayer();
                })
        ;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().player.playSound(CybSoundEvents.CYBERWARE_CLOSE.get());
        super.onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        RenderSystem.enableDepthTest();
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.enableScissor(leftPos + 45 - scissorX - 5, topPos, leftPos + 46 + scissorX + 5, topPos + imageHeight);

        renderBg(guiGraphics, partialTick, mouseX, mouseY);

        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.8888f, 0.8888f, 0);
        guiGraphics.pose().translate(0.125 * textWidget.getX(), 0.125 * textWidget.getY(), 0);
        textWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.pose().popPose();

        barLeft.render(guiGraphics, mouseX, mouseY, partialTick);
        barRight.render(guiGraphics, mouseX, mouseY, partialTick);


        guiGraphics.disableScissor();
    }

    public void renderBg(GuiGraphics guiGraphics, float frameTimeDelta, int mouseX, int mouseY) {
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        RenderSystem.setShaderTexture(0, TEXTURE);
//        RenderHelper.resetShaderColor();
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 128, 128);




    }

}
