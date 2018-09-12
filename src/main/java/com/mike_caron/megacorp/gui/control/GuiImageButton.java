package com.mike_caron.megacorp.gui.control;

import net.minecraft.client.renderer.GlStateManager;

import java.awt.Color;

public class GuiImageButton
    extends GuiButton
{
    GuiImage image;

    public GuiImageButton(int id, int x, int y, int width, int height, GuiImage image)
    {
        super(id, x, y, width, height, null);
        this.image = image;
    }

    @Override
    public void draw()
    {
        drawBackground();

        int totalWidth = image.width;

        Color fore = Color.WHITE;

        if(!enabled)
        {
            fore = Color.LIGHT_GRAY;
        }

        if(getLabel() != null)
        {
            totalWidth += 4 + this.parent.getFontRenderer().getStringWidth(getLabel());
        }

        float dx = this.width / 2f - totalWidth / 2f;
        float dy = this.height / 2f - totalWidth / 2f;

        /*GuiUtil.bindTexture(texture);
        GuiUtil.drawTexturePart(dx, dy, texWidth, texHeight, sourceX, sourceY, 256, 256);*/
        GlStateManager.pushMatrix();
        GlStateManager.translate(dx, dy, 0);
        image.draw();
        GlStateManager.popMatrix();

        if(getLabel() != null)
        {
            this.parent.getFontRenderer().drawString(getLabel(), (int)(dx + image.width + 4 + 1), this.height / 2 - 5 + 1, Color.black.getRGB());
            this.parent.getFontRenderer().drawString(getLabel(), (int)(dx + image.width + 4), this.height / 2 - 5, fore.getRGB());
        }
    }
}
