package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.util.ResourceLocation;

public class GuiImageToggleButton
    extends GuiToggleButton
{
    ResourceLocation texture;
    int texWidth, texHeight, sourceX, sourceY;
    int id;

    public GuiImageToggleButton(int id, int x, int y, int width, int height, ResourceLocation texture, int texWidth, int texHeight, int sourceX, int sourceY)
    {
        super(id, x, y, width, height);

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
        GuiUtil.drawTexturePart(this.x + dx, this.y + dy, texWidth, texHeight, sourceX, sourceY, 256, 256);
    }
}
