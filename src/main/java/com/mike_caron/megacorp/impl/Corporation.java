package com.mike_caron.megacorp.impl;

import com.mike_caron.megacorp.api.ICorporation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public class Corporation
        implements ICorporation, INBTSerializable<NBTTagCompound>
{
    private UUID owner;
    private String name;
    private long availableProfit;

    @Override
    public UUID getOwner()
    {
        return null;
    }

    @Override
    public String getName()
    {
        return null;
    }

    @Override
    public long getAvailableProfit()
    {
        return 0;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setString("Owner", owner.toString());
        tag.setString("Name", name);
        tag.setLong("AvailableProfit", availableProfit);

        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag)
    {
        owner = UUID.fromString(tag.getString("Owner"));
        name = tag.getString("Name");
        availableProfit = tag.getLong("AvailableProfit");
    }
}
