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
    private int desiredCount;
    private FluidStack desiredFluid;
    private int profit;
    private int progress = 0;


    public WorkOrder(UUID owner, String questId, ItemStack desiredItem, int desiredCount, int profit)
    {
        this.owner = owner;
        this.questId = questId;
        this.kind = Kind.ITEM;
        this.desiredItem = desiredItem;
        this.desiredCount = desiredCount;
        this.profit = profit;
    }

    public WorkOrder(UUID owner, String questId, FluidStack desiredFluid, int profit)
    {
        this.owner = owner;
        this.questId = questId;
        this.kind = Kind.FLUID;
        this.desiredFluid = desiredFluid;
        this.desiredCount = this.desiredFluid.amount;
        this.profit = profit;
    }

    private WorkOrder()
    {
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.owner, this.questId, this.kind, this.kind == Kind.ITEM ? desiredItem : desiredFluid, this.desiredCount, this.profit, this.progress);
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
        if(this.desiredCount != other.desiredCount) return false;
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
        ret.setInteger("Count", desiredCount);

        return ret;
    }

    public static WorkOrder fromNBT(NBTTagCompound tag)
    {
        WorkOrder ret = new WorkOrder();

        ret.owner = UUID.fromString(tag.getString("Owner"));
        ret.questId = tag.getString("QuestID");
        ret.kind = Kind.valueOf(tag.getString("Kind"));
        ret.profit = tag.getInteger("Profit");
        ret.progress = tag.getInteger("Progress");
        ret.desiredCount = tag.getInteger("Count");

        if (ret.kind == Kind.ITEM)
        {
            ret.desiredItem = new ItemStack(tag.getCompoundTag("Item"));
        }
        else if (ret.kind == Kind.FLUID)
        {
            ret.desiredFluid = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("Fluid"));
        }
        else
        {
            throw new RuntimeException("Unable to discern the work order format");
        }

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

    public int getDesiredCount() {
        return desiredCount;
    }

    public int addProgress(int progress)
    {
        Preconditions.checkArgument(progress > 0);

        int newProgress = Math.addExact(this.progress, progress);

        if(newProgress > desiredCount)
        {
            progress = desiredCount - this.progress;
            newProgress = desiredCount;
        }

        this.progress = newProgress;

        return progress;
    }

    public boolean isComplete()
    {
        return progress >= desiredCount;
    }

    public enum Kind
    {
        ITEM,
        FLUID
    }
}
