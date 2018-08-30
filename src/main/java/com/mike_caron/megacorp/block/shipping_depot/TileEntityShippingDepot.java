package com.mike_caron.megacorp.block.shipping_depot;

import com.mike_caron.megacorp.api.ICorporation;
import com.mike_caron.megacorp.block.TileEntityOwnedBase;
import com.mike_caron.megacorp.impl.Corporation;
import com.mike_caron.megacorp.impl.CorporationManager;
import com.mike_caron.megacorp.impl.WorkOrder;
import com.mike_caron.megacorp.storage.TweakedItemStackHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityShippingDepot
    extends TileEntityOwnedBase
    implements ITickable
{

    private WorkOrder workOrder;
    private boolean automaticallyGenerate;

    public final TweakedItemStackHandler inventory = new TweakedItemStackHandler(1)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            super.onContentsChanged(slot);

            markDirty();
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            if(getWorkOrder() == null || !ItemStack.areItemsEqual(stack, workOrder.getDesiredItem()))
            {
                return stack;
            }

            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public int getSlotLimit(int slot)
        {
            if(getWorkOrder() == null) return 0;


            int remaining = workOrder.getDesiredCount() - workOrder.getProgress();
            //return Math.min(remaining, 64);
            return remaining;
        }
    };

    public TileEntityShippingDepot()
    {

    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null)
        {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null)
        {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
        }
        return super.getCapability(capability, facing);
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

        if(compound.hasKey("AutoGen"))
        {
            automaticallyGenerate = true;
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
        if(automaticallyGenerate)
        {
            ret.setBoolean("AutoGen", true);
        }

        return ret;
    }

    @Override
    public void handleGuiButton(EntityPlayerMP player, int button, String extraData)
    {
        if(!canInteractWith(player)) return;

        if(button == ContainerShippingDepot.GUI_NEW_QUEST)
        {

            if(owner != null)
            {
                rollNewWorkOrder(extraData);
            }
        }
        else if(button == ContainerShippingDepot.GUI_REROLL_QUEST)
        {
            if(owner != null && workOrder != null)
            {
                workOrder = null;
            }
        }
    }

    private void rollNewWorkOrder(String id)
    {
        Corporation corp = (Corporation)CorporationManager.get(world).getCorporationForOwner(owner);

        workOrder = corp.createNewWorkOrder(id);
    }

    @Override
    public void handleGuiToggle(EntityPlayerMP player, int element, boolean newState)
    {
        if(!canInteractWith(player)) return;

        if(element == ContainerShippingDepot.GUI_AUTOMATIC_QUEST)
        {
            automaticallyGenerate = newState;
        }
    }

    private void tryConsumeItem()
    {
        if(world.isRemote) return;
        
        if(getWorkOrder() == null) return;

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
                    if(automaticallyGenerate)
                    {
                        rollNewWorkOrder(workOrder.getQuestId());
                    }
                }
            }
        }
    }

    @Override
    public void update()
    {
        if(world.isRemote) return;

        tryConsumeItem();
    }

    public WorkOrder getWorkOrder()
    {
        return workOrder;
    }

    public boolean getAutomaticallyGenerate()
    {
        return automaticallyGenerate;
    }
}
