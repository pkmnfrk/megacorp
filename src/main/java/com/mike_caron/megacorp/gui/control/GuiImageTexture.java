package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.util.ResourceLocation;

import java.awt.Color;

public class GuiImageTexture
    extends GuiImage
{
    private int width, height;
    private int sourceX, sourceY, sourceWidth, sourceHeight, textureWidth, textureHeight;
    private ResourceLocation texture;

    public GuiImageTexture(int x, int y, int width, int height, int sourceX, int sourceY, ResourceLocation texture)
    {
        this(x, y, width, height, sourceX, sourceY, width, height, texture);
    }

    public GuiImageTexture(int x, int y, int width, int height, int sourceX, int sourceY, int sourceWidth, int sourceHeight, ResourceLocation texture)
    {
        this(x, y, width, height, sourceX, sourceY, sourceWidth, sourceHeight, texture, 256, 256);
    }

    public GuiImageTexture(int x, int y, int width, int height, int sourceX, int sourceY, int sourceWidth, int sourceHeight, ResourceLocation texture, int textureWidth, int textureHeight)
    {
        super(x, y);

        this.width = width;
        this.height = height;
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public void draw()
    {
        GuiUtil.bindTexture(texture);
        GuiUtil.setGLColor(Color.WHITE);
        GuiUtil.drawStretchedTexturePart(0, 0, this.width, this.height, this.sourceX, this.sourceY, this.sourceWidth, this.sourceHeight, this.textureWidth, this.textureHeight);
    }

    public void setSourceX(int sourceX)
    {
        this.sourceX = sourceX;
    }

    public void setSourceY(int sourceY)
    {
        this.sourceY = sourceY;
    }

    public void setSourceWidth(int sourceWidth)
    {
        this.sourceWidth = sourceWidth;
    }

    public void setSourceHeight(int sourceHeight)
    {
        this.sourceHeight = sourceHeight;
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }
}
