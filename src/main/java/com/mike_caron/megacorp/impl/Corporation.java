package com.mike_caron.megacorp.impl;

import com.google.common.base.Preconditions;
import com.mike_caron.megacorp.api.ICorporation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Corporation
        implements ICorporation, INBTSerializable<NBTTagCompound>
{
    private final Lock lock = new ReentrantLock();
    private UUID owner;
    private String name;
    private long availableProfit;
    private long totalProfit;

    private final CorporationManager manager;

    public Corporation(CorporationManager manager)
    {
        this.manager = manager;
    }

    public Corporation(CorporationManager manager, UUID owner)
    {
        this.manager = manager;
        this.owner = owner;
    }

    @Override
    public long getTotalProfit()
    {
        return totalProfit;
    }

    @Override
    public UUID getOwner()
    {
        return owner;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
        this.manager.markDirty();
    }

    @Override
    public long getAvailableProfit()
    {
        return availableProfit;
    }

    public int consumeProfit(int amount)
    {
        Preconditions.checkArgument(amount >= 0);
        lock.lock();

        try
        {
            if (amount > availableProfit)
            {
                amount = (int) availableProfit;
            }

            availableProfit -= amount;

            this.manager.markDirty();

            return amount;
        }
        finally
        {
            lock.unlock();
        }

    }

    public void addProfit(int amount)
    {
        Preconditions.checkArgument(amount >= 0);

        lock.lock();

        try
        {
            availableProfit = Math.addExact(availableProfit, amount);
            totalProfit = Math.addExact(totalProfit, amount);

            this.manager.markDirty();
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setString("Owner", owner.toString());
        tag.setString("Name", name);
        tag.setLong("AvailableProfit", availableProfit);
        tag.setLong("TotalProfit", totalProfit);

        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag)
    {
        owner = UUID.fromString(tag.getString("Owner"));
        name = tag.getString("Name");
        availableProfit = tag.getLong("AvailableProfit");
        totalProfit = tag.getLong("TotalProfit");
    }
}
