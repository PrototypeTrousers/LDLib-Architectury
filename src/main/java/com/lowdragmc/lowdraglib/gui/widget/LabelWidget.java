package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.editor.annotation.ConfigSetter;
import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberColor;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import lombok.Setter;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

@Configurable(name = "ldlib.gui.editor.register.widget.label", collapse = false)
@LDLRegister(name = "label", group = "widget.basic")
public class LabelWidget extends Widget implements IConfigurableWidget {

    @Setter
    @Nonnull
    protected Supplier<String> textSupplier;

    @Setter
    @Nullable
    protected Component component;

    @Configurable(name = "ldlib.gui.editor.name.text")
    private String lastTextValue = "";

    @Configurable
    @NumberColor
    private int color;

    @Configurable
    private boolean dropShadow;

    public LabelWidget() {
        this(0, 0, "label");
    }

    public LabelWidget(int xPosition, int yPosition, String text) {
        this(xPosition, yPosition, ()->text);
    }

    public LabelWidget(int xPosition, int yPosition, Component component) {
        super(new Position(xPosition, yPosition), new Size(10, 10));
        setDropShadow(true);
        setTextColor(-1);
        this.component = component;
        if (isRemote()) {
            lastTextValue = component.getString();
            updateSize();
        }
    }

    public LabelWidget(int xPosition, int yPosition, Supplier<String> text) {
        super(new Position(xPosition, yPosition), new Size(10, 10));
        setDropShadow(true);
        setTextColor(-1);
        this.textSupplier = text;
        if (isRemote()) {
            lastTextValue = text.get();
            updateSize();
        }
    }

    @ConfigSetter(field = "lastTextValue")
    public void setText(String text) {
        textSupplier = () -> text;
    }

    public LabelWidget setTextColor(int color) {
        this.color = color;
        if (this.component != null) this.component = this.component.copy().withStyle(this.component.getStyle().withColor(color));
        return this;
    }

    public LabelWidget setDropShadow(boolean dropShadow) {
        this.dropShadow = dropShadow;
        return this;
    }

    @Override
    public void writeInitialData(FriendlyByteBuf buffer) {
        super.writeInitialData(buffer);
        if (!isClientSideWidget) {
            if (this.component != null) {
                buffer.writeBoolean(true);
                buffer.writeComponent(this.component);
            } else {
                buffer.writeBoolean(false);
                this.lastTextValue = textSupplier.get();
                buffer.writeUtf(lastTextValue);
            }
        } else {
            buffer.writeBoolean(false);
            buffer.writeUtf(lastTextValue);
        }
    }

    @Override
    public void readInitialData(FriendlyByteBuf buffer) {
        super.readInitialData(buffer);
        if (buffer.readBoolean()) {
            this.component = buffer.readComponent();
            this.lastTextValue = component.getString();
        } else {
            this.lastTextValue = buffer.readUtf();
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (!isClientSideWidget) {
            if (this.component != null) {
                String latest = component.getString();
                if (!latest.equals(lastTextValue)) {
                    this.lastTextValue = latest;
                    writeUpdateInfo(-2, buffer -> buffer.writeComponent(this.component));
                }
            }
            String latest = textSupplier.get();
            if (!latest.equals(lastTextValue)) {
                this.lastTextValue = latest;
                writeUpdateInfo(-1, buffer -> buffer.writeUtf(this.lastTextValue));
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void readUpdateInfo(int id, FriendlyByteBuf buffer) {
        if (id == -1) {
            this.lastTextValue = buffer.readUtf();
            updateSize();
        } else if(id == -2) {
            this.component = buffer.readComponent();
            this.lastTextValue = component.getString();
            updateSize();
        } else {
            super.readUpdateInfo(id, buffer);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void updateScreen() {
        super.updateScreen();
        if (isClientSideWidget) {
            String latest = component == null ? textSupplier.get() : component.getString();
            if (!latest.equals(lastTextValue)) {
                this.lastTextValue = latest;
                updateSize();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void updateSize() {
        Font fontRenderer = Minecraft.getInstance().font;
        setSize(new Size(this.component == null ? fontRenderer.width(LocalizationUtils.format(lastTextValue)) : fontRenderer.width(this.component), fontRenderer.lineHeight));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
        Font fontRenderer = Minecraft.getInstance().font;
        Position position = getPosition();
        if (component == null) {
            String suppliedText = LocalizationUtils.format(lastTextValue);
            String[] split = suppliedText.split("\n");
            for (int i = 0; i < split.length; i++) {
                int y = position.y + (i * (fontRenderer.lineHeight + 2));
                graphics.drawString(fontRenderer, split[i], position.x, y, color, dropShadow);
            }
        } else {
            graphics.drawString(fontRenderer, component, position.x, position.y, color, dropShadow);
        }
    }

    @Override
    public boolean handleDragging(Object dragging) {
        if (dragging instanceof String string) {
            setText(string);
            return true;
        } else return IConfigurableWidget.super.handleDragging(dragging);
    }

}
