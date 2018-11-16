package com.mike_caron.megacorp.block.sbs;

import com.mike_caron.megacorp.block.TEContainerBase;
import com.mike_caron.megacorp.util.StringUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerSBS
    extends TEContainerBase
{
    public int fluidAmount = 0;
    public int fluidCapacity = 1;
    public String fluid = null;
    public float progress = 0f;

    Slot emeraldSlot, goldSlot;

    public ContainerSBS(IInventory playerInventory, TileEntitySBS te)
    {
        super(playerInventory, te);
        init();
    }

    private TileEntitySBS getTE()
    {
        return (TileEntitySBS)this.te;
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
    protected int numOwnSlots()
    {
        return 2;
    }

    @Override
    protected void addOwnSlots()
    {
        goldSlot = new SlotItemHandler(getTE().reagents, 0, 34, 29);
        emeraldSlot = new SlotItemHandler(getTE().reagents, 1, 34, 52);

        this.addSlotToContainer(goldSlot);
        this.addSlotToContainer(emeraldSlot);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();


        TileEntitySBS te = getTE();

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
        if(tag.hasKey("Progress"))
        {
            this.progress = tag.getFloat("Progress");
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
        tag.setFloat("Progress", progress);

    }

    @Override
    public int getId()
    {
        return 3;
    }


}
