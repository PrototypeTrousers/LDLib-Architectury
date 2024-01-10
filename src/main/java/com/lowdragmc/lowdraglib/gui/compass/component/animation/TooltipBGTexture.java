package com.lowdragmc.lowdraglib.gui.compass.component.animation;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.TransformTexture;
import net.minecraft.client.gui.GuiGraphics;

/**
 * @author KilaBash
 * @date 2023/7/30
 * @implNote TooltipBGTexture
 */
public class TooltipBGTexture extends TransformTexture {
    public static TooltipBGTexture INSTANCE = new TooltipBGTexture();

    @Override
    protected void drawInternal(GuiGraphics graphics, int mouseX, int mouseY, float x, float y, int width, int height) {
        ColorPattern.BLACK.rectTexture().draw(graphics, mouseX, mouseY, x - 1, y - 1, width + 2, height + 2);
        ColorPattern.WHITE.borderTexture(1).draw(graphics, mouseX, mouseY, x - 1, y - 1, width + 2, height + 2);
    }
}
