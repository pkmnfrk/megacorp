package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;

import java.awt.*;

public class GuiList
    extends GuiClippedSized
{

    public GuiList(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    @Override
    public void draw()
    {
        // TODO: implement
        GuiUtil.setGLColor(Color.WHITE);
        GuiUtil.bindTexture(GuiUtil.MISC_RESOURCES);
        GuiUtil.draw3x3Stretched(0, 0, this.width, this.height, 16, 16);

    }
}
