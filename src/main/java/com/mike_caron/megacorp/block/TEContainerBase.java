package com.mike_caron.megacorp.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class TEContainerBase
    extends ContainerBase
{
    @Nonnull
    protected final TileEntityBase te;

    public UUID owner;

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        if(te != null)
        {
            if(owner != te.getOwner())
            {
                owner = te.getOwner();
                changed = true;
            }
        }
    }

    @Override
    protected void onWriteNBT(NBTTagCompound tag)
    {
        super.onWriteNBT(tag);

        if(owner != null)
        {
            tag.setString("Owner", owner.toString());
        }
    }

    @Override
    protected void onReadNBT(NBTTagCompound tag)
    {
        super.onReadNBT(tag);

        if(tag.hasKey("Owner"))
        {
            owner = UUID.fromString( tag.getString("Owner"));
        }
        else
        {
            owner = null;
        }
    }

    public TEContainerBase(IInventory playerInventory, TileEntityBase te)
    {
        super(playerInventory);

        this.te = te;

        init();
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
