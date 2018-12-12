package com.mike_caron.megacorp.block.liquid_shipping_depot;

import com.mike_caron.megacorp.api.ICorporation;
import com.mike_caron.megacorp.api.ICorporationManager;
import com.mike_caron.megacorp.block.TEOwnedContainerBase;
import com.mike_caron.megacorp.impl.CorporationManager;
import com.mike_caron.mikesmodslib.util.TankTracker;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerLiquidShippingDepot
    extends TEOwnedContainerBase
{
    TankTracker tankTracker;

    public ContainerLiquidShippingDepot(IInventory playerInventory, TileEntityLiquidShippingDepot te)
    {
        super(playerInventory, te);

        tankTracker = new TankTracker(te.inputFluid);

        init();
    }

    private TileEntityLiquidShippingDepot getTE()
    {
        return (TileEntityLiquidShippingDepot)this.te;
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

        TileEntityLiquidShippingDepot te = getTE();

        if(te.getWorld().isRemote) return;

        if(tankTracker.detect())
        {
            changed = true;
        }

        ICorporationManager manager = CorporationManager.get(te.getWorld());


        if(owner != null && manager.ownerHasCorporation(owner))
        {
            ICorporation corp = manager.getCorporationForOwner(owner);


        }

        if(changed)
        {
            triggerUpdate();
        }
    }

    @Override
    protected void onReadNBT(NBTTagCompound tag)
    {
        super.onReadNBT(tag);

        tankTracker.deserializeNBT(tag.getCompoundTag("InputFluid"));
    }

    @Override
    protected void onWriteNBT(NBTTagCompound tag)
    {
        super.onWriteNBT(tag);

        tag.setTag("InputFluid", tankTracker.serializeNBT());
    }

    @Override
    public int getId()
    {
        return 5;
    }
}
