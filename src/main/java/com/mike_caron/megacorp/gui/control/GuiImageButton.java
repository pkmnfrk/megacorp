package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.util.ResourceLocation;

public class GuiImageButton
    extends GuiButton
{
    ResourceLocation texture;
    int texWidth, texHeight, sourceX, sourceY;

    public GuiImageButton(int id, int x, int y, int width, int height, ResourceLocation texture, int texWidth, int texHeight, int sourceX, int sourceY)
    {
        super(id, x, y, width, height, "");
        this.texture = texture;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
        this.sourceX = sourceX;
        this.sourceY = sourceY;
    }

    @Override
    public void draw()
    {
        super.draw();

        GuiUtil.bindTexture(texture);
        float dx = this.width / 2f - texWidth / 2f;
        float dy = this.height / 2f - texHeight / 2f;
        GuiUtil.drawTexturePart(dx, dy, texWidth, texHeight, sourceX, sourceY, 256, 256);
    }
}
