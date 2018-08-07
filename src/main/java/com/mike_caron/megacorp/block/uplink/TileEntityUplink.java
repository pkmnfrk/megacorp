package com.mike_caron.megacorp.block.uplink;

import com.mike_caron.megacorp.block.TileEntityBase;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityUplink
        extends TileEntityBase
{

    public TileEntityUplink()
    {

    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound ret = super.writeToNBT(compound);

        return ret;
    }
}
