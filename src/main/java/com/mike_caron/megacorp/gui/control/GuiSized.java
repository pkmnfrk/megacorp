package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;

public abstract class GuiSized extends GuiControl
{
    protected int width;
    protected int height;

    @Override
    public GuiControl hitTest(int x, int y)
    {
        if(GuiUtil.inBounds(x,y, this.x, this.y, this.width, this.height))
        {
            return this;
        }

        return null;
    }

    public GuiSized(int x, int y, int width, int height)
    {
        super(x, y);
        this.height = height;
        this.width = width;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width) { this.width = width; }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height) { this.height = height; }

}
