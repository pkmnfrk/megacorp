package com.mike_caron.megacorp.storage;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public class TweakedItemStackHandler
    extends ItemStackHandler
{
    public TweakedItemStackHandler(NonNullList<ItemStack> stacks)
    {
        super(stacks);
    }

    public TweakedItemStackHandler(int size)
    {
        super(size);
    }

    public TweakedItemStackHandler()
    {
        super();
    }

    public void notifySlotChanged(int slot)
    {
        this.onContentsChanged(slot);

    }
}
