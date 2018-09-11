package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;

import java.awt.*;

public class GuiLabel
    extends GuiSized
{
    protected String stringLabel;

    protected Color color;

    public GuiLabel(int x, int y, String key)
    {
        this(x,y, GuiUtil.FONT_COLOUR, key);
    }

    @Override
    public int getWidth()
    {
        if(width == 0)
        {
            if (this.parent != null)
            {
                width = this.parent.getFontRenderer().getStringWidth(stringLabel);
            }
        }
        return super.getWidth();
    }

    public GuiLabel(int x, int y, Color color, String key)
    {
        super(x, y, 0, 10);

        this.color = color;
        this.stringLabel = key;
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
