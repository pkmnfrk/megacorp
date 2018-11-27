package com.mike_caron.megacorp.block.shipping_depot;

import com.mike_caron.megacorp.api.ICorporation;
import com.mike_caron.megacorp.block.TileEntityOwnedBase;
import com.mike_caron.megacorp.impl.Corporation;
import com.mike_caron.megacorp.impl.Quest;
import com.mike_caron.megacorp.impl.QuestManager;
import com.mike_caron.megacorp.impl.WorkOrder;
import com.mike_caron.megacorp.storage.LimitedItemStackHandler;
import com.mike_caron.megacorp.util.ItemUtils;
import com.mike_caron.megacorp.util.LastResortUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class TileEntityShippingDepot
    extends TileEntityOwnedBase
    implements ITickable
{

    private WorkOrder workOrder;
    private boolean automaticallyGenerate = true;
    private boolean allowChoosing = false;

    public final ItemStackHandler inventory = new LimitedItemStackHandler(this, 1)
    {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack item)
        {
            if(getWorkOrder() == null)
                return false;

            return getWorkOrder().isItemAcceptable(item);
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

        if(compound.hasKey("AllowChoice"))
        {
            allowChoosing = true;
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
        if(allowChoosing)
        {
            ret.setBoolean("AllowChoice", true);
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
            ItemStack desiredItem = ItemStack.EMPTY;
            if(extraData != null)
            {
                desiredItem = ItemUtils.getStackFromTag(extraData);
            }

            if(allowChoosing)
            {
                if (owner != null && workOrder != null)
                {
                    workOrder = null;
                }
            }
            else
            {
                String desiredQuest = null;
                if(!desiredItem.isEmpty())
                {
                    desiredQuest = findQuestForItem(desiredItem);
                }

                rollNewWorkOrder(desiredQuest);
            }
        }
    }

    private String findQuestForItem(ItemStack desiredItem)
    {
        EntityPlayer player = LastResortUtils.getPlayer(owner);
        List<Quest> quests = QuestManager.INSTANCE.getQuests(player);

        for(Quest quest : quests)
        {
            for(ItemStack possibleItem : quest.possibleItems())
            {
                if(possibleItem.isItemEqual(desiredItem))
                    return quest.getId();
            }
        }

        return null;
    }

    private void rollNewWorkOrder(@Nullable String id)
    {
        Corporation corp = getCorporation();

        if(corp == null) return;

        if(id == null)
        {
            workOrder = corp.createNewWorkOrder();
        }
        else
        {
            workOrder = corp.createNewWorkOrder(id);
        }
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

        ICorporation corp = getCorporation();

        if(corp == null) return;
        
        if(getWorkOrder() == null) return;

        ItemStack stack = inventory.getStackInSlot(0);
        if(stack.isEmpty()) return;

        if(workOrder.isItemAcceptable(stack))
        {
            int consumed = workOrder.addProgress(stack.getCount());

            if(consumed > 0)
            {
                stack.shrink(consumed);

                if (corp.completeWorkOrder(workOrder))
                {
                    if(allowChoosing)
                    {
                        if (automaticallyGenerate)
                        {
                            rollNewWorkOrder(workOrder.getQuestId());
                        }
                        else
                        {
                            workOrder = null;
                        }
                    }
                    else
                    {
                        rollNewWorkOrder(null);
                    }
                }

                this.markDirty();
                this.markAndNotify();

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
        return allowChoosing && automaticallyGenerate;
    }

    @Override
    public void setOwner(UUID owner)
    {
        super.setOwner(owner);

        if(owner != null && !allowChoosing)
        {
            rollNewWorkOrder(null);
        }
    }

    public boolean getAllowChoice()
    {
        return allowChoosing;
    }
}
