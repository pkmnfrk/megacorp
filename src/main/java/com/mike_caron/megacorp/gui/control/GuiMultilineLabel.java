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

    private boolean autoHeight = false;

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

        if(height == -1)
        {
            autoHeight = true;
        }
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
    public void setHeight(int height)
    {
        super.setHeight(height);

        if(height == -1 && !autoHeight)
        {
            autoHeight = true;
            lines = null;
        }
        else if(height != -1 && autoHeight)
        {
            autoHeight = false;
            lines = null;
        }
    }

    @Override
    public void setWidth(int width)
    {
        super.setWidth(width);

        lines = null;
    }

    @Override
    public int getHeight()
    {
        if(autoHeight)
        {
            if (lines == null)
            {
                updateCache();
            }

            if(lines == null)
            {
                return 10;
            }

            FontRenderer renderer = parent.getFontRenderer();

            return lines.size() * renderer.FONT_HEIGHT;
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

        //GuiUtil.setGLColor(Color.RED);
        //GuiUtil.drawDebugFlatRectangle(0, 0, getWidth(), getHeight());

        FontRenderer renderer = parent.getFontRenderer();

        int dh = lines.size() * renderer.FONT_HEIGHT;
        int dy = 0;
        if(!autoHeight)
        {
            switch (valignment)
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
        }

        GuiUtil.setGLColor(Color.WHITE);

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
        if(this.parent == null || this.parent.getFontRenderer() == null) return;

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
