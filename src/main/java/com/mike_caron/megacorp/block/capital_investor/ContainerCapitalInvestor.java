package com.mike_caron.megacorp.block.capital_investor;

import com.mike_caron.megacorp.api.CorporationManager;
import com.mike_caron.megacorp.api.ICorporation;
import com.mike_caron.megacorp.api.IReward;
import com.mike_caron.megacorp.api.events.CorporationRewardsChangedEvent;
import com.mike_caron.megacorp.block.TEOwnedContainerBase;
import com.mike_caron.megacorp.impl.RewardManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class ContainerCapitalInvestor
    extends TEOwnedContainerBase
{
    public static final int GUI_BUY_REWARD = 1;

    public int fluidAmount = 0;
    public int fluidCapacity = 1;
    public String fluid = null;

    public int rewardsSerial = 0;
    public int lastRewardsSerial = -1;

    public List<RewardData> rewardList = null;

    public ContainerCapitalInvestor(IInventory playerInventory, TileEntityCapitalInvestor te)
    {
        super(playerInventory, te);

        init();
    }

    private TileEntityCapitalInvestor getTE()
    {
        return (TileEntityCapitalInvestor) this.te;
    }

    @Override
    protected int playerInventoryY()
    {
        return 84;
    }

    @Override
    protected int playerInventoryX()
    {
        return 8;
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        TileEntityCapitalInvestor te = getTE();

        if(te.getWorld().isRemote) return;

        if(lastRewardsSerial != rewardsSerial)
        {
            lastRewardsSerial = rewardsSerial;
            rewardList = null;

            changed = true;
        }

        if(changed)
        {
            triggerUpdate();
        }
    }

    @Override
    protected void onReadNBT(NBTTagCompound tag)
    {
        super.onReadNBT(tag);

        NBTTagList rewards = tag.getTagList("rewards", Constants.NBT.TAG_COMPOUND);

        rewardList = new ArrayList<>();

        if(tag.hasKey("rewards"))
        {
            for (int i = 0; i < rewards.tagCount(); i++)
            {
                NBTTagCompound reward = rewards.getCompoundTagAt(i);

                RewardData data = RewardData.fromNbt(reward);

                rewardList.add(data);
            }
        }
    }

    @Override
    protected void onWriteNBT(NBTTagCompound tag)
    {
        super.onWriteNBT(tag);

        TileEntityCapitalInvestor te = getTE();

        if(te.getOwner() != null)
        {
            ICorporation corp = CorporationManager.getInstance(te.getWorld()).getCorporationForOwner(te.getOwner());

            NBTTagList rewards = new NBTTagList();

            if (rewardList == null)
            {
                buildRewardList(corp);
            }

            for (RewardData reward : rewardList)
            {
                rewards.appendTag(reward.serialize());
            }

            tag.setTag("rewards", rewards);
        }

    }

    private void buildRewardList(ICorporation corp)
    {
        rewardList = new ArrayList<>();

        for(IReward reward : RewardManager.INSTANCE.getRewards())
        {
            RewardData rd = new RewardData();
            rd.id = reward.getId();
            rd.currentRank = corp.getRankInReward(reward.getId());
            if(rd.currentRank >= reward.numRanks())
                continue;

            rd.nextRank = rd.currentRank + 1;
            rd.nextRankCost = reward.costForRank(rd.nextRank);
            rewardList.add(rd);
        }
    }

    @Override
    public int getId()
    {
        return 4;
    }

    @SubscribeEvent
    public void onRewardsChanged(CorporationRewardsChangedEvent event)
    {
        if(event.owner.equals(getTE().getOwner()))
        {
            rewardsSerial += 1;
        }
    }

    public static class RewardData
    {
        public String id;
        public int currentRank;
        public int nextRank;
        public int nextRankCost;

        public NBTTagCompound serialize()
        {
            NBTTagCompound ret = new NBTTagCompound();

            ret.setString("id", id);
            ret.setIntArray("d", new int[] { currentRank, nextRank, nextRankCost });
            ret.setInteger("currentRank", currentRank);
            ret.setInteger("nextRank", nextRank);
            ret.setInteger("nextRankCost", nextRankCost);

            return ret;
        }

        public static RewardData fromNbt(NBTTagCompound tag)
        {
            RewardData ret = new RewardData();
            int[] data = tag.getIntArray("d");

            ret.id = tag.getString("id");
            ret.currentRank = data[0];
            ret.nextRank = data[1];
            ret.nextRankCost = data[2];

            return ret;
        }
    }
}
