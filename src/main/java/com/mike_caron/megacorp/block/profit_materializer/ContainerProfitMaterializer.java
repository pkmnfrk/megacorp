package com.mike_caron.megacorp.block.profit_materializer;

import com.mike_caron.megacorp.api.CorporationManager;
import com.mike_caron.megacorp.block.TEOwnedContainerBase;
import com.mike_caron.megacorp.util.StringUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;

public class ContainerProfitMaterializer
    extends TEOwnedContainerBase
{
    public int fluidAmount = 0;
    public int fluidCapacity = 1;
    public String fluid = null;
    public long profitRemaining;
    public float speed = 0f;

    public ContainerProfitMaterializer(IInventory playerInventory, TileEntityProfitMaterializer te)
    {
        super(playerInventory, te);

        init();
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

        if(speed != te.getMoneyPerTick())
        {
            speed = te.getMoneyPerTick();
            changed = true;
        }

        long pr = 0;

        if(owner != null)
        {
            pr = CorporationManager.getInstance(te.getWorld()).getCorporationForOwner(owner).getAvailableProfit();
        }

        if(profitRemaining != pr)
        {
            profitRemaining = pr;
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
        if(tag.hasKey("ProfitRemaining"))
        {
            this.profitRemaining = tag.getLong("ProfitRemaining");
        }
        if(tag.hasKey("Speed"))
        {
            this.speed = tag.getFloat("Speed");
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
        tag.setLong("ProfitRemaining", this.profitRemaining);
        tag.setFloat("Speed", speed);

    }

    @Override
    public int getId()
    {
        return 1;
    }


}
