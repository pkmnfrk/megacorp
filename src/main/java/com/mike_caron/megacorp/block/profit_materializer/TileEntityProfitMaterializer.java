package com.mike_caron.megacorp.block.profit_materializer;

import com.mike_caron.megacorp.block.TileEntityBase;
import com.mike_caron.megacorp.fluid.ModFluids;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

public class TileEntityProfitMaterializer
        extends TileEntityBase
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

    @Override
    public void update()
    {
        if(world == null || world.isRemote) return;

        timer += 1;

        if(timer > 8)
        {
            this.fluidTank.fillInternal(new FluidStack(ModFluids.MONEY, 2), true);
            timer = 0;
        }

    }
}
