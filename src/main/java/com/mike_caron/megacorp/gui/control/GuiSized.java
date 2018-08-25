package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class GuiSized extends GuiControl
{
    protected int width;
    protected int height;
    protected List<String> tooltipText = null;

    @Override
    public GuiControl hitTest(int x, int y)
    {
        if(GuiUtil.inBounds(x,y, this.getX(), this.getY(), this.getWidth(), this.getHeight()))
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

    public void setTooltip(String text)
    {
        this.tooltipText = new ArrayList<>();
        this.tooltipText.add(text);
    }

    @Nullable
    @Override
    public List<String> getTooltip(int mouseX, int mouseY)
    {
        return this.tooltipText;
    }

    public void setTooltip(List<String> text)
    {
        this.tooltipText = new ArrayList<>();
        this.tooltipText.addAll(text);
    }

}
