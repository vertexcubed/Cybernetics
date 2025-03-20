package com.vertexcubed.cybernetics.client.gui.cyberware;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.client.gui.util.ScreenHelper;
import com.vertexcubed.cybernetics.client.gui.util.ScreenState;
import com.vertexcubed.cybernetics.client.gui.util.ScreenStateMachine;
import com.vertexcubed.cybernetics.client.gui.widget.BasicWidget;
import com.vertexcubed.cybernetics.client.task.*;
import com.vertexcubed.cybernetics.client.util.FakeLocalPlayer;
import com.vertexcubed.cybernetics.client.util.RenderHelper;
import com.vertexcubed.cybernetics.common.menu.CyberwareMenu;
import com.vertexcubed.cybernetics.common.storage.CyberwareSection;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public class CyberwareScreen extends AbstractContainerScreen<CyberwareMenu> {

    public static final ResourceLocation TEXTURE = modLoc("textures/gui/cyberware/background.png");
    public static final ResourceLocation SLOT_TEXTURE = modLoc("textures/gui/cyberware/slots.png");



    private BasicWidget backButton;
    private BasicWidget entityWidget;
    private final List<BasicWidget> sectionButtons = new ArrayList<>();
    private float entityRotation;
    private float entityScale;
    private boolean canClickSectionButtons = false;
    private boolean canClickBackButton = false;

    private ScreenState mainState;
    private ScreenStateMachine stateMachine;


    private LocalPlayer fakePlayer;
    public CyberwareScreen(CyberwareMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 226;
        this.imageHeight = 154;
    }


    @Override
    protected void init() {
        super.init();

        //============
        // Back Button
        // ===========

        this.backButton = addRenderableWidget(BasicWidget
                .texture(this, leftPos + 208, topPos + 9, 9, 9, 0, 23, 32, 32, modLoc("textures/gui/cyberware/buttons.png"))
                .playSoundOnClick(true)
                .lightOnHover(true)
                .click((ctx, mouseX, mouseY, button) -> {
                    if(canClickBackButton && ctx.getAlpha() > 0.0f &&
                            (!mainState.isActive(stateMachine))) {
                        this.stateMachine.changeState(mainState);
                    }
                })
                .zOffset(300)
        );

        //==============
        // Entity Widget
        //==============

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

        ScreenHelper.getTaskManager(this).addFrameTask(moveWidget(entityWidget, leftPos + 91, topPos + 16, 20, Easing.QUARTIC_OUT));

        // ===============
        // Section Buttons
        // ===============

        List<CyberwareSection> sections = menu.getCyberware().getSections();
        Map<CyberwareSection, ScreenState> sectionStates = new HashMap<>();
        sections.forEach(section -> {
            sectionStates.put(section, ScreenState.create(this, state -> {
                int duration = 15;
                ScreenHelper.getTaskManager(this).addFrameTask(
                        new TweenTask(() -> entityRotation, (f) -> entityRotation = f, -45, duration, Easing.CUBIC_IN_OUT)
                                .withTag("section_enter")
                );
                ScreenHelper.getTaskManager(this).addFrameTask(
                        new TweenTask(entityWidget::getScale, entityWidget::setScale, 120, duration, Easing.CUBIC_IN_OUT)
                                .withTag("section_enter")
                );
                ScreenHelper.getTaskManager(this).addFrameTask(
                        moveWidget(entityWidget, leftPos + section.getType().playerX(), topPos + section.getType().playerY(), duration, Easing.CUBIC_IN_OUT)
                                .withTag("section_enter")
                );

                ScreenHelper.getTaskManager(this).addTickTask(
                        new WaitTask(20).then(() -> new InstantRunTask(() -> canClickBackButton = true))
                );



            }, state -> {
                int duration = 15;
                ScreenHelper.getTaskManager(this).addFrameTask(
                        new TweenTask(() -> entityRotation, (f) -> entityRotation = f, 0, duration, Easing.CUBIC_IN_OUT)
                                .withTag("section_exit")
                );
                ScreenHelper.getTaskManager(this).addFrameTask(
                        new TweenTask(entityWidget::getScale, entityWidget::setScale, 60, duration, Easing.CUBIC_IN_OUT)
                                .withTag("section_exit")
                );
                ScreenHelper.getTaskManager(this).addFrameTask(
                        moveWidget(entityWidget, leftPos + 91, topPos + 16, duration, Easing.CUBIC_IN_OUT)
                                .withTag("section_exit")
                );

            }));
            int pos = topPos + section.getType().y();
            BasicWidget widget = addRenderableWidget(BasicWidget
                    .texture(this, leftPos + section.getType().x(), pos + 20, 24, 24, section.getType().texture()))
                    .lightOnHover(true)
                    .playSoundOnClick(true)
                    .alpha(0.0f)
                    .click((ctx, mouseX, mouseY, partialTick) -> {
                        if(canClickSectionButtons && ctx.getAlpha() > 0.0f &&
                                (mainState.isActive(stateMachine))) {
                            this.stateMachine.changeState(sectionStates.get(section));
                        }
                    });
            this.sectionButtons.add(widget);
        });

        sectionButtons.sort((button1, button2) -> {
            int y = button1.getY() - button2.getY();
            if(y == 0) {
                return button1.getX() - button2.getX();
            }
            return y;
        });


        //===========
        // Main State
        //===========

        mainState = ScreenState.create(this, state -> {
            Cybernetics.LOGGER.debug("Entering main state");
            List<AbstractTask> moveSections = new ArrayList<>();

            for (int i = 0; i < sectionButtons.size(); i++) {
                BasicWidget widget = sectionButtons.get(i);
                widget.visible = true;
                int pos = widget.getY() - 20;
                moveSections.add(new WaitTask(i).then(r -> new TweenTask(
                        () -> (float) widget.getY(),
                        (f) -> widget.setY((int) (float) f),
                        pos,
                        10,
                        Easing.CUBIC_OUT)
                ));

                moveSections.add(new WaitTask(i)
                        .then(
                                r -> new TweenTask(
                                        widget::getAlpha,
                                        widget::setAlpha,
                                        1.0f,
                                        10,
                                        Easing.CUBIC_OUT)
                        )
                );
            }

            ScreenHelper.getTaskManager(this).addFrameTask(
                    new WaitTask(10)
                            .then(res -> new AfterAllTask(moveSections))
                            .withTag("main_enter")
            );
            ScreenHelper.getTaskManager(this).addFrameTask(
                    new WaitTask(20)
                            .then(res -> new InstantRunTask(() -> canClickSectionButtons = true))
            );
        }, state -> {
            this.canClickSectionButtons = false;
            ScreenHelper.getTaskManager(this).interruptFrameTask("main_enter");

            List<AbstractTask> alphaSections = new ArrayList<>();
            for(BasicWidget widget : sectionButtons) {
                alphaSections.add(new TweenTask(
                        widget::getAlpha,
                        widget::setAlpha,
                        0.0f,
                        10,
                        Easing.CUBIC_OUT)
                        .then(() -> new InstantRunTask(() -> {
                            widget.setY(widget.getY() + 20);
                            widget.visible = false;
                        })));
            }
            ScreenHelper.getTaskManager(this).addFrameTask(new AfterAllTask(alphaSections));
        });


        List<ScreenState> allStates = new ArrayList<>(sectionStates.values());
        allStates.add(mainState);
        stateMachine = new ScreenStateMachine(allStates);
        stateMachine.init(mainState);
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
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);

        for (int i = 0; i < menu.slots.size(); i++) {
            if(!menu.getSlot(i).isActive()) continue;
            boolean isInvSlot = menu.getSlot(i) instanceof CyberwareMenu.InventorySlot;
            boolean slotEmpty = !menu.getSlot(i).hasItem();
            int u = slotEmpty && !isInvSlot ? 23 : 0;
            int v = isInvSlot ? 19 : 0;
            guiGraphics.blit(SLOT_TEXTURE, leftPos + menu.getSlot(i).x - 5, topPos + menu.getSlot(i).y - 1, u, v, 22, 18, 64, 64);
        }
    }

    private void scissor(GuiGraphics guiGraphics) {
        guiGraphics.enableScissor(leftPos + 5, topPos + 5, leftPos + 221, topPos + 149);
    }

    //make sure to add the task thats returned by this function too!!!
    private AbstractTask moveWidget(AbstractWidget widget, int newX, int newY, int duration, Easing easing) {
        AbstractTask moveX = new TweenTask(() -> (float) widget.getX(), (x) -> widget.setX((int) (float) x), newX, duration, easing);
        AbstractTask moveY = new TweenTask(() -> (float) widget.getY(), (y) -> widget.setY((int) (float) y), newY, duration, easing);
        return new AfterAllTask(List.of(moveX, moveY));
    }
    private long gameTime() {
        return Minecraft.getInstance().level.getGameTime();
    }

    public ScreenStateMachine stateMachine() {
        return stateMachine;
    }
}
