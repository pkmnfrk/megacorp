package com.mike_caron.megacorp.block.profit_materializer;

import com.mike_caron.megacorp.api.ICorporation;
import com.mike_caron.megacorp.api.IReward;
import com.mike_caron.megacorp.block.TileEntityOwnedBase;
import com.mike_caron.megacorp.fluid.ModFluids;
import com.mike_caron.megacorp.impl.RewardManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

public class TileEntityProfitMaterializer
    extends TileEntityOwnedBase
        implements ITickable
{
    public final FluidTank fluidTank = new FluidTank(10000)
    {
        @Override
        protected void onContentsChanged()
        {
            super.onContentsChanged();
            TileEntityProfitMaterializer.this.markDirty();
        }
    };

    private int timer = 0;
    private float moneyPerTick = 0f;

    private float accumulation = 0f;

    public TileEntityProfitMaterializer()
    {
        fluidTank.setCanDrain(true);
        fluidTank.setCanFill(false);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        if(compound.hasKey("tank"))
        {
            fluidTank.readFromNBT(compound.getCompoundTag("tank"));
        }

        if(compound.hasKey("timer"))
        {
            timer = compound.getInteger("timer");
        }

        if(compound.hasKey("accumulation"))
        {
            accumulation = compound.getFloat("accumulation");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound ret = super.writeToNBT(compound);

        ret.setTag("tank", fluidTank.writeToNBT(new NBTTagCompound()));
        ret.setInteger("timer", timer);
        ret.setFloat("accumulation", accumulation);

        return ret;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != null)
        {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != null)
        {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidTank);
        }
        return super.getCapability(capability, facing);
    }

    private float calculateSpeed()
    {
        ICorporation corp = getCorporation();

        if(corp != null)
        {
            int rewardRank = corp.getRankInReward("faster_generation");

            IReward reward = RewardManager.INSTANCE.getRewardWithId("faster_generation");

            if (reward != null)
            {
                float[] vals = reward.getValuesForRank(rewardRank);

                return vals[0];
            }
        }

        return 0.25f;
    }

    @Override
    public void update()
    {
        if(world == null || world.isRemote) return;

        ICorporation corp = getCorporation();

        if(corp == null) return;

        moneyPerTick = calculateSpeed();

        //timer += 1;

        //basically, in the case of fractions, we want to hang on to the fraction
        //so that next tick we can try and generate an extra mb or whatever
        accumulation += moneyPerTick;
        int amount = (int)accumulation;
        accumulation -= amount;

        amount = Math.min(fluidTank.getCapacity() - fluidTank.getFluidAmount(), amount);

        if(amount > 0)
        {
            amount = corp.consumeProfit(amount);

            if(amount > 0)
            {
                this.fluidTank.fillInternal(new FluidStack(ModFluids.MONEY, amount), true);
            }

            markDirty();
        }
    }

    /*
    @SubscribeEvent
    public void onRewardsChanged(CorporationRewardsChangedEvent event)
    {
        if(world.isRemote) return;

        if(event.owner.equals(owner) && event.rewardId.equals("faster_generation"))
        {
            moneyPerTick = calculateSpeed();
        }
    }
    */

    public float getMoneyPerTick()
    {
        return moneyPerTick;
    }
}
