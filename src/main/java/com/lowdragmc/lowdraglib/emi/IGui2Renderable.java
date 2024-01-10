package com.lowdragmc.lowdraglib.emi;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import dev.emi.emi.api.render.EmiRenderable;

/**
 * @author KilaBash
 * @date 2022/04/30
 * @implNote IGui2Renderable
 */
public interface IGui2Renderable {
    static EmiRenderable toDrawable(IGuiTexture guiTexture, int width, int height) {
        return (graphics, x, y, delta) -> {
            if (guiTexture == null) return;
            guiTexture.draw(graphics, 0, 0, x, y, width, height);
        };
    }
}
