package com.mike_caron.megacorp.impl;

import com.google.common.base.Preconditions;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Objects;
import java.util.UUID;

public class WorkOrder
{
    private UUID owner;
    private String questId;
    private Kind kind;
    private NonNullList<ItemStack> desiredItem;
    private int desiredCount;
    private FluidStack desiredFluid;
    private int profit;
    private int progress = 0;
    private int level = 0;


    public WorkOrder(UUID owner, String questId, ItemStack desiredItem, int desiredCount, int profit, int level)
    {
        this.owner = owner;
        this.questId = questId;
        this.kind = Kind.ITEM;
        this.desiredItem = NonNullList.create();
        this.desiredItem.add(desiredItem);
        this.desiredCount = desiredCount;
        this.profit = profit;
        this.level = level;
    }

    public WorkOrder(UUID owner, String questId, NonNullList<ItemStack> desiredItems, int desiredCount, int profit, int level)
    {
        Preconditions.checkArgument(desiredItems.size() > 0, "No desired items??");

        this.owner = owner;
        this.questId = questId;
        this.kind = Kind.ITEM;
        this.desiredItem = desiredItems;
        this.desiredCount = desiredCount;
        this.profit = profit;
        this.level = level;
    }

    public WorkOrder(UUID owner, String questId, FluidStack desiredFluid, int profit, int level)
    {
        this.owner = owner;
        this.questId = questId;
        this.kind = Kind.FLUID;
        this.desiredFluid = desiredFluid;
        this.desiredCount = this.desiredFluid.amount;
        this.profit = profit;
        this.level = level;
    }

    private WorkOrder()
    {
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.owner, this.questId, this.kind, this.kind == Kind.ITEM ? desiredItem : desiredFluid, this.desiredCount, this.profit, this.progress, this.level);
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
            if(this.desiredItem.size() != other.desiredItem.size())
                return false;

            for(int i = 0; i < this.desiredItem.size(); i++)
            {
                if(!ItemStack.areItemsEqual(this.desiredItem.get(i), other.desiredItem.get(i)))
                    return false;
            }
        }
        else if(this.kind == Kind.FLUID)
        {
            if(!this.desiredFluid.isFluidStackIdentical(other.desiredFluid)) return false;
        }
        if(this.desiredCount != other.desiredCount) return false;
        if(this.profit != other.profit) return false;
        if(this.progress != other.progress) return false;
        if(this.level != other.level) return false;

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
            NBTTagList items = new NBTTagList();
            for(ItemStack item : getDesiredItems())
            {
                items.appendTag(item.writeToNBT(new NBTTagCompound()));
            }
            ret.setTag("Items", items);
        }
        else if(getKind() == Kind.FLUID)
        {
            ret.setTag("Fluid", getDesiredFluid().writeToNBT(new NBTTagCompound()));
        }
        ret.setInteger("Count", desiredCount);
        ret.setInteger("Level", level);

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
        ret.level = tag.getInteger("Level");

        if (ret.kind == Kind.ITEM)
        {
            if(tag.hasKey("Item"))
            {
                ret.desiredItem = NonNullList.from(ItemStack.EMPTY, new ItemStack(tag.getCompoundTag("Item")));
            }
            else
            {
                ret.desiredItem = NonNullList.create();
                NBTTagList items = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
                for(int i = 0; i < items.tagCount(); i++)
                {
                    ret.desiredItem.add(new ItemStack(items.getCompoundTagAt(i)));
                }
            }
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

    public NonNullList<ItemStack> getDesiredItems()
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

    public int getLevel()
    {
        return level;
    }

    public boolean isItemAcceptable(ItemStack item)
    {
        return OreDictionary.containsMatch(true, this.desiredItem, item);
    }

    public enum Kind
    {
        ITEM,
        FLUID
    }
}
