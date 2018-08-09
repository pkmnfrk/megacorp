package com.mike_caron.megacorp.gui;

public class GuiMultilineLabel
    extends GuiSized
{
    private String key;

    public GuiMultilineLabel(int x, int y, int height, int width, String key, Object[] ... placeholders)
    {
        super(x, y, height, width);

    }

    @Override
    public void draw()
    {

    }

    public enum Alignment
    {
        LEFT,
        CENTER,
        RIGHT
    }
}
