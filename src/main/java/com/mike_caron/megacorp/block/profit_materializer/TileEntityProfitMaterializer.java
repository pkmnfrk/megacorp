package com.mike_caron.megacorp.block.profit_materializer;

import com.mike_caron.megacorp.api.ICorporation;
import com.mike_caron.megacorp.api.events.CorporationRewardsChangedEvent;
import com.mike_caron.megacorp.block.TileEntityOwnedBase;
import com.mike_caron.megacorp.fluid.ModFluids;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.math.Fraction;

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
    private Fraction speed = null;

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
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound ret = super.writeToNBT(compound);

        ret.setTag("tank", fluidTank.writeToNBT(new NBTTagCompound()));
        ret.setInteger("timer", timer);

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

    private Fraction calculateSpeed()
    {
        ICorporation corp = getCorporation();

        if(corp == null) return Fraction.ONE_QUARTER;

        int rewardRank = corp.getRankInReward("faster_generation");

        return Fraction.getReducedFraction(rewardRank + 2, 8);
    }

    @Override
    public void update()
    {
        if(world == null || world.isRemote) return;

        ICorporation corp = getCorporation();

        if(corp == null) return;

        if(speed == null)
        {
            speed = calculateSpeed();
        }

        timer += 1;

        int amount = Math.min(fluidTank.getCapacity() - fluidTank.getFluidAmount(), speed.getNumerator());
        if(amount > 0)
        {
            if(timer > speed.getDenominator())
            {
                amount = corp.consumeProfit(amount);

                if(amount > 0)
                {
                    this.fluidTank.fillInternal(new FluidStack(ModFluids.MONEY, amount), true);
                }
                timer = 0;
            }
        }

    }

    @SubscribeEvent
    public void onRewardsChanged(CorporationRewardsChangedEvent event)
    {
        if(world.isRemote) return;

        if(event.owner.equals(owner) && event.rewardId.equals("faster_generation"))
        {
            speed = calculateSpeed();
        }
    }

    public Fraction getSpeed()
    {
        return speed;
    }
}
