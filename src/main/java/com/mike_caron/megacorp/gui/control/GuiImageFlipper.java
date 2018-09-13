package com.mike_caron.megacorp.gui.control;

import net.minecraft.client.renderer.GlStateManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GuiImageFlipper
    extends GuiImage
{
    private final List<GuiImage> subImages = new ArrayList<>();
    private int timer;
    private int currentImage;

    public GuiImageFlipper(int x, int y)
    {
        super(x, y);
    }

    @Override
    public void update()
    {
        timer += 1;
        if(timer >= 20)
        {
            timer = 0;
            currentImage += 1;
        }
    }

    @Override
    public int getWidth()
    {
        return subImages.stream().map(GuiControl::getWidth).max(Integer::compareTo).orElse(0);
    }

    @Override
    public int getHeight()
    {
        return subImages.stream().map(GuiControl::getHeight).max(Integer::compareTo).orElse(0);
    }

    public void addImage(GuiImage image)
    {
        this.subImages.add(image);
    }

    public void clearImages()
    {
        this.subImages.clear();
    }

    @Nullable
    @Override
    public List<String> getTooltip(int mouseX, int mouseY)
    {
        if(subImages.size() == 0) return null;

        if(currentImage >= subImages.size())
        {
            currentImage = 0;
        }

        return subImages.get(currentImage).getTooltip(mouseX, mouseY);
    }

    @Override
    public void draw()
    {
        if(subImages.size() == 0) return;

        if(currentImage >= subImages.size())
        {
            currentImage = 0;
        }

        GuiImage realImage = subImages.get(currentImage);

        int w = this.getWidth();
        int h = this.getHeight();

        int x = w / 2 - realImage.getWidth() / 2;
        int y = h / 2 - realImage.getHeight() / 2;

        GlStateManager.translate(x, y, 0);
        realImage.draw();
    }
}
