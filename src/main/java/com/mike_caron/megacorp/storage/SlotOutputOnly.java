package com.mike_caron.megacorp.storage;

import net.minecraft.item.ItemStack;

public class SlotOutputOnly
    extends SlotItemHandlerFixed
{
    public SlotOutputOnly(TweakedItemStackHandler inventoryIn, int index, int xPosition, int yPosition)
    {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return false;
    }
}
