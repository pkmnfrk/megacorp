package com.mike_caron.megacorp.storage;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public abstract class LimitedItemStackHandler
    extends ItemStackHandler
{
    TileEntity parent;

    public LimitedItemStackHandler(TileEntity te, int size)
    {
        super(size);
        this.parent = te;
    }

    public abstract boolean isItemValid(int slot, @Nonnull ItemStack item);

    @Override
    protected void onContentsChanged(int slot)
    {
        super.onContentsChanged(slot);

        parent.markDirty();
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if(!parent.getWorld().isRemote)
        {
            if(!isItemValid(slot, stack))
                return stack;
        }

        return super.insertItem(slot, stack, simulate);
    }
}
