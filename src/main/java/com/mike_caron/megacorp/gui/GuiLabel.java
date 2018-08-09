package com.mike_caron.megacorp.gui;

public class GuiLabel
    extends GuiControl
{
    protected String stringLabel;

    protected int color;

    public GuiLabel(int x, int y, String key)
    {
        this(x,y, GuiUtil.FONT_COLOUR, key);
    }
    public GuiLabel(int x, int y, int color, String key)
    {
        super(x, y);

        this.color = color;
        this.stringLabel = key;
    }


    @Override
    public void draw()
    {
        if(!this.visible) return;

        this.parent.getFontRenderer().drawString(stringLabel, this.x, this.y, this.color);
    }
}
