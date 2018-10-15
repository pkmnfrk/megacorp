package com.mike_caron.megacorp.reward;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerRewardsProvider
    implements ICapabilitySerializable<NBTBase>
{
    @CapabilityInject(IPlayerRewards.class)
    public static Capability<IPlayerRewards> REWARDS;

    private IPlayerRewards instance = REWARDS.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing enumFacing)
    {
        return capability == REWARDS;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing enumFacing)
    {
        if(capability == REWARDS)
        {
            return REWARDS.cast(instance);
        }
        return null;
    }

    @Override
    public NBTBase serializeNBT()
    {
        return REWARDS.getStorage().writeNBT(REWARDS, instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase iPlayerRewards)
    {
        REWARDS.getStorage().readNBT(REWARDS, instance, null, iPlayerRewards);
    }
}
