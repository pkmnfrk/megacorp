package com.mike_caron.megacorp.block.profit_condenser;

import com.mike_caron.mikesmodslib.util.StringUtil;
import com.mike_caron.mikesmodslib.block.TEContainerBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;

public class ContainerProfitCondenser
    extends TEContainerBase
{
    public int inputFluidAmount = 0;
    public int inputFluidCapacity = 1;
    public String inputFluid = null;
    public int outputFluidAmount = 0;
    public int outputFluidCapacity = 1;
    public String outputFluid = null;
    public float progress = 0f;

    public ContainerProfitCondenser(IInventory playerInventory, TileEntityProfitCondenser te)
    {
        super(playerInventory, te);
        init();
    }

    private TileEntityProfitCondenser getTE()
    {
        return (TileEntityProfitCondenser)this.te;
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


        TileEntityProfitCondenser te = getTE();

        if(te.inputFluidTank.getFluidAmount() != this.inputFluidAmount)
        {
            this.inputFluidAmount = te.inputFluidTank.getFluidAmount();
            changed = true;
        }

        if(te.outputFluidTank.getFluidAmount() != this.outputFluidAmount)
        {
            this.outputFluidAmount = te.outputFluidTank.getFluidAmount();
            changed = true;
        }

        if(te.inputFluidTank.getCapacity() != this.inputFluidCapacity)
        {
            this.inputFluidCapacity = te.inputFluidTank.getCapacity();
            changed = true;
        }

        if(te.outputFluidTank.getCapacity() != this.outputFluidCapacity)
        {
            this.outputFluidCapacity = te.outputFluidTank.getCapacity();
            changed = true;
        }


        String fluidName = null;
        if(te.inputFluidTank.getFluid() != null && te.inputFluidTank.getFluid().getFluid() != null)
        {
            fluidName = FluidRegistry.getFluidName(te.inputFluidTank.getFluid().getFluid());
        }

        if(!StringUtil.areEqual(fluidName, this.inputFluid))
        {
            this.inputFluid = fluidName;
            changed = true;
        }

        fluidName = null;
        if(te.outputFluidTank.getFluid() != null && te.outputFluidTank.getFluid().getFluid() != null)
        {
            fluidName = FluidRegistry.getFluidName(te.outputFluidTank.getFluid().getFluid());
        }

        if(!StringUtil.areEqual(fluidName, this.outputFluid))
        {
            this.outputFluid = fluidName;
            changed = true;
        }

        if(progress != te.getProgress())
        {
            progress = te.getProgress();
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

        if(tag.hasKey("InputFluidAmount"))
        {
            this.inputFluidAmount = tag.getInteger("InputFluidAmount");
        }
        if(tag.hasKey("InputFluidCapacity"))
        {
            this.inputFluidCapacity = tag.getInteger("InputFluidCapacity");
        }
        if(tag.hasKey("InputFluid"))
        {
            this.inputFluid = tag.getString("InputFluid");
        }
        else
        {
            this.inputFluid = null;
        }

        if(tag.hasKey("OutputFluidAmount"))
        {
            this.outputFluidAmount = tag.getInteger("OutputFluidAmount");
        }
        if(tag.hasKey("OutputFluidCapacity"))
        {
            this.outputFluidCapacity = tag.getInteger("OutputFluidCapacity");
        }
        if(tag.hasKey("OutputFluid"))
        {
            this.outputFluid = tag.getString("OutputFluid");
        }
        else
        {
            this.outputFluid = null;
        }

        if(tag.hasKey("Progress"))
        {
            this.progress = tag.getFloat("Progress");
        }
    }

    @Override
    protected void onWriteNBT(NBTTagCompound tag)
    {
        super.onWriteNBT(tag);

        tag.setInteger("InputFluidAmount", this.inputFluidAmount);
        tag.setInteger("InputFluidCapacity", this.inputFluidCapacity);
        if(this.inputFluid != null)
        {
            tag.setString("InputFluid", this.inputFluid);
        }
        tag.setInteger("OutputFluidAmount", this.outputFluidAmount);
        tag.setInteger("OutputFluidCapacity", this.outputFluidCapacity);
        if(this.outputFluid != null)
        {
            tag.setString("OutputFluid", this.outputFluid);
        }
        tag.setFloat("Progress", progress);
    }

    @Override
    public int getId()
    {
        return 4;
    }


}
