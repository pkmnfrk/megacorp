package com.mike_caron.megacorp.impl;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.api.ICorporation;
import com.mike_caron.megacorp.api.ICorporationManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CorporationManager extends WorldSavedData implements ICorporationManager
{
    private final Map<UUID, Corporation> corporations = new HashMap<>();

    public CorporationManager()
    {
        super(MegaCorpMod.modId);

    }

    public CorporationManager(String id)
    {
        super(id);
    }

    public boolean ownerHasCorporation(UUID owner)
    {
        return corporations.containsKey(owner);
    }

    public ICorporation createCorporation(UUID owner)
    {
        if(corporations.containsKey(owner))
            throw new IllegalArgumentException("owner");

        Corporation corp = new Corporation(this, owner);

        corp.setName(CorporationNameGenerator.generateName());

        corporations.put(owner, corp);

        markDirty();

        return corp;
    }

    public ICorporation getCorporationForOwner(UUID owner)
    {
        if(!corporations.containsKey(owner))
            throw new IllegalArgumentException("owner");

        return corporations.get(owner);
    }

    public Corporation getCorporationForOwnerInternal(UUID owner)
    {
        if(!corporations.containsKey(owner))
            throw new IllegalArgumentException("owner");

        return corporations.get(owner);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        this.corporations.clear();

        for(String key : tag.getKeySet())
        {
            UUID owner = UUID.fromString(key);
            Corporation corp = new Corporation(this);
            corp.deserializeNBT(tag.getCompoundTag(key));

            corporations.put(owner, corp);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound ret)
    {
        for(UUID owner : corporations.keySet())
        {
            ICorporation corporation = corporations.get(owner);
            NBTTagCompound corp = ((Corporation) corporation).serializeNBT();

            ret.setTag(owner.toString(), corp);
        }

        return ret;
    }

    public static CorporationManager get(World world)
    {
        CorporationManager ret = (CorporationManager)world.getMapStorage().getOrLoadData(CorporationManager.class, MegaCorpMod.modId);
        if(ret == null)
        {
            ret = new CorporationManager();
            world.getMapStorage().setData(MegaCorpMod.modId, ret);
        }

        return ret;
    }
}
