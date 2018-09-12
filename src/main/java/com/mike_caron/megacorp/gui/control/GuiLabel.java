package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;

import java.awt.*;

public class GuiLabel
    extends GuiControl
{
    protected String stringLabel;

    protected Color color;

    public GuiLabel(int x, int y, String key)
    {
        this(x,y, GuiUtil.FONT_COLOUR, key);
    }

    public GuiLabel(int x, int y, Color color, String key)
    {
        super(x, y);

        this.color = color;
        this.stringLabel = key;
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

        this.parent.getFontRenderer().drawString(stringLabel, 0, 0, this.color.getRGB());
    }

}
