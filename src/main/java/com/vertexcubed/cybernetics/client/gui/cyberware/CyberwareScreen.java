package com.vertexcubed.cybernetics.client.gui.cyberware;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.client.gui.util.ScreenHelper;
import com.vertexcubed.cybernetics.client.gui.widget.BasicWidget;
import com.vertexcubed.cybernetics.client.task.AbstractTask;
import com.vertexcubed.cybernetics.client.task.AfterAllTask;
import com.vertexcubed.cybernetics.client.task.TweenTask;
import com.vertexcubed.cybernetics.client.task.WaitTask;
import com.vertexcubed.cybernetics.client.util.FakeLocalPlayer;
import com.vertexcubed.cybernetics.client.util.RenderHelper;
import com.vertexcubed.cybernetics.common.menu.CyberwareMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import team.lodestar.lodestone.systems.easing.Easing;

import java.util.ArrayList;
import java.util.List;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public class CyberwareScreen extends AbstractContainerScreen<CyberwareMenu> {

    public static final ResourceLocation TEXTURE = modLoc("textures/gui/cyberware/background.png");
    public static final ResourceLocation SLOT_TEXTURE = modLoc("textures/gui/cyberware/slots.png");


    private BasicWidget backButton;
    private BasicWidget entityWidget;
    private final List<BasicWidget> sectionButtons = new ArrayList<>();
    private float entityRotation;


    private LocalPlayer fakePlayer;
    public CyberwareScreen(CyberwareMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 226;
        this.imageHeight = 154;
    }


    @Override
    protected void init() {
        super.init();

        this.backButton = addRenderableWidget(BasicWidget
                .texture(this, leftPos + 208, topPos + 9, 9, 9, 0, 23, 32, 32, modLoc("textures/gui/cyberware/buttons.png"))
                .playSoundOnClick(true)
                .lightOnHover(true)
                .click((ctx, mouseX, mouseY, button) -> {

                })
        );

        LocalPlayer player = Minecraft.getInstance().player;
        this.fakePlayer = new FakeLocalPlayer(Minecraft.getInstance(), Minecraft.getInstance().level, player);


        this.entityRotation = 0.0f;
        this.entityWidget = addRenderableWidget(BasicWidget
                .create(this, leftPos + 91, topPos - 136, 60, 120,
                        (context, graphics, mouseX, mouseY, partialTick) -> {
                            graphics.pose().pushPose();
                            graphics.pose().translate(0, 0, context.getZOffset());
                            scissor(graphics);
                            RenderHelper.renderEntity(fakePlayer, graphics.pose(), context.getX() + context.getScale()/2, context.getY() + context.getScale()*2, 20, context.getScale(), entityRotation);
                            graphics.pose().popPose();
                            graphics.disableScissor();
                        })
                .zOffset(150)
        );
        entityWidget.setScale(60);

        ScreenHelper.getTaskManager(this).addFrameTask(gameTime(), moveWidget(entityWidget, leftPos + 91, topPos + 16, gameTime(), 20, Easing.QUARTIC_OUT));


        List<AbstractTask<Float>> moveSections = new ArrayList<>();
        menu.getCyberware().getSections().forEach(section -> {
            int pos = topPos + section.getType().y();
            BasicWidget widget = addRenderableWidget(BasicWidget
                    .texture(this, leftPos + section.getType().x(), pos + 20, 24, 24, section.getType().texture()))
                    .lightOnHover(true)
                    .playSoundOnClick(true)
            ;
            this.sectionButtons.add(widget);
            moveSections.add(new TweenTask(() -> (float) widget.getY(), (f) -> widget.setY((int) (float )f), pos, 10, Easing.CUBIC_OUT));

        });

//        if(true) return;

        ScreenHelper.getTaskManager(this).addFrameTask(gameTime(),
                new WaitTask<>(gameTime() + 20, AbstractTask.NONE)
                        .onComplete(res -> {
//                            Cybernetics.LOGGER.debug("Moving section buttons");
//                            Cybernetics.LOGGER.debug("moveSections size: " + moveSections.size());
                            return new AfterAllTask<>(moveSections);
                        })
        );



    }

    @Override
    protected void containerTick() {
        super.containerTick();
        fakePlayer.tickCount++;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);

    }

    private void scissor(GuiGraphics guiGraphics) {
        guiGraphics.enableScissor(leftPos + 5, topPos + 5, leftPos + 221, topPos + 149);
    }

    //make sure to add the task thats returned by this function too!!!
    private AbstractTask<?> moveWidget(AbstractWidget widget, int newX, int newY, long startTime, int duration, Easing easing) {
        AbstractTask<Float> moveX = new TweenTask(() -> (float) widget.getX(), (x) -> widget.setX((int) (float) x), newX, duration, easing);
        AbstractTask<Float> moveY = new TweenTask(() -> (float) widget.getY(), (y) -> widget.setY((int) (float) y), newY, duration, easing);
        return new AfterAllTask<>(List.of(moveX, moveY));
    }
   private long gameTime() {
        return Minecraft.getInstance().level.getGameTime();
    }
}
