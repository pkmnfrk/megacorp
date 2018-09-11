package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.util.ResourceLocation;

public class GuiProgressGlyph
    extends GuiSized
{
    private int sourceX, sourceY;
    private float progress = 0f;
    private ResourceLocation texture;
    private Orientation orientation;

    public GuiProgressGlyph(int x, int y, int width, int height, int sourceX, int sourceY, ResourceLocation texture)
    {
        this(x, y, width, height, sourceX, sourceY, texture, Orientation.HORIZONTAL);
    }

    public GuiProgressGlyph(int x, int y, int width, int height, int sourceX, int sourceY, ResourceLocation texture, Orientation orientation)
    {
        super(x, y, width, height);

        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.texture = texture;
        this.orientation = orientation;
    }

    public void setProgress(float progress)
    {
        this.progress = progress;
    }

    @Override
    public void draw()
    {
        GuiUtil.bindTexture(texture);
        if(orientation == Orientation.HORIZONTAL)
        {
            drawTexturedModalRect(0, 0, sourceX, sourceY, (int) (width * progress), height);
        }
        else if(orientation == Orientation.VERTICAL)
        {
            drawTexturedModalRect(0, height - (height * progress), sourceX, sourceY, width, (int)(height * progress));
        }
    }

    public enum Orientation
    {
        HORIZONTAL,
        VERTICAL
    }
}
