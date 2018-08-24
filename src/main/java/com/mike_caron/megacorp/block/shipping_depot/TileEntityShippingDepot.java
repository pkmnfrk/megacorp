package com.mike_caron.megacorp.block.shipping_depot;

import com.mike_caron.megacorp.api.ICorporation;
import com.mike_caron.megacorp.block.TileEntityOwnedBase;
import com.mike_caron.megacorp.impl.CorporationManager;
import com.mike_caron.megacorp.impl.WorkOrder;
import com.mike_caron.megacorp.item.ModItems;
import com.mike_caron.megacorp.storage.TweakedItemStackHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityShippingDepot
    extends TileEntityOwnedBase
{
    public WorkOrder workOrder;

    public final TweakedItemStackHandler inventory = new TweakedItemStackHandler(1)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            super.onContentsChanged(slot);

            tryConsumeItem();

            markDirty();
        }
    };

    public TileEntityShippingDepot()
    {

    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        if(compound.hasKey("Inventory"))
        {
            inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
        }

        if(compound.hasKey("WorkOrder"))
        {
            workOrder = WorkOrder.fromNBT(compound.getCompoundTag("WorkOrder"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound ret = super.writeToNBT(compound);

        ret.setTag("Inventory", inventory.serializeNBT());
        if (workOrder != null)
        {
            ret.setTag("WorkOrder", workOrder.serializeNBT());
        }

        return ret;
    }

    @Override
    public void handleGuiButton(EntityPlayerMP player, int button)
    {
        if(!canInteractWith(player)) return;

        if(button == 1)
        {

            if(owner != null)
            {
                workOrder = new WorkOrder(this.owner, "test", new ItemStack(ModItems.corporateCard, 3), 1000);
            }
        }
    }

    private void tryConsumeItem()
    {
        if(world.isRemote) return;
        
        if(workOrder == null) return;

        ItemStack stack = inventory.getStackInSlot(0);
        if(stack.isEmpty()) return;

        if(ItemStack.areItemsEqual(workOrder.getDesiredItem(), stack))
        {
            int consumed = workOrder.addProgress(stack.getCount());

            if(consumed > 0)
            {
                stack.shrink(consumed);
                inventory.notifySlotChanged(0);

                ICorporation corp = CorporationManager.get(world).getCorporationForOwner(owner);
                if (corp.completeWorkOrder(workOrder))
                {
                    workOrder = null;
                }
            }
        }
    }
}
