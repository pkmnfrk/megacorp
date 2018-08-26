package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import org.lwjgl.opengl.GL11;

public abstract class GuiClippedSized
    extends GuiSized
{
    protected int scrollX, scrollY;

    public GuiClippedSized(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    @Override
    public void preDraw()
    {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        int realX = GuiUtil.getRealX(parent.translateX(this.x));
        int realY = GuiUtil.getRealY(parent.translateY(this.y) + this.height);
        int realWidth = GuiUtil.getRealWidth(this.width);
        int realHeight = GuiUtil.getRealHeight(this.height);

        GL11.glScissor(realX, realY, realWidth, realHeight);

        GL11.glPushMatrix();
        GL11.glTranslatef(x - this.scrollX, y - this.scrollY, 0);
    }

    @Override
    public void postDraw()
    {
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public int getScrollX()
    {
        return scrollX;
    }

    public void setScrollX(int scrollX)
    {
        this.scrollX = scrollX;
    }

    public int getScrollY()
    {
        return scrollY;
    }

    public void setScrollY(int scrollY)
    {
        this.scrollY = scrollY;
    }
}
