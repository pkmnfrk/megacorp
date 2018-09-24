package com.mike_caron.megacorp.impl;

import com.google.common.base.Preconditions;
import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.api.ICorporation;
import com.mike_caron.megacorp.api.IReward;
import com.mike_caron.megacorp.api.events.CorporationRewardsChangedEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

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
        NonNullList<ItemStack> item = quest.possibleItems();

        if(item.size() == 0)
        {
            MegaCorpMod.logger.error("Quest returned 0 possible items!");
        }

        int level = getCompletedCountFor(quest.id);
        int qty = quest.getCountForLevel(level);

        int lower_quest_req = getRankInReward("lower_quest_requirements");

        float reduction = 1f - (lower_quest_req * 0.05f);

        qty = (int)Math.ceil(qty * reduction);

        if(qty <= 0)
        {
            MegaCorpMod.logger.warn("Not returning quest, because it is bogus");
            return null;
        }

        int profit = quest.getProfit(qty, level);

        int extra_profit = getRankInReward("extra_profits");

        float profit_increase = 1f + (extra_profit * 0.05f);

        profit = (int)Math.ceil(profit * profit_increase);

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
        if(!rewardLog.isEmpty())
        {
            tag.setTag("Rewards", serializeRewards());
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

        if(tag.hasKey("Rewards"))
        {
            deserializeRewards(tag.getCompoundTag("Rewards"));
        }
        else
        {
            deserializeRewards(null);
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

    private NBTTagCompound serializeRewards()
    {
        NBTTagCompound ret = new NBTTagCompound();

        for(String reward : rewardLog.keySet())
        {
            ret.setInteger(reward, rewardLog.get(reward));
        }

        return ret;
    }

    private void deserializeRewards(NBTTagCompound tag)
    {
        rewardLog = new HashMap<>();

        if(tag != null)
        {
            for(String reward : tag.getKeySet())
            {
                rewardLog.put(reward, tag.getInteger(reward));
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

        this.manager.markDirty();

        MinecraftForge.EVENT_BUS.post(new CorporationRewardsChangedEvent(owner, id, currentRank + 1));

        return true;
    }

    public void setRewardLevel(String id, int level)
    {
        IReward reward = RewardManager.INSTANCE.getRewardWithId(id);
        if(reward == null)
            throw new IllegalArgumentException(id);

        rewardLog.put(id, level);

        this.manager.markDirty();

        MinecraftForge.EVENT_BUS.post(new CorporationRewardsChangedEvent(owner, id, level));
    }

    public void clearRewards()
    {
        // calculate events

        List<CorporationRewardsChangedEvent> events = rewardLog
            .keySet()
            .stream()
            .filter(k -> rewardLog.get(k) > 0)
            .map(k -> new CorporationRewardsChangedEvent(owner,k, 0))
            .collect(Collectors.toList());

        rewardLog.clear();

        this.manager.markDirty();

        // post events

        for(CorporationRewardsChangedEvent event : events)
        {
            MinecraftForge.EVENT_BUS.post(event);
        }
    }
}
