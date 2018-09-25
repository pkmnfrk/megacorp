package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;

import java.awt.*;

public class GuiLabel
    extends GuiControl
{
    protected String stringLabel;

    protected Color color;

    protected Alignment alignment;
    protected VerticalAlignment verticalAlignment;

    public GuiLabel(int x, int y, String key)
    {
        this(x,y, GuiUtil.FONT_COLOUR, key, Alignment.LEFT, VerticalAlignment.BOTTOM);
    }

    public GuiLabel(int x, int y, Color color, String key)
    {
        this(x, y, color, key, Alignment.LEFT, VerticalAlignment.BOTTOM);
    }

    public GuiLabel(int x, int y, Color color, String key, Alignment alignment)
    {
        this(x, y, color, key, alignment, VerticalAlignment.BOTTOM);
    }

    public GuiLabel(int x, int y, Color color, String key, Alignment alignment, VerticalAlignment verticalAlignment)
    {
        super(x, y);

        this.color = color;
        this.stringLabel = key;
        this.alignment = alignment;
        this.verticalAlignment = verticalAlignment;
    }

    @Override
    public int getWidth()
    {
        return this.parent.getFontRenderer().getStringWidth(stringLabel);
    }

    @Override
    public int getHeight()
    {
        return 10;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    @Override
    public void draw()
    {
        if(!this.visible) return;

        int dx = 0;
        int dy = 0;
        int sw = this.parent.getFontRenderer().getStringWidth(stringLabel);
        int sh = this.parent.getFontRenderer().FONT_HEIGHT;

        if(alignment == Alignment.CENTER)
        {
            dx = - (sw / 2);
        }
        else if(alignment == Alignment.RIGHT)
        {
            dx = -sw;
        }

        if(verticalAlignment == VerticalAlignment.TOP)
        {
            dy = -sh;
        }
        else if(verticalAlignment == VerticalAlignment.MIDDLE)
        {
            dy = -(sh / 2);
        }


        this.parent.getFontRenderer().drawString(stringLabel, dx, dy, this.color.getRGB());
    }

    public enum Alignment
    {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum VerticalAlignment
    {
        TOP,
        MIDDLE,
        BOTTOM
    }
}
