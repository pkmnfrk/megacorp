package com.mike_caron.megacorp.block.profit_materializer;

import com.mike_caron.megacorp.block.TEContainerBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

public class ContainerProfitMaterializer
        extends TEContainerBase
{
    public int fluidAmount = 0;

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

        //for(IContainerListener crafter : this.listeners)
        //{
            if(te.fluidTank.getFluidAmount() != this.fluidAmount)
            {
                this.fluidAmount = te.fluidTank.getFluidAmount();

                this.triggerUpdate();
            }
        //}
    }

    @Override
    protected void onReadNBT(NBTTagCompound tag)
    {
        if(tag.hasKey("tank"))
        {
            getTE().fluidTank.setFluid(FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("tank")));
        }
    }

    @Override
    protected void onWriteNBT(NBTTagCompound tag)
    {
        NBTTagCompound tank = new NBTTagCompound();
        getTE().fluidTank.getFluid().writeToNBT(tank);
        tag.setTag("tank", tank);

    }

    @Override
    public int getId()
    {
        return 1;
    }
}
