package com.vertexcubed.cybernetics.client.gui.cyberware;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.client.gui.widget.BasicWidget;
import com.vertexcubed.cybernetics.common.menu.CyberwareMenu;
import com.vertexcubed.cybernetics.server.network.C2SApplyCyberwarePayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Iterator;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public class CyberwareConfirmScreen extends Screen {

    private static final ResourceLocation TEXTURE = modLoc("textures/gui/cyberware/confirm_background.png");

    private int leftPos;
    private int topPos;
    private int imageWidth;
    private int imageHeight;
    private long time;


    public CyberwareConfirmScreen() {
        super(Component.literal("Confirmation"));
        imageWidth = 91;
        imageHeight = 65;
        Cybernetics.LOGGER.info("teehee");
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;


        addRenderableWidget(button(leftPos + 9, topPos + 39, true));
        addRenderableWidget(button(leftPos + 65, topPos + 39, false));


    }


    private BasicWidget button(int x, int y, boolean isConfirm) {
        return BasicWidget.create(this, x, y, 17, 17, ((context, guiGraphics, mouseX, mouseY, partialTick) -> {
                    int v = isConfirm ? 30 : 12;
                    guiGraphics.blit(TEXTURE, context.getX(), context.getY(), context.getWidth(), context.getHeight(), 93, v, context.getWidth(), context.getHeight(), 128, 128);
        }))
                .lightOnHover(true)
                .click((context, guiGraphics, mouseX, mouseY) -> {
                    if(isConfirm) {
                        if(this.minecraft.player.containerMenu instanceof CyberwareMenu menu) {
                            menu.applyChanges(this.minecraft.player);
                            PacketDistributor.sendToServer(new C2SApplyCyberwarePayload());
                        }
                        else {
                            Cybernetics.LOGGER.error("Couldn't apply changes: menu is not CyberwareMenu!");
                        }
                    }
                    this.onClose();
                })
        ;
    }


    @Override
    public void onClose() {
        this.minecraft.popGuiLayer();
        this.minecraft.player.closeContainer();
        this.minecraft.popGuiLayer();
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        renderBg(guiGraphics, partialTick, mouseX, mouseY);

        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    public void renderBg(GuiGraphics guiGraphics, float frameTimeDelta, int mouseX, int mouseY) {
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        RenderSystem.setShaderTexture(0, TEXTURE);
//        RenderHelper.resetShaderColor();
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 128, 128);
    }

}
