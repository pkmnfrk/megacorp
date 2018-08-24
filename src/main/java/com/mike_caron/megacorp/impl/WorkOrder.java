package com.mike_caron.megacorp.impl;

import com.google.common.base.Preconditions;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import java.util.Objects;
import java.util.UUID;

public class WorkOrder
{
    private UUID owner;
    private String questId;
    private Kind kind;
    private ItemStack desiredItem;
    private FluidStack desiredFluid;
    private int profit;
    private int progress = 0;


    public WorkOrder(UUID owner, String questId, ItemStack desiredItem, int profit)
    {
        this.owner = owner;
        this.questId = questId;
        this.kind = Kind.ITEM;
        this.desiredItem = desiredItem;
        this.profit = profit;
    }

    public WorkOrder(UUID owner, String questId, FluidStack desiredFluid, int profit)
    {
        this.owner = owner;
        this.questId = questId;
        this.kind = Kind.FLUID;
        this.desiredFluid = desiredFluid;
        this.profit = profit;
    }

    private WorkOrder()
    {
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.owner, this.questId, this.kind, this.kind == Kind.ITEM ? desiredItem : desiredFluid, this.profit, this.progress);
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof WorkOrder)) return false;

        WorkOrder other = (WorkOrder)obj;

        if(!this.owner.equals(other.owner)) return false;
        if(!this.questId.equals(other.questId)) return false;
        if(!this.kind.equals(other.kind)) return false;
        if(this.kind == Kind.ITEM)
        {
            if(!ItemStack.areItemStacksEqual(this.desiredItem,  other.desiredItem)) return false;
        }
        else if(this.kind == Kind.FLUID)
        {
            if(!this.desiredFluid.isFluidStackIdentical(other.desiredFluid)) return false;
        }
        if(this.profit != other.profit) return false;
        if(this.progress != other.progress) return false;

        return true;

    }

    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound ret = new NBTTagCompound();

        ret.setString("Owner", getOwner().toString());
        ret.setString("QuestID", getQuestId());
        ret.setString("Kind", getKind().name());
        ret.setInteger("Profit", getProfit());
        ret.setInteger("Progress", getProgress());

        if(getKind() == Kind.ITEM)
        {
            ret.setTag("Item", getDesiredItem().writeToNBT(new NBTTagCompound()));
        }
        else if(getKind() == Kind.FLUID)
        {
            ret.setTag("Fluid", getDesiredFluid().writeToNBT(new NBTTagCompound()));
        }

        return ret;
    }

    public static WorkOrder fromNBT(NBTTagCompound tag)
    {
        UUID owner = UUID.fromString(tag.getString("Owner"));
        String questId = tag.getString("QuestID");
        Kind kind = Kind.valueOf(tag.getString("Kind"));
        int profit = tag.getInteger("Profit");
        int progress = tag.getInteger("Progress");

        WorkOrder ret;
        if (kind == Kind.ITEM)
        {
            ItemStack desired = new ItemStack(tag.getCompoundTag("Item"));
            ret = new WorkOrder(owner, questId, desired, profit);
        }
        else if (kind == Kind.FLUID)
        {
            FluidStack desired = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("Fluid"));
            ret = new WorkOrder(owner, questId, desired, profit);
        }
        else
        {
            throw new RuntimeException("Unable to discern the work order format");
        }

        ret.progress = progress;

        return ret;
    }

    public UUID getOwner()
    {
        return owner;
    }

    public String getQuestId()
    {
        return questId;
    }

    public Kind getKind()
    {
        return kind;
    }

    public ItemStack getDesiredItem()
    {
        Preconditions.checkArgument(kind == Kind.ITEM);

        return desiredItem;
    }

    public FluidStack getDesiredFluid()
    {
        Preconditions.checkArgument(kind == Kind.FLUID);

        return desiredFluid;
    }

    public int getProfit()
    {
        return profit;
    }

    public int getProgress()
    {
        return progress;
    }

    public int addProgress(int progress)
    {
        Preconditions.checkArgument(progress > 0);

        int newProgress = Math.addExact(this.progress, progress);

        if(kind == Kind.ITEM && newProgress > desiredItem.getCount())
        {
            progress = desiredItem.getCount() - this.progress;
            newProgress = desiredItem.getCount();
        }
        else if(kind == Kind.FLUID && newProgress > desiredFluid.amount)
        {
            progress = desiredFluid.amount - this.progress;
            newProgress = desiredFluid.amount;
        }

        this.progress = newProgress;

        return progress;
    }

    public boolean isComplete()
    {
        if (kind == Kind.ITEM)
        {
            return progress >= desiredItem.getCount();
        }
        else if (kind == Kind.FLUID)
        {
            return progress >= desiredFluid.amount;
        }

        return false;
    }

    public enum Kind
    {
        ITEM,
        FLUID
    }
}
