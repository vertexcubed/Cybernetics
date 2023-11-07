package com.vivi.cybernetics.client.gui.cyberware;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vivi.cybernetics.Cybernetics;
import com.vivi.cybernetics.client.gui.CyberwareScreenOld;
import com.vivi.cybernetics.client.gui.util.CapacityGuiComponent;
import com.vivi.cybernetics.client.gui.util.CybAbstractContainerScreen;
import com.vivi.cybernetics.client.gui.util.TextWidget;
import com.vivi.cybernetics.client.util.Easing;
import com.vivi.cybernetics.client.util.MouseHelper;
import com.vivi.cybernetics.client.util.ScreenHelper;
import com.vivi.cybernetics.common.menu.CyberwareMenu;
import com.vivi.cybernetics.common.menu.CyberwareSlot;
import com.vivi.cybernetics.common.menu.InventorySlot;
import com.vivi.cybernetics.server.network.CybPackets;
import com.vivi.cybernetics.server.network.packet.C2SSwitchPagePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CyberwareScreen<T extends CyberwareMenu> extends CybAbstractContainerScreen<T> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Cybernetics.MOD_ID, "textures/gui/cyberware/background.png");
    public static final ResourceLocation SLOT_TEXTURE = new ResourceLocation(Cybernetics.MOD_ID, "textures/gui/cyberware/slots.png");

    private State currentState;
    private Slot slot;

    //components/widgets
    private EntityWidget entityWidget;
    private TextWidget textWidget;
    private ArrowWidget arrowWidget;
    private CapacityGuiComponent capacityComponent;
    private final List<SectionButton> sectionButtons = new ArrayList<>();
    private final List<MaskWidget> itemMasks = new ArrayList<>();
    private PageButton pageButtonLeft;
    private PageButton pageButtonRight;


    //misc
    private LocalPlayer fakePlayer;
    public CyberwareScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 226;
        this.imageHeight = 154;
    }

    @Override
    protected void init() {
        super.init();
        LocalPlayer player = Minecraft.getInstance().player;
        fakePlayer = new LocalPlayer(Minecraft.getInstance(), Minecraft.getInstance().level, player.connection, player.getStats(), player.getRecipeBook(), false, false);


        //components/widgets
        capacityComponent = new CapacityGuiComponent(leftPos + 7, topPos + 7);


        entityWidget = new EntityWidget(leftPos + 91, topPos - 136, 60, fakePlayer)
                .setBox(leftPos + 5, leftPos + 221, topPos + 5, topPos + 149);
        moveWidget(entityWidget, leftPos + 91, topPos + 16, 20, Easing.QUART_OUT);
        addRenderableWidget(entityWidget);


        sectionButtons.clear();
        menu.getCyberware().getSections().forEach(section -> {
            SectionButton button = new SectionButton(this, leftPos + section.getType().getX(), topPos + section.getType().getY() + 20, section)
                    .setBox(leftPos + 5, leftPos + 221, topPos + 5, topPos + 149);
            sectionButtons.add(button);
            addRenderableWidget(button);
        });
        sectionButtons.sort((button1, button2) -> {
            int y = button1.y - button2.y;
            if(y == 0) {
                return button1.x - button2.x;
            }
            return y;
        });


        itemMasks.clear();
        int slotX = 36, slotY = 20;
        int rows = 4;
        for(int i = 0; i < menu.getCyberware().getLongestSectionSize(); i++) {
            MaskWidget widget = new MaskWidget(leftPos + slotX + ((i % rows) * 25) - 4, topPos + slotY + ((i / rows) * 21));
            itemMasks.add(widget);
        }
        slotY = 84;
        for(int i = 0; i < 12; i++) {
            MaskWidget widget = new MaskWidget(leftPos + slotX + ((i % rows) * 25) - 4, topPos + slotY + ((i / rows) * 21));
            itemMasks.add(widget);
        }


        textWidget = new TextWidget(this, leftPos + 32, topPos + 9);
        addRenderableWidget(textWidget);


        addRenderableWidget(new BackButton(this, leftPos + 208, topPos + 9));

        setCurrentState(new State.Main(this));


        pageButtonLeft = new PageButton(this, leftPos + 37, topPos + 67, true);
        pageButtonRight = new PageButton(this, leftPos + 112, topPos + 67, false);
        addRenderableWidget(pageButtonLeft);
        addRenderableWidget(pageButtonRight);


        arrowWidget = new ArrowWidget(leftPos + 77, topPos + 62);
        addRenderableWidget(arrowWidget);
    }

    @Override
    public void onClose() {
        if(canEdit() && menu.hasModified()) {
            this.minecraft.pushGuiLayer(new CyberwareConfirmScreen(Component.literal("Confirmation")));
        }
        else {
            super.onClose();
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        fakePlayer.tickCount++;
        textWidget.tick(time);
        currentState.tick(time);
    }

    public void updateArrow(Slot slot) {
        Cybernetics.LOGGER.info("Updating arrow");
        if(slot == null || slot.getItem().isEmpty()) {
            return;
        }
        else if(slot instanceof CyberwareSlot) {
            if(getMenu().hasDependents(slot.getItem())) {
                arrowWidget.setMode(ArrowWidget.Mode.OUT_ERROR);
            }
            else {
                arrowWidget.setMode(ArrowWidget.Mode.OUT_OK);
            }
        }
        else if(slot instanceof InventorySlot) {
            int firstEnabledSlot = -1;
            for(int i = 0; i < menu.getCyberware().getSlots(); i++) {
                if(menu.getSlot(i).isActive() && menu.getSlot(i).getItem().isEmpty()) {
                    firstEnabledSlot = i;
                    break;
                }
            }
            if(firstEnabledSlot != -1 && menu.getCyberware().isItemValid(firstEnabledSlot, slot.getItem())) {
                arrowWidget.setMode(ArrowWidget.Mode.IN_OK);
            }
            else {
                arrowWidget.setMode(ArrowWidget.Mode.IN_ERROR);
            }
        }
    }

    public void setCurrentState(State state) {
        if(currentState != null) currentState.exit();
        this.currentState = state;
        currentState.enter();
    }

    public State getCurrentState() {
        return currentState;
    }

    public void rotateEntity(EntityWidget widget, float rotation, int duration) {
        ScreenHelper.addAnimation(this, widget::getRotation, widget::setRotation, rotation, duration);
    }

    public void rotateEntity(EntityWidget widget, float rotation, int duration, Easing easing) {
        ScreenHelper.addAnimation(this, widget::getRotation, widget::setRotation, rotation, duration, easing);
    }

    public boolean canEdit() {
        return menu.canEdit();
    }
    public List<SectionButton> getSectionButtons() {
        return sectionButtons;
    }

    public List<MaskWidget> getItemMasks() {
        return itemMasks;
    }

    public EntityWidget getEntityWidget() {
        return entityWidget;
    }

    public TextWidget getTextWidget() {
        return textWidget;
    }

    public ArrowWidget getArrowWidget() {
        return arrowWidget;
    }

    public PageButton getPageButtonLeft() {
        return pageButtonLeft;
    }

    public PageButton getPageButtonRight() {
        return pageButtonRight;
    }

    public int getCurrentPage() {
        return getMenu().getCurrentPage();
    }

    public void setCurrentPage(int page) {
        this.getMenu().switchInventoryPage(page);
        CybPackets.sendToServer(new C2SSwitchPagePacket(page));
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float frameTimeDelta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, frameTimeDelta);
        currentState.render(pPoseStack, getPartialTick());
        renderTooltip(pPoseStack, pMouseX, pMouseY);
        if(slot != hoveredSlot) {
            slot = hoveredSlot;
            //update arrow
            updateArrow(slot);
        }

        itemMasks.forEach(mask -> mask.render(pPoseStack, pMouseX, pMouseY, frameTimeDelta));

    }

    @Override
    protected void renderBg(PoseStack poseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        this.blit(poseStack, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);

        RenderSystem.setShaderTexture(0, SLOT_TEXTURE);

        for (int i = 0; i < menu.slots.size(); i++) {
            if(!menu.getSlot(i).isActive()) continue;
            boolean isInvSlot = menu.getSlot(i) instanceof InventorySlot;
            boolean slotEmpty = !menu.getSlot(i).hasItem();
            int u = slotEmpty && !isInvSlot ? 23 : 0;
            int v = isInvSlot ? 19 : 0;
            blit(poseStack, leftPos + menu.getSlot(i).x - 5, topPos + menu.getSlot(i).y - 1, u, v, 22, 18, 64, 64);
        }

        capacityComponent.draw(poseStack, menu.getStoredCapacity(), menu.getMaxCapacity());

    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        if(MouseHelper.isHovering(capacityComponent.x, capacityComponent.y, capacityComponent.width, capacityComponent.height, mouseX, mouseY)) {
            renderTooltip(poseStack, capacityComponent.getTooltip(menu.getStoredCapacity(), menu.getMaxCapacity()), Optional.empty(), mouseX - leftPos, mouseY - topPos);
        }
    }
}
