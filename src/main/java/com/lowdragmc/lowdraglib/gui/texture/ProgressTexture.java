package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.data.resource.Resource;
import lombok.Getter;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

/**
 * @author youyihj
 */
@LDLRegister(name = "progress_texture", group = "texture")
public class ProgressTexture extends TransformTexture {
    @Configurable
    protected FillDirection fillDirection = FillDirection.LEFT_TO_RIGHT;
    @Configurable
    @Getter
    protected IGuiTexture emptyBarArea;
    @Configurable
    @Getter
    protected IGuiTexture filledBarArea;

    protected double progress;

    private boolean demo;

    public ProgressTexture() {
        this(new ResourceTexture("ldlib:textures/gui/progress_bar_fuel.png").getSubTexture(0, 0, 1, 0.5),
                new ResourceTexture("ldlib:textures/gui/progress_bar_fuel.png").getSubTexture(0, 0.5, 1, 0.5));
        fillDirection = FillDirection.DOWN_TO_UP;
        demo = true;
    }

    @Override
    public void updateTick() {
        if (emptyBarArea != null) {
            emptyBarArea.updateTick();
        }
        if (filledBarArea != null) {
            filledBarArea.updateTick();
        }
        if (demo) {
            progress = Math.abs(System.currentTimeMillis() % 2000) / 2000.0;
        }
    }

    public ProgressTexture(IGuiTexture emptyBarArea, IGuiTexture filledBarArea) {
        this.emptyBarArea = emptyBarArea;
        this.filledBarArea = filledBarArea;
    }

    public void setProgress(double progress) {
        this.progress = Mth.clamp(0.0, progress, 1.0);
    }

    public ProgressTexture setFillDirection(FillDirection fillDirection) {
        this.fillDirection = fillDirection;
        return this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void drawInternal(GuiGraphics graphics, int mouseX, int mouseY, float x, float y, int width, int height) {
        if (emptyBarArea != null) {
            emptyBarArea.draw(graphics, mouseX, mouseY, x, y, width, height);
        }
        if (filledBarArea != null) {
            float drawnU = (float) fillDirection.getDrawnU(progress);
            float drawnV = (float) fillDirection.getDrawnV(progress);
            float drawnWidth = (float) fillDirection.getDrawnWidth(progress);
            float drawnHeight = (float) fillDirection.getDrawnHeight(progress);
            float X = x + drawnU * width;
            float Y = y + drawnV * height;
            float W = width * drawnWidth;
            float H = height * drawnHeight;

            filledBarArea.drawSubArea(graphics, X, Y, W, H, drawnU, drawnV,
                    ((drawnWidth * width)) / (width),
                    ((drawnHeight * height)) / (height));
        }
    }

    @Override
    public void setUIResource(Resource<IGuiTexture> texturesResource) {
        if (emptyBarArea != null) {
            emptyBarArea.setUIResource(texturesResource);
        }
        if (filledBarArea != null) {
            filledBarArea.setUIResource(texturesResource);
        }
    }

    public static class Auto extends ProgressTexture {

        public Auto(IGuiTexture emptyBarArea, IGuiTexture filledBarArea) {
            super(emptyBarArea, filledBarArea);
        }

        @Override
        public void updateTick() {
            progress = Math.abs(System.currentTimeMillis() % 2000) / 2000.0;
        }
    }

    public enum FillDirection {
        LEFT_TO_RIGHT {
            @Override
            public double getDrawnHeight(double progress) {
                return 1.0;
            }
        },
        RIGHT_TO_LEFT {
            @Override
            public double getDrawnU(double progress) {
                return 1.0 - progress;
            }

            @Override
            public double getDrawnHeight(double progress) {
                return 1.0;
            }
        },
        UP_TO_DOWN {
            @Override
            public double getDrawnWidth(double progress) {
                return 1.0;
            }
        },
        DOWN_TO_UP {
            @Override
            public double getDrawnV(double progress) {
                return 1.0 - progress;
            }

            @Override
            public double getDrawnWidth(double progress) {
                return 1.0;
            }
        },

        ALWAYS_FULL {
            @Override
            public double getDrawnHeight(double progress) {
                return 1.0;
            }

            @Override
            public double getDrawnWidth(double progress) {
                return 1.0;
            }
        };

        public double getDrawnU(double progress) {
            return 0.0;
        }

        public double getDrawnV(double progress) {
            return 0.0;
        }

        public double getDrawnWidth(double progress) {
            return progress;
        }

        public double getDrawnHeight(double progress) {
            return progress;
        }
    }
}
