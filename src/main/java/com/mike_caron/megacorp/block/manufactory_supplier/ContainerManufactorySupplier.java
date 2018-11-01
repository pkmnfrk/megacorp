package com.mike_caron.megacorp.block.manufactory_supplier;

import com.mike_caron.megacorp.block.TEOwnedContainerBase;
import com.mike_caron.megacorp.block.shipping_depot.TileEntityShippingDepot;
import com.mike_caron.megacorp.impl.WorkOrder;
import com.mike_caron.megacorp.storage.SlotItemHandlerFixed;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerManufactorySupplier
    extends TEOwnedContainerBase
{
    public static final int GUI_CHOOSE_QUEST = 1;
    public static final int GUI_STOP_QUEST = 2;
    public static final int GUI_LEVEL_UP = 3;

    public WorkOrder workOrder;
    private int workOrderHash = 0;
    public boolean questLocked;
    public boolean automaticallyGenerate;
    public boolean allowChoice;

    Slot itemInputSlot;

    public ContainerManufactorySupplier(IInventory playerInventory, TileEntityShippingDepot te)
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
        return 48;
    }

    @Override
    protected void addOwnSlots()
    {
        itemInputSlot = new SlotItemHandlerFixed(getTE().inventory, 0, 207, 33);

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

        if(workOrder != te.getWorkOrder())
        {
            this.workOrder = te.getWorkOrder();
            changed = true;
            //if(this.workOrder != null)
            //{
            //    MegaCorpMod.logger.warn("Detected quest: " + this.workOrder.getDesiredItem());
            //}
        }
        else
        {
            if(workOrder != null && workOrder.hashCode() != workOrderHash)
            {
                this.workOrderHash = workOrder.hashCode();
                changed = true;
            }
        }

        if(automaticallyGenerate != te.getAutomaticallyGenerate())
        {
            automaticallyGenerate = te.getAutomaticallyGenerate();
            changed = true;
        }

        if(allowChoice != te.getAllowChoice())
        {
            allowChoice = te.getAllowChoice();
            changed = true;
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

            //MegaCorpMod.logger.warn("Deserialized workorder: " + workOrder.getDesiredItem());
        }
        else
        {
            workOrder = null;
        }
        automaticallyGenerate = tag.getBoolean("AutoGen");
        questLocked = tag.getBoolean("QuestLocked");
        allowChoice = tag.getBoolean("AllowChoice");
    }

    @Override
    protected void onWriteNBT(NBTTagCompound tag)
    {
        super.onWriteNBT(tag);

        if(workOrder != null)
        {
            tag.setTag("WorkOrder", workOrder.serializeNBT());

            //MegaCorpMod.logger.warn("serialized workorder: " + workOrder.getDesiredItem());
        }
        tag.setBoolean("AutoGen", automaticallyGenerate);
        tag.setBoolean("QuestLocked", questLocked);
        tag.setBoolean("AllowChoice", allowChoice);

    }

    @Override
    public int getId()
    {
        return 4;
    }
}
