package com.mike_caron.megacorp.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public class TankTracker
    implements INBTSerializable<NBTTagCompound>
{
    private FluidStack fluid;

    IFluidTank tank;

    public TankTracker(IFluidTank tank)
    {
        this.tank = tank;
    }

    public boolean detect()
    {
        boolean ret = false;

        if(fluid == null && tank.getFluid() != null || fluid.isFluidStackIdentical(tank.getFluid()))
        {
            fluid = tank.getFluid();
            ret = true;
        }

        return ret;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound ret = new NBTTagCompound();
        if(fluid != null)
        {
            fluid.writeToNBT(ret);
        }
        return ret;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbtTagCompound)
    {
        fluid = FluidStack.loadFluidStackFromNBT(nbtTagCompound);
    }
}
