package com.mike_caron.megacorp.block.manufactory_supplier;

import com.mike_caron.megacorp.api.ICorporation;
import com.mike_caron.megacorp.block.TileEntityOwnedBase;
import com.mike_caron.megacorp.impl.Corporation;
import com.mike_caron.megacorp.impl.Quest;
import com.mike_caron.megacorp.impl.QuestManager;
import com.mike_caron.megacorp.storage.TweakedItemStackHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import org.apache.commons.lang3.math.Fraction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class TileEntityManufactorySupplier
    extends TileEntityOwnedBase
    implements ITickable
{
    private static final int DEFAULT_TICKS_PER_CYCLE = 20 * 60 * 5;

    private String questId = null;
    private NonNullList<ItemStack> desiredItems = null;
    private int level = 0;
    private int reward = 0;
    private int ticksRemaining = 0;
    private int itemsPerCycle = 0;
    private int ticksPerCycle = DEFAULT_TICKS_PER_CYCLE;
    private int progress;
    private boolean autoLevel = false;

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
            if(desiredItems == null)
                return stack;

            boolean good = false;
            for(ItemStack item : desiredItems)
            {
                if(item.isItemEqual(stack))
                {
                    good = true;
                    break;
                }
            }

            if(!good)
                return stack;

            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public int getSlotLimit(int slot)
        {
            return 64;
        }
    };

    public TileEntityManufactorySupplier()
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
        else
        {
            inventory.setStackInSlot(0, ItemStack.EMPTY);
        }

        questId = null;
        if(compound.hasKey("questId"))
            questId = compound.getString("questId");

        level = 0;
        if(compound.hasKey("level"))
        {
            level = compound.getInteger("level");
        }

        desiredItems = null;
        if(compound.hasKey("desiredItems"))
        {
            NBTTagList list = compound.getTagList("desiredItems", Constants.NBT.TAG_COMPOUND);

            desiredItems = NonNullList.create();
            for(int i = 0; i < list.tagCount(); i++)
            {
                ItemStack tmp = new ItemStack(list.getCompoundTagAt(i));
                if(!tmp.isEmpty())
                {
                    desiredItems.add(tmp);
                }
            }

            if(desiredItems.isEmpty())
            {
                desiredItems = null;
            }
        }

        reward = 0;
        if(compound.hasKey("reward"))
        {
            reward = compound.getInteger("reward");
        }

        ticksRemaining = 0;
        if(compound.hasKey("ticksRemaining"))
        {
            ticksRemaining = compound.getInteger("ticksRemaining");
        }

        ticksPerCycle = DEFAULT_TICKS_PER_CYCLE;
        if(compound.hasKey("ticksPerCycle"))
        {
            ticksPerCycle = compound.getInteger("ticksPerCycle");
        }

        progress = 0;
        if(compound.hasKey("progress"))
        {
            progress = compound.getInteger("progress");
        }

        itemsPerCycle = 0;
        if(compound.hasKey("itemsPerCycle"))
        {
            itemsPerCycle = compound.getInteger("itemsPerCycle");
        }

        autoLevel = false;
        if(compound.hasKey("autoLevel"))
        {
            autoLevel = compound.getBoolean("autoLevel");
        }

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound ret = super.writeToNBT(compound);

        ret.setTag("Inventory", inventory.serializeNBT());
        if(questId != null)
            ret.setString("questId", questId);
        ret.setInteger("level", level);
        ret.setInteger("ticksRemaining", ticksRemaining);
        ret.setInteger("ticksPerCycle", ticksPerCycle);
        ret.setInteger("reward", reward);
        ret.setInteger("progress", progress);
        ret.setInteger("itemsPerCycle", itemsPerCycle);
        ret.setBoolean("autoLevel", autoLevel);
        if(desiredItems != null)
        {
            NBTTagList list = new NBTTagList();
            for(ItemStack item : desiredItems)
            {
                list.appendTag(item.serializeNBT());
            }
            ret.setTag("desiredItems", list);
        }


        return ret;
    }

    @Override
    public void handleGuiButton(EntityPlayerMP player, int button, String extraData)
    {
        if(!canInteractWith(player)) return;

        if(button == ContainerManufactorySupplier.GUI_CHOOSE_QUEST)
        {
            if(owner != null)
            {
                handleQuest(extraData, 0);
            }
        }
        else if(button == ContainerManufactorySupplier.GUI_STOP_QUEST)
        {
            desiredItems = null;
            questId = null;
        }
        else if(button == ContainerManufactorySupplier.GUI_LEVEL_UP)
        {
            if(owner != null)
            {
                levelUp();
            }
        }

        markDirty();
    }

    private void levelUp()
    {
        if(progress >= getLevelUpThreshold())
        {
            handleQuest(questId, level + 1);
        }
    }

    @Override
    public void handleGuiToggle(EntityPlayerMP player, int element, boolean bool)
    {
        switch(element)
        {
            case ContainerManufactorySupplier.GUI_AUTOLEVEL:
                autoLevel = bool;
                break;
        }
    }

    private void handleQuest(@Nonnull String id, int level)
    {
        Quest quest = QuestManager.INSTANCE.getSpecificQuest(id);

        if(quest == null)
            return;

        handleQuest(quest, level);
    }

    private void handleQuest(Quest quest, int level)
    {
        desiredItems = quest.possibleItems();
        ItemStack sample = desiredItems.get(0);

        if(level > 50) level = 50;

        Fraction frac = Fraction.getFraction(desiredItems.get(0).getMaxStackSize(), 600 + 5400 * (50 - level) / 50).reduce();

        int num = frac.getNumerator();
        int den = frac.getDenominator();

        while(den < 600) //30 seconds
        {
            num *= 2;
            den *= 2;
        }

        if(num > sample.getMaxStackSize())
        {
            num = sample.getMaxStackSize();
        }

        int profit = (int)(quest.baseProfit * sample.getMaxStackSize() * Math.pow(1.15, level));

        profit *= 2;

        this.questId = quest.id;
        this.level = level;
        this.reward = profit;
        this.itemsPerCycle = num;
        this.ticksPerCycle = den;
        this.ticksRemaining = this.ticksPerCycle;
        this.progress = 0;
    }

    private void tryConsumeItem()
    {
        if(world.isRemote) return;

        ICorporation corp = getCorporation();

        if(corp == null) return;
        
        if(desiredItems == null) return;

        ItemStack stack = inventory.getStackInSlot(0);
        if(stack.getCount() < itemsPerCycle)
        {
            //Failure :(
            if(progress > 0 || level > 0)
            {
                if(progress > 0)
                {
                    progress -= 3;
                    if(progress < 0) progress = 0;
                }
                else
                {
                    progress -= 1;
                }

                levelDown();
            }

            return;
        }

        stack.shrink(itemsPerCycle);

        inventory.notifySlotChanged(0);

        ((Corporation) corp).addProfit(reward);

        if(progress < getLevelUpThreshold())
        {
            progress += 1;
        }

        levelUp();

        this.markDirty();
        this.markAndNotify();

    }

    private void levelDown()
    {
        if (progress <= -10)
        {
            handleQuest(questId, level - 1);
            autoLevel = false;
        }
    }

    @Override
    public void update()
    {
        if(world.isRemote || owner == null) return;

        if(desiredItems == null) return;

        if(ticksRemaining > 0)
        {
            ticksRemaining -= 1;
        }

        if(ticksRemaining > 0) return;

        tryConsumeItem();

        ticksRemaining = ticksPerCycle;
    }

    @Override
    public void setOwner(UUID owner)
    {
        super.setOwner(owner);
    }

    public String getQuestId()
    {
        return questId;
    }

    public int getLevel()
    {
        return level;
    }

    public int getReward()
    {
        return reward;
    }

    public int getTicksRemaining()
    {
        return ticksRemaining;
    }

    public int getTicksPerCycle()
    {
        return ticksPerCycle;
    }

    public int getProgress()
    {
        return progress;
    }

    @Nullable
    public NonNullList<ItemStack> getDesiredItems()
    {
        return desiredItems;
    }

    public int getLevelUpThreshold()
    {
        return 10 + (level * 3);
    }

    public int getItemsPerCycle()
    {
        return itemsPerCycle;
    }

    public boolean getAutoLevel()
    {
        return autoLevel;
    }

}
