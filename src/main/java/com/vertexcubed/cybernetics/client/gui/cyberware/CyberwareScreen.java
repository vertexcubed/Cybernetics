package com.vertexcubed.cybernetics.client.gui.cyberware;

import com.mojang.blaze3d.systems.RenderSystem;
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
import com.vertexcubed.cybernetics.common.storage.CyberwareSectionType;
import com.vertexcubed.cybernetics.server.network.C2SSwitchActiveSlotsPayload;
import com.vertexcubed.cybernetics.server.network.C2SSwitchInventoryPagePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import team.lodestar.lodestone.systems.easing.Easing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public class CyberwareScreen extends AbstractContainerScreen<CyberwareMenu> {

    public static final ResourceLocation TEXTURE = modLoc("textures/gui/cyberware/background.png");
    public static final ResourceLocation SLOT_TEXTURE = modLoc("textures/gui/cyberware/slots.png");


    private BasicWidget pageLeft;
    private BasicWidget pageRight;
    private BasicWidget backButton;
    private BasicWidget entityWidget;
    private final List<BasicWidget> sectionButtons = new ArrayList<>();
    private final List<BasicWidget> slotMasks = new ArrayList<>();
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

        //When screens are resized, init() is called again but not the constructor. Well I think at least
        ScreenHelper.getTaskManager(this).clear();

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

        //===========
        // Slot Masks
        //===========

        slotMasks.clear();
        int slotX = 36, slotY = 20;
        int rows = 4;
        for(int i = 0; i < menu.getCyberware().getLongestSectionSize(); i++) {
            slotMasks.add((maskWidget(slotX, slotY, rows, i)));
        }
        slotY = 84;
        for(int i = 0; i < 12; i++) {
            slotMasks.add((maskWidget(slotX, slotY, rows, i)));
        }

        //=============
        // Page Buttons
        //=============

        this.pageLeft = addRenderableWidget(pageButton(leftPos + 37, topPos + 67, true));
        this.pageRight = addRenderableWidget(pageButton(leftPos + 112, topPos + 67, false));



        //================
        // Section Buttons
        //================

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
                List<AbstractTask> maskFadeOut = new ArrayList<>();
                for(int i = 0; i < slotMasks.size(); i++) {
                    BasicWidget mask = slotMasks.get(i);
                    maskFadeOut.add(new WaitTask(i)
                            .then(() -> new TweenTask(false, mask::getAlpha, mask::setAlpha, 0.0f, 10, Easing.CUBIC_OUT))
                            .then(() -> new InstantRunTask(() -> {
                                mask.visible = false;
                            }))
                    );
                }
                maskFadeOut.add(new WaitTask(menu.getCyberware().getLongestSectionSize())
                        .then(() -> new InstantRunTask(() -> {
                            pageLeft.visible = true;
                            pageLeft.active = true;
                        }))
                        .then(() -> new TweenTask(pageLeft::getAlpha, pageLeft::setAlpha, 1.0f, 15))
                );
                maskFadeOut.add(new WaitTask(menu.getCyberware().getLongestSectionSize() + 2)
                        .then(() -> new InstantRunTask(() -> {
                            pageRight.visible = true;
                            pageRight.active = true;
                        }))
                        .then(() -> new TweenTask(pageRight::getAlpha, pageRight::setAlpha, 1.0f, 15))
                );


                ScreenHelper.getTaskManager(this).addFrameTask(new WaitTask(15)
                        .then(() -> new InstantRunTask(() -> {
                            slotMasks.forEach(mask -> {
                                mask.visible = true;
                                mask.setAlpha(1.0f);
                            });
                            menu.switchCyberwareSlots(section.getType());
                            PacketDistributor.sendToServer(new C2SSwitchActiveSlotsPayload(section.getType()));
                            menu.switchInventoryPage(0);
                            PacketDistributor.sendToServer(new C2SSwitchInventoryPagePayload(0));
                        }))
                        .then(() -> new AfterAllTask(maskFadeOut))
                        .withTag("section_enter")
                );




            }, state -> {
                ScreenHelper.getTaskManager(this).interruptFrameTask("section_enter");
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
                List<AbstractTask> maskFadeIn = new ArrayList<>();
                slotMasks.forEach(mask -> {
                    maskFadeIn.add(new InstantRunTask(() -> mask.visible = true)
                            .then(() -> new TweenTask(mask::getAlpha, mask::setAlpha, 1.0f, 5))
                            .then(() -> new InstantRunTask(() -> mask.visible = false))
                    );
                });

                maskFadeIn.add(new TweenTask(pageLeft::getAlpha, pageLeft::setAlpha, 0.0f, 5)
                        .then(() -> new InstantRunTask(() -> {
                            pageLeft.visible = false;
                            pageLeft.active = false;
                        }))
                );

                maskFadeIn.add(new TweenTask(pageRight::getAlpha, pageRight::setAlpha, 0.0f, 5)
                        .then(() -> new InstantRunTask(() -> {
                            pageRight.visible = false;
                            pageRight.active = false;
                        }))
                );


                ScreenHelper.getTaskManager(this).addFrameTask(new AfterAllTask(maskFadeIn)
                        .then(() -> new InstantRunTask(() -> {
                            menu.switchCyberwareSlots(null);
                            PacketDistributor.sendToServer(new C2SSwitchActiveSlotsPayload((CyberwareSectionType) null));
                            menu.switchInventoryPage(-1);
                            PacketDistributor.sendToServer(new C2SSwitchInventoryPagePayload(-1));
                        }))

                );

                canClickBackButton = false;

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

    private BasicWidget maskWidget(int slotX, int slotY, int rows, int i) {
        return BasicWidget.create(this, leftPos + slotX + ((i % rows) * 25) - 4, topPos + slotY + ((i / rows) * 21), 22, 18,
                (context, guiGraphics, mouseX, mouseY, partialTick) -> {
                    guiGraphics.blit(CyberwareScreen.TEXTURE, context.getX(), context.getY(), context.getZOffset(), 27, 9, context.getWidth(), context.getHeight(), 226, 154);
                })
                .alpha(0.0f)
                .playSoundOnClick(false)
                .visible(false)
                .zOffset(350)
                .active(false);
    }


    @Override
    protected void containerTick() {
        super.containerTick();
        fakePlayer.tickCount++;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        //Need to render separately, because for some reason I can't make them render over items otherwise.
        slotMasks.forEach(mask -> {
            mask.render(guiGraphics, mouseX, mouseY, partialTick);
        });

        this.renderTooltip(guiGraphics, mouseX, mouseY);

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

    private BasicWidget pageButton(int x, int y, boolean left) {
        return BasicWidget.create(this, x, y, 12, 9, ((context, guiGraphics, mouseX, mouseY, partialTick) -> {
            int page = menu.getInventoryPage();
            boolean canPress = left ? page > 0 : page < 2;

            int v = left ? 0 : 10;
            int u = canPress ? 0 : 13;
            guiGraphics.blit(modLoc("textures/gui/cyberware/buttons.png"), context.getX(), context.getY(), context.getWidth(), context.getHeight(), u, v, context.getWidth(), context.getHeight(), 32, 32);
        }))
                .alpha(0.0f)
                .visible(false)
                .active(false)
                .lightOnHover(true)
                .click((context, mouseX, mouseY, button) -> {
                    int page = menu.getInventoryPage();
                    boolean canPress = left ? page > 0 : page < 2;
                    if(canPress) {
                        int newPage = left ? page - 1 : page + 1;
                        menu.switchInventoryPage(newPage);
                        PacketDistributor.sendToServer(new C2SSwitchInventoryPagePayload(newPage));
                    }
                })
        ;
    }


    private long gameTime() {
        return Minecraft.getInstance().level.getGameTime();
    }

    public ScreenStateMachine stateMachine() {
        return stateMachine;
    }
}
