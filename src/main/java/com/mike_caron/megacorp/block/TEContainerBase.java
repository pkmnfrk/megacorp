package com.mike_caron.megacorp.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

import javax.annotation.Nonnull;

public abstract class TEContainerBase
    extends ContainerBase
{
    @Nonnull
    protected final TileEntityBase te;

    public TEContainerBase(IInventory playerInventory, TileEntityBase te)
    {
        super(playerInventory);

        this.te = te;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer)
    {
        return te.canInteractWith(entityPlayer);
    }
}
