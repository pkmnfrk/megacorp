package com.mike_caron.megacorp.reward;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class PlayerRewardCapabilityStorage
    implements Capability.IStorage<IPlayerRewards>
{
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IPlayerRewards> capability, IPlayerRewards iPlayerRewards, EnumFacing enumFacing)
    {
        NBTTagCompound ret = new NBTTagCompound();

        ret.setInteger("hungerRestore", iPlayerRewards.getHungerRestore());
        ret.setInteger("damageTimer", iPlayerRewards.getDamageTimer());

        return ret;
    }

    @Override
    public void readNBT(Capability<IPlayerRewards> capability, IPlayerRewards iPlayerRewards, EnumFacing enumFacing, NBTBase nbtBase)
    {
        NBTTagCompound tag = (NBTTagCompound)nbtBase;

        if(tag.hasKey("hungerRestore"))
        {
            iPlayerRewards.setHungerRestore(tag.getInteger("hungerRestore"));
        }
        else
        {
            iPlayerRewards.setHungerRestore(0);
        }

        if(tag.hasKey("damageTimer"))
        {
            iPlayerRewards.setDamageTimer(tag.getInteger("damageTimer"));
        }
        else
        {
            iPlayerRewards.setDamageTimer(0);
        }
    }
}
