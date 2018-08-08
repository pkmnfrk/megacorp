package com.mike_caron.megacorp.block.profit_materializer;

import com.mike_caron.megacorp.block.TEContainerBase;
import com.mike_caron.megacorp.util.StringUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;

public class ContainerProfitMaterializer
        extends TEContainerBase
{
    public int fluidAmount = 0;
    public int fluidCapacity = 1;
    public String fluid = null;

    public ContainerProfitMaterializer(IInventory playerInventory, TileEntityProfitMaterializer te)
    {
        super(playerInventory, te);
    }

    private TileEntityProfitMaterializer getTE()
    {
        return (TileEntityProfitMaterializer)this.te;
    }

    @Override
    protected int playerInventoryY()
    {
        return 84;
    }

    @Override
    protected int playerInventoryX()
    {
        return 8;
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        TileEntityProfitMaterializer te = getTE();

        if(te.fluidTank.getFluidAmount() != this.fluidAmount)
        {
            this.fluidAmount = te.fluidTank.getFluidAmount();
            changed = true;
        }

        if(te.fluidTank.getCapacity() != this.fluidCapacity)
        {
            this.fluidCapacity = te.fluidTank.getCapacity();
            changed = true;
        }


        String fluidName = null;
        if(te.fluidTank.getFluid() != null && te.fluidTank.getFluid().getFluid() != null)
        {
            fluidName = FluidRegistry.getFluidName(te.fluidTank.getFluid().getFluid());
        }

        if(!StringUtil.areEqual(fluidName, this.fluid))
        {
            this.fluid = fluidName;
            changed = true;
        }

        if(changed)
        {
            this.triggerUpdate();
        }
    }

    @Override
    protected void onReadNBT(NBTTagCompound tag)
    {
        super.onReadNBT(tag);

        if(tag.hasKey("FluidAmount"))
        {
            this.fluidAmount = tag.getInteger("FluidAmount");
        }
        if(tag.hasKey("FluidCapacity"))
        {
            this.fluidCapacity = tag.getInteger("FluidCapacity");
        }
        if(tag.hasKey("Fluid"))
        {
            this.fluid = tag.getString("Fluid");
        }
        else
        {
            this.fluid = null;
        }
    }

    @Override
    protected void onWriteNBT(NBTTagCompound tag)
    {
        super.onWriteNBT(tag);

        tag.setInteger("FluidAmount", this.fluidAmount);
        tag.setInteger("FluidCapacity", this.fluidCapacity);
        if(this.fluid != null)
        {
            tag.setString("Fluid", this.fluid);
        }

    }

    @Override
    public int getId()
    {
        return 1;
    }


}
