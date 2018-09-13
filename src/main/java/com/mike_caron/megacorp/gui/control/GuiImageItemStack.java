package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;

public class GuiImageItemStack
    extends GuiImage
{
    ItemStack itemStack;
    RenderItem itemRender;

    public GuiImageItemStack(int x, int y, ItemStack itemStack)
    {
        super(x, y);
        this.itemStack = itemStack;
        this.itemRender = Minecraft.getMinecraft().getRenderItem();

        setTooltip(itemStack.getTooltip(Minecraft.getMinecraft().player, ITooltipFlag.TooltipFlags.NORMAL));
    }

    @Override
    public int getWidth()
    {
        return 16;
    }

    @Override
    public int getHeight()
    {
        return 16;
    }

    @Override
    public void draw()
    {
        GuiUtil.drawItemStack(itemStack, 0, 0, itemRender, parent != null ? parent.getFontRenderer() : null);
    }
}
