package com.lowdragmc.lowdraglib.emi;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStackInteraction;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.screen.EmiScreenManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author KilaBash
 * @date 2023/4/3
 * @implNote ModularWrapperWidget
 */
@OnlyIn(Dist.CLIENT)
public class ModularWrapperWidget extends Widget implements ContainerEventHandler {
    @Nullable
    private GuiEventListener focused;
    private boolean isDragging;
    public final ModularWrapper<?> modular;
    public final List<Widget> slots;
    private int lastX, lastY;

    public ModularWrapperWidget(ModularWrapper<?> modular, List<Widget> slots) {
        this.modular = modular;
        this.slots = slots;
    }

    @Override
    public Bounds getBounds() {
        return new Bounds(0, 0, modular.getWidget().getSize().width, modular.getWidget().getSize().height);
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.lastX = pMouseX;
        this.lastY = pMouseY;
        modular.draw(graphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
        if (modular.tooltipTexts != null && !modular.tooltipTexts.isEmpty()) {
            List<ClientTooltipComponent> tooltips = modular.tooltipTexts.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).collect(Collectors.toList());
            if (modular.tooltipComponent != null) {
                tooltips.add(DrawerHelper.getClientTooltipComponent(modular.tooltipComponent));
            }
            return tooltips;
        }
        return super.getTooltip(mouseX, mouseY);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return Collections.emptyList();
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return modular.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (modular.mouseClicked(mouseX + modular.getLeft(), mouseY + modular.getTop(), button)) {
            return true;
        }
        for (Widget slot : slots) {
            if (slot.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        return modular.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        modular.mouseMoved(pMouseX, pMouseY);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return modular.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        return modular.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        modular.focused = false;
        if (modular.modularUI.mainGroup.keyPressed(pKeyCode, pScanCode, pModifiers)) {
            return true;
        }
        for (Widget slot : slots) {
            if (slot instanceof ModularSlotWidget slotWidget) {
                if (slotWidget.getBounds().contains(lastX, lastY)) {
                    if (slotWidget.slotInteraction(bind -> bind.matchesKey(pKeyCode, pScanCode))) {
                        return true;
                    }
                    if (slotWidget.getSlot().getXEIIngredientOverMouse(lastX + modular.getLeft(), lastY + modular.getTop()) instanceof EmiIngredient ingredient) {
                        return EmiScreenManager.stackInteraction(new EmiStackInteraction(ingredient, slotWidget.getRecipe(), true),
                                bind -> bind.matchesKey(pKeyCode, pScanCode));
                    }
                }
            }
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        return modular.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        return modular.charTyped(pCodePoint, pModifiers);
    }

    @Override
    public boolean isDragging() {
        return this.isDragging;
    }

    @Override
    public void setDragging(boolean isDragging) {
        this.isDragging = isDragging;
    }

    @Override
    @Nullable
    public GuiEventListener getFocused() {
        return this.focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        this.focused = focused;
    }
}
