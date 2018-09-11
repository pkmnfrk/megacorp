package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import org.lwjgl.opengl.GL11;

public abstract class GuiClippedSized
    extends GuiSized
{
    protected int scrollX, scrollY;

    private boolean clippingEnabled = false;

    public GuiClippedSized(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    protected void start()
    {
        clippingEnabled = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        assertClippingPlane();

        GL11.glPushMatrix();
        GL11.glTranslatef(-this.scrollX, - this.scrollY, 0);
    }

    protected void assertClippingPlane()
    {
        int lm = getLeftMargin();
        int rm = getRightMargin();

        setClippingPlane(
            parent.translateToScreenX(this.x) + lm,
            parent.translateToScreenY(this.y) + this.height,
            this.width - lm - rm,
            this.height
        );
    }

    protected void finish()
    {
        GL11.glPopMatrix();
        if(!clippingEnabled)
        {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    protected void setClippingPlane(int x, int y, int width, int height)
    {
        int realX = GuiUtil.getRealX(x);
        int realY = GuiUtil.getRealY(y);
        int realWidth = GuiUtil.getRealWidth(width);
        int realHeight = GuiUtil.getRealHeight(height);

        GL11.glScissor(realX, realY, realWidth, realHeight);
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

    protected int getLeftMargin()
    {
        return 0;
    }

    protected int getRightMargin()
    {
        return 0;
    }
}
