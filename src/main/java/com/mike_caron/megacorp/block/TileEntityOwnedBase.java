package com.mike_caron.megacorp.block;

import com.mike_caron.megacorp.impl.Corporation;
import com.mike_caron.megacorp.impl.CorporationManager;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
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

    @Nullable
    protected Corporation getCorporation()
    {
        if(owner == null) return null;

        if(!CorporationManager.get(world).ownerHasCorporation(owner))
        {
            owner = null;
            return null;
        }

        return (Corporation)CorporationManager.get(world).getCorporationForOwner(owner);
    }

}
