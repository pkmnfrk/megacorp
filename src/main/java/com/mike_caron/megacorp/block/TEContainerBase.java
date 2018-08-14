package com.mike_caron.megacorp.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public abstract class TEContainerBase extends ContainerBase
{
    @Nonnull
    protected final TileEntityBase te;

    public TEContainerBase(IInventory player, TileEntityBase te)
    {
        super(player);

        this.te = te;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer)
    {
        return te.canInteractWith(entityPlayer);
    }

    public BlockPos getPos()
    {
        return te.getPos();
    }
}
