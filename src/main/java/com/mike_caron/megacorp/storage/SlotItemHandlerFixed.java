package com.mike_caron.megacorp.storage;

import net.minecraftforge.items.SlotItemHandler;

public class SlotItemHandlerFixed
    extends SlotItemHandler
{
    private TweakedItemStackHandler itemHandler;

    public SlotItemHandlerFixed(TweakedItemStackHandler itemHandler, int index, int xPosition, int yPosition)
    {
        super(itemHandler, index, xPosition, yPosition);
        this.itemHandler = itemHandler;
    }

    @Override
    public void onSlotChanged()
    {
        super.onSlotChanged();

        itemHandler.notifySlotChanged(this.slotNumber);
    }
}
