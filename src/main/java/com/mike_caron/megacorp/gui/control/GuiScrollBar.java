package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;

import java.awt.Color;

public class GuiScrollBar
    extends GuiSized
{
    public GuiScrollBar(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    @Override
    public void draw()
    {
        GuiUtil.setGLColor(Color.WHITE);
        GuiUtil.bindTexture(GuiUtil.MISC_RESOURCES);
        GuiUtil.draw3x3(0, 0, this.width, this.height, 72, 0, 2, 4);
        GuiUtil.drawStretchedTexturePart(0, 0, this.width, this.width, 64, 0, 8, 8, 256, 256);
        GuiUtil.drawStretchedTexturePart(0, this.height - this.width, this.width, this.width, 64, 8, 8, 8, 256, 256);
    }
}
