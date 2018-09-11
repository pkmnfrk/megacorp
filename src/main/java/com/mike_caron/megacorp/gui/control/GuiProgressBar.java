package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;

import java.awt.*;

public class GuiProgressBar
    extends GuiSized
{
    private Color foreColor = new Color(0, 196, 0, 255);
    private Color backColor = new Color(128, 0, 0, 255);
    private float progress = 0f;

    public GuiProgressBar(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    public void setForeColor(Color foreColor)
    {
        this.foreColor = foreColor;
    }

    public void setBackColor(Color backColor)
    {
        this.backColor = backColor;
    }

    public void setProgress(float progress)
    {
        this.progress = progress;
    }

    @Override
    public void draw()
    {
        if(!visible) return;

        GuiUtil.bindTexture(GuiUtil.MISC_RESOURCES);
        GuiUtil.setGLColor(backColor);
        //GuiUtil.draw3x3Stretched(x, y, width, height, 32, 16);
        GuiUtil.drawStretchedTexturePart(x, y, width, height, 36, 16, 1, 16, 256, 256);
        //GuiUtil.drawDebugFlatRectangle(x, y, width, height);

        GuiUtil.setGLColor(foreColor);

        //GL11.glScissor(parent.translateToScreenX(x), y, (int)(width * progress), height);

        //GL11.glEnable(GL11.GL_SCISSOR_TEST);

        //GuiUtil.drawDebugFlatRectangle(-parent.translateToScreenX(0), -parent.translateToScreenY(0), 1000, 1000 );
        //GuiUtil.draw3x3Stretched(x, y, (int)(width * progress + 4), height, 32, 16);
        GuiUtil.drawStretchedTexturePart(x, y, (int)(width * progress), height, 36, 16, 1, 16, 256, 256);
        //GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
