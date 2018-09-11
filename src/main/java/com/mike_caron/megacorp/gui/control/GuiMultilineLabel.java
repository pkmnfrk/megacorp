package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;
import java.util.List;

public class GuiMultilineLabel
    extends GuiSized
{
    private String string;
    private Color color;
    private Alignment alignment;
    private VerticalAlignment valignment;

    private List<String> lines;

    public GuiMultilineLabel(int x, int y, int width, String string)
    {
        this(x, y, width, -1, string);
    }

    public GuiMultilineLabel(int x, int y, int width, int height, String string)
    {
        this(x, y, width, height, GuiUtil.FONT_COLOUR, Alignment.LEFT, VerticalAlignment.CENTER, string);
    }

    public GuiMultilineLabel(int x, int y, int width, Color color, String string)
    {
        this(x, y, width, -1, color, Alignment.LEFT, VerticalAlignment.CENTER, string);
    }

    public GuiMultilineLabel(int x, int y, int width, int height, Color color, String string)
    {
        this(x, y, width, height, color, Alignment.LEFT, VerticalAlignment.CENTER, string);
    }

    public GuiMultilineLabel(int x, int y, int width, int height, Alignment alignment, VerticalAlignment valignment, String string)
    {
        this(x, y, width, height, GuiUtil.FONT_COLOUR, alignment, valignment, string);
    }

    public GuiMultilineLabel(int x, int y, int width, int height, Color color, Alignment alignment, VerticalAlignment valignment, String string)
    {
        super(x, y, width, height);

        this.color = color;
        this.string = string;
        this.alignment = alignment;
        this.valignment = valignment;
    }

    public String getString()
    {
        return string;
    }

    public void setString(String string)
    {
        this.string = string;
        lines = null;
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public Alignment getAlignment()
    {
        return alignment;
    }

    public void setAlignment(Alignment alignment)
    {
        this.alignment = alignment;
    }

    public VerticalAlignment getVerticalAlignment()
    {
        return valignment;
    }

    public void setVeticalAlignment(VerticalAlignment valignment)
    {
        this.valignment = valignment;
    }

    @Override
    public int getHeight()
    {
        if(this.height == -1)
        {
            updateCache();
        }

        return super.getHeight();
    }

    @Override
    public void draw()
    {
        if(!visible)
            return;

        if(lines == null)
            updateCache();

        if(lines == null)
            return;

        //GuiUtil.drawDebugFlatRectangle(x, y, width, height, 255, 0, 0, 128);

        FontRenderer renderer = parent.getFontRenderer();

        int dh = renderer.FONT_HEIGHT * lines.size();
        int dy = -1;
        switch(valignment)
        {
            case TOP:
                dy = 0;
                break;
            case CENTER:
                dy = this.height / 2 - dh / 2;
                break;
            case BOTTOM:
                dy = this.height - dh;
                break;
        }

        for(int i = 0; i < lines.size(); i++)
        {
            int w = renderer.getStringWidth(lines.get(i));
            int dx = -1;

            switch (alignment)
            {
                case LEFT:
                    dx = 0;
                    break;
                case CENTER:
                    dx = this.width / 2 - w / 2;
                    break;
                case RIGHT:
                    dx = this.width - w;
                    break;
            }

            renderer.drawString(lines.get(i), dx, dy + i * renderer.FONT_HEIGHT, color.getRGB());
        }
    }

    private void updateCache()
    {
        if(this.parent == null) return;

        lines = this.parent.getFontRenderer().listFormattedStringToWidth(string, this.width);

        if(this.height == -1)
        {
            this.height = lines.size() * 10;
        }
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
        CENTER,
        BOTTOM
    }
}
