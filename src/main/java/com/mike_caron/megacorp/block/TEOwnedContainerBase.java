package com.mike_caron.megacorp.block;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public abstract class TEOwnedContainerBase
    extends TEContainerBase
{

    public UUID owner;

    public TEOwnedContainerBase(IInventory playerInventory, TileEntityOwnedBase te)
    {
        super(playerInventory, te);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        TileEntityOwnedBase te = getTE();

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

    private TileEntityOwnedBase getTE()
    {
        return (TileEntityOwnedBase)te;
    }
}
