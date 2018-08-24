package com.mike_caron.megacorp.block.shipping_depot;

import com.mike_caron.megacorp.api.ICorporation;
import com.mike_caron.megacorp.api.ICorporationManager;
import com.mike_caron.megacorp.block.TEOwnedContainerBase;
import com.mike_caron.megacorp.impl.CorporationManager;
import com.mike_caron.megacorp.impl.WorkOrder;
import com.mike_caron.megacorp.storage.SlotItemHandlerFixed;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerShippingDepot
    extends TEOwnedContainerBase
{
    public WorkOrder workOrder;
    private int workOrderHash = 0;

    Slot itemInputSlot;

    public ContainerShippingDepot(IInventory playerInventory, TileEntityShippingDepot te)
    {
        super(playerInventory, te);

        init();
    }

    private TileEntityShippingDepot getTE()
    {
        return (TileEntityShippingDepot)this.te;
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
    protected void addOwnSlots()
    {
        itemInputSlot = new SlotItemHandlerFixed(getTE().inventory, 0, 128, 33);

        this.addSlotToContainer(itemInputSlot);
    }

    @Override
    protected int numOwnSlots()
    {
        return 1;
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        TileEntityShippingDepot te = getTE();

        if(te.getWorld().isRemote) return;

        ICorporationManager manager = CorporationManager.get(te.getWorld());

        if(workOrder != te.workOrder)
        {
            this.workOrder = te.workOrder;
            changed = true;
        }
        else
        {
            if(workOrder != null && workOrder.hashCode() != workOrderHash)
            {
                this.workOrderHash = workOrder.hashCode();
                changed = true;
            }
        }

        if(owner != null)
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

        if(tag.hasKey("WorkOrder"))
        {
            workOrder = WorkOrder.fromNBT(tag.getCompoundTag("WorkOrder"));
        }
        else
        {
            workOrder = null;
        }
    }

    @Override
    protected void onWriteNBT(NBTTagCompound tag)
    {
        super.onWriteNBT(tag);

        if(workOrder != null)
        {
            tag.setTag("WorkOrder", workOrder.serializeNBT());
        }

    }

    @Override
    public int getId()
    {
        return 4;
    }
}
