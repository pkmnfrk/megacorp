package com.mike_caron.megacorp.block;

import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class TileEntityOwnedBase
    extends TileEntityBase
{
    protected UUID owner;

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);

        if(owner != null)
        {
            compound.setString("Owner", owner.toString());
        }

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        if(compound.hasKey("Owner"))
        {
            owner = UUID.fromString(compound.getString("Owner"));
        }
        else
        {
            owner = null;
        }
    }

    public UUID getOwner()
    {
        return owner;
    }

    public void setOwner(UUID owner)
    {
        this.owner = owner;
        this.markDirty();
    }

}
