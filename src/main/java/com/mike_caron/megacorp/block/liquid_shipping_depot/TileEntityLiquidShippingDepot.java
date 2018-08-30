package com.mike_caron.megacorp.block.liquid_shipping_depot;

import com.mike_caron.megacorp.api.ICorporationManager;
import com.mike_caron.megacorp.block.TileEntityOwnedBase;
import com.mike_caron.megacorp.impl.Corporation;
import com.mike_caron.megacorp.impl.CorporationManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidTank;

public class TileEntityLiquidShippingDepot
    extends TileEntityOwnedBase
{
    public MyFluidTank inputFluid = null;

    public TileEntityLiquidShippingDepot()
    {

    }

    @Override
    public void handleGuiButton(EntityPlayerMP player, int button, String extraData)
    {
        if(button == 1)
        {
            if(owner == null)
            {
                ICorporationManager manager = CorporationManager.get(player.getEntityWorld());
                if(!manager.ownerHasCorporation(player.getPersistentID()))
                {
                    manager.createCorporation(player.getPersistentID());
                }

                setOwner(player.getPersistentID());

            }
        }
    }

    @Override
    public void handleGuiString(EntityPlayerMP player, int element, String string)
    {
        if(element == 2)
        {
            if(owner.equals(player.getPersistentID()))
            {
                CorporationManager manager = CorporationManager.get(player.getEntityWorld());
                Corporation corp = manager.getCorporationForOwnerInternal(player.getPersistentID());

                corp.setName(string);

            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);


    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound ret = super.writeToNBT(compound);

        return ret;
    }

    public class MyFluidTank extends FluidTank
    {
        public MyFluidTank(int capacity)
        {
            super(capacity);
        }
    }
}
