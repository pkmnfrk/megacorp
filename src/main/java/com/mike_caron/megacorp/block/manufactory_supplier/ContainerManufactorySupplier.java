package com.mike_caron.megacorp.block.manufactory_supplier;

import com.mike_caron.megacorp.block.TEOwnedContainerBase;
import com.mike_caron.megacorp.storage.SlotItemHandlerFixed;
import com.mike_caron.megacorp.util.ItemUtils;
import com.mike_caron.megacorp.util.StringUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

public class ContainerManufactorySupplier
    extends TEOwnedContainerBase
{
    public static final int GUI_CHOOSE_QUEST = 1;
    public static final int GUI_STOP_QUEST = 2;
    public static final int GUI_LEVEL_UP = 3;

    private int workOrderHash = 0;

    public String questId = null;
    public NonNullList<ItemStack> desiredItems = null;
    public int level = 0;
    public int reward = 0;
    public int ticksRemaining = 0;
    public int ticksPerCycle = 0;
    public int progress;
    public int levelUpThreshold = 1;
    public boolean canLevelUp = false;

    Slot itemInputSlot;

    public ContainerManufactorySupplier(IInventory playerInventory, TileEntityManufactorySupplier te)
    {
        super(playerInventory, te);

        init();
    }

    private TileEntityManufactorySupplier getTE()
    {
        return (TileEntityManufactorySupplier)this.te;
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

        TileEntityManufactorySupplier te = getTE();

        if(te.getWorld().isRemote) return;

        if(!StringUtil.areEqual(questId, te.getQuestId()))
        {
            this.questId = te.getQuestId();
            changed = true;
        }

        if(level != te.getLevel())
        {
            level = te.getLevel();
            changed = true;
        }

        if(reward != te.getReward())
        {
            reward = te.getReward();
            changed = true;
        }

        if(ticksRemaining != te.getTicksRemaining())
        {
            ticksRemaining = te.getTicksRemaining();
            changed = true;
        }

        if(progress != te.getProgress())
        {
            progress = te.getProgress();
            changed = true;
        }

        if(ticksPerCycle != te.getTicksPerCycle())
        {
            ticksPerCycle = te.getTicksPerCycle();
            changed = true;
        }

        if(!ItemUtils.areEqual(desiredItems, te.getDesiredItems()))
        {
            desiredItems = te.getDesiredItems();
            changed = true;
        }

        if(levelUpThreshold != te.getLevelUpThreshold())
        {
            levelUpThreshold = te.getLevelUpThreshold();
            changed = true;
        }

        if(changed)
        {
            canLevelUp = progress >= levelUpThreshold;
            triggerUpdate();
        }
    }

    @Override
    protected void onReadNBT(NBTTagCompound compound)
    {
        super.onReadNBT(compound);

        level = compound.getInteger("level");
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

        reward = compound.getInteger("reward");
        ticksRemaining = compound.getInteger("ticksRemaining");
        ticksPerCycle = compound.getInteger("ticksPerCycle");
        progress = compound.getInteger("progress");
        levelUpThreshold = compound.getInteger("levelUpThreshold");
        canLevelUp = compound.getBoolean("canLevelUp");
    }

    @Override
    protected void onWriteNBT(NBTTagCompound ret)
    {
        super.onWriteNBT(ret);
        ret.setInteger("level", level);
        ret.setInteger("ticksRemaining", ticksRemaining);
        ret.setInteger("ticksPerCycle", ticksPerCycle);
        ret.setInteger("reward", reward);
        ret.setInteger("progress", progress);
        if(desiredItems != null)
        {
            NBTTagList list = new NBTTagList();
            for(ItemStack item : desiredItems)
            {
                list.appendTag(item.serializeNBT());
            }
            ret.setTag("desiredItems", list);
        }
        ret.setInteger("levelUpThreshold", levelUpThreshold);
        ret.setBoolean("canLevelUp", canLevelUp);

    }

    @Override
    public int getId()
    {
        return 6;
    }
}
