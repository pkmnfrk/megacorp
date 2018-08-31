package com.mike_caron.megacorp.impl;

import com.google.common.base.Preconditions;
import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.api.ICorporation;
import com.mike_caron.megacorp.api.IReward;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Corporation
        implements ICorporation, INBTSerializable<NBTTagCompound>
{
    private final Lock lock = new ReentrantLock();
    private UUID owner;
    private String name;
    private long availableProfit;
    private long totalProfit;
    private Map<String, Integer> questLog = new HashMap<>();
    private Map<String, Integer> rewardLog = new HashMap<>();

    @Nonnull
    private final CorporationManager manager;

    public Corporation(@Nonnull CorporationManager manager)
    {
        this.manager = manager;
    }

    public Corporation(@Nonnull CorporationManager manager,@Nonnull UUID owner)
    {
        this.manager = manager;
        this.owner = owner;
    }

    @Override
    public long getTotalProfit()
    {
        return totalProfit;
    }

    @Override
    @Nonnull
    public UUID getOwner()
    {
        return owner;
    }

    @Override
    @Nonnull
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
        this.manager.markDirty();
    }

    @Override
    public long getAvailableProfit()
    {
        return availableProfit;
    }

    public int consumeProfit(int amount)
    {
        Preconditions.checkArgument(amount >= 0);
        lock.lock();

        try
        {
            if (amount > availableProfit)
            {
                amount = (int) availableProfit;
            }

            availableProfit -= amount;

            this.manager.markDirty();

            return amount;
        }
        finally
        {
            lock.unlock();
        }

    }

    @Override
    public boolean completeWorkOrder(WorkOrder workOrder)
    {
        lock.lock();
        try
        {
            if (workOrder.isComplete())
            {
                this.addProfit(workOrder.getProfit());

                int completed = questLog.getOrDefault(workOrder.getQuestId(), 0);
                completed = Math.incrementExact(completed);
                questLog.put(workOrder.getQuestId(), completed);

                return true;
            }
            return false;
        }
        finally
        {
            lock.unlock();
        }
    }

    public void addProfit(int amount)
    {
        Preconditions.checkArgument(amount >= 0);

        lock.lock();

        try
        {
            availableProfit = Math.addExact(availableProfit, amount);
            totalProfit = Math.addExact(totalProfit, amount);

            this.manager.markDirty();
        }
        finally
        {
            lock.unlock();
        }
    }

    public int getCompletedCountFor(String questId)
    {
        if(!questLog.containsKey(questId))
        {
            return 0;
        }

        return questLog.get(questId);
    }

    public WorkOrder createNewWorkOrder(String questId)
    {
        Quest quest = QuestManager.INSTANCE.getSpecificQuest(questId);
        if(quest == null)
            return null;

        return createNewWorkOrder(quest);
    }

    public WorkOrder createNewWorkOrder()
    {
        //first, select a quest
        Quest quest = QuestManager.INSTANCE.getRandomQuest();
        return createNewWorkOrder(quest);
    }

    private WorkOrder createNewWorkOrder(Quest quest)
    {
        ItemStack item = quest.item.copy();
        int level = getCompletedCountFor(quest.id);
        int qty = quest.getCountForLevel(level);

        if(qty <= 0)
        {
            MegaCorpMod.logger.warn("Not returning quest, because it is bogus");
            return null;
        }

        int profit = quest.getProfit(qty);

        WorkOrder ret = new WorkOrder(this.owner, quest.id, item, qty, profit, level);

        return ret;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setString("Owner", owner.toString());
        tag.setString("Name", name);
        tag.setLong("AvailableProfit", availableProfit);
        tag.setLong("TotalProfit", totalProfit);
        if(!questLog.isEmpty())
        {
            tag.setTag("Quests", serializeLog());
        }

        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag)
    {
        owner = UUID.fromString(tag.getString("Owner"));
        name = tag.getString("Name");
        availableProfit = tag.getLong("AvailableProfit");
        totalProfit = tag.getLong("TotalProfit");
        if(tag.hasKey("Quests"))
        {
            deserializeLog(tag.getCompoundTag("Quests"));
        }
        else
        {
            deserializeLog(null);
        }
    }

    private NBTTagCompound serializeLog()
    {
        NBTTagCompound ret = new NBTTagCompound();

        for(String quest : questLog.keySet())
        {
            ret.setInteger(quest, questLog.get(quest));
        }

        return ret;
    }

    private void deserializeLog(NBTTagCompound tag)
    {
        questLog = new HashMap<>();

        if(tag != null)
        {
            for(String quest : tag.getKeySet())
            {
                questLog.put(quest, tag.getInteger(quest));
            }
        }
    }

    @Override
    public int getRankInReward(String id)
    {
        if(rewardLog.containsKey(id))
        {
            return rewardLog.get(id);
        }

        return 0;
    }

    public Optional<Integer> getCostForReward(String id)
    {
        IReward reward = RewardManager.INSTANCE.getRewardWithId(id);
        if(reward == null)
            throw new IllegalArgumentException(id);

        int currentRank = getRankInReward(id);

        if(currentRank >= reward.numRanks())
        {
            return Optional.empty();
        }

        return Optional.of(reward.costForRank(currentRank + 1));
    }

    public boolean purchaseReward(String id)
    {
        IReward reward = RewardManager.INSTANCE.getRewardWithId(id);
        if(reward == null)
            throw new IllegalArgumentException(id);

        int currentRank = getRankInReward(id);

        if(currentRank >= reward.numRanks())
        {
            return false;
        }

        rewardLog.put(id, currentRank + 1);

        return true;
    }
}
