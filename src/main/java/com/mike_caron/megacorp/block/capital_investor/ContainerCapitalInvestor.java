package com.mike_caron.megacorp.block.capital_investor;

import com.mike_caron.megacorp.api.CorporationManager;
import com.mike_caron.megacorp.api.ICorporation;
import com.mike_caron.megacorp.api.ICorporationManager;
import com.mike_caron.megacorp.api.IReward;
import com.mike_caron.megacorp.api.events.CorporationRewardsChangedEvent;
import com.mike_caron.megacorp.block.TEOwnedContainerBase;
import com.mike_caron.megacorp.impl.RewardManager;
import com.mike_caron.megacorp.integrations.gamestages.GameStagesCompatability;
import com.mike_caron.megacorp.reward.BaseReward;
import com.mike_caron.megacorp.util.DataUtils;
import com.mike_caron.megacorp.util.LastResortUtils;
import com.mike_caron.megacorp.util.StringUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
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
        return 100;
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

        String tFluid = null;
        if(te.fluidTank.getFluid() != null)
            tFluid = te.fluidTank.getFluid().getFluid().getName();

        if(!StringUtil.areEqual(fluid, tFluid))
        {
            fluid = tFluid;
            changed = true;
        }

        if(fluidAmount != te.fluidTank.getFluidAmount())
        {
            fluidAmount = te.fluidTank.getFluidAmount();
            changed = true;
        }

        if(fluidCapacity != te.fluidTank.getCapacity())
        {
            fluidCapacity = te.fluidTank.getCapacity();
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

        fluidAmount = tag.getInteger("fluidAmount");
        fluidCapacity = tag.getInteger("fluidCapacity");
        if(tag.hasKey("fluid"))
        {
            fluid = tag.getString("fluid");
        }
        else
        {
            fluid = null;
        }
    }

    @Override
    protected void onWriteNBT(NBTTagCompound tag)
    {
        super.onWriteNBT(tag);

        TileEntityCapitalInvestor te = getTE();

        ICorporationManager manager = CorporationManager.getInstance(te.getWorld());

        if(te.getOwner() != null && manager.ownerHasCorporation(te.getOwner()))
        {
            ICorporation corp = manager.getCorporationForOwner(te.getOwner());

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

        if(fluid != null)
        {
            tag.setString("fluid", fluid);
        }
        tag.setInteger("fluidAmount", fluidAmount);
        tag.setInteger("fluidCapacity", fluidCapacity);

    }

    private void buildRewardList(ICorporation corp)
    {
        rewardList = new ArrayList<>();

        EntityPlayer ownerPlayer = LastResortUtils.getPlayer(owner);

        for(IReward reward : RewardManager.INSTANCE.getRewards())
        {
            if(!GameStagesCompatability.hasStagesUnlocked(ownerPlayer, reward.getGameStages()))
                continue;

            RewardData rd = new RewardData();
            rd.id = reward.getId();
            rd.currentRank = corp.getRankInReward(reward.getId());
            if(rd.currentRank >= reward.numRanks())
                continue;

            rd.nextRank = rd.currentRank + 1;
            rd.nextRankCost = reward.costForRank(rd.nextRank);
            rd.nextRankVariables = DataUtils.box(reward.getValuesForRank(rd.nextRank));
            rd.currencyType = reward.getCurrency();
            rd.available = reward.getCurrency().name().toLowerCase().equals(fluid) && rd.nextRankCost <= fluidAmount;

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
        public static final int DATA_SIZE = 3;
        public String id;
        public int currentRank;
        public int nextRank;
        public int nextRankCost;
        public Float[] nextRankVariables;
        public BaseReward.CurrencyType currencyType;
        public boolean available;

        public NBTTagCompound serialize()
        {
            NBTTagCompound ret = new NBTTagCompound();

            int[] data = new int[DATA_SIZE];
            data[0] = currentRank | (nextRank << 16);
            data[1] = nextRankCost;
            data[2] = currencyType.ordinal() | ((available ? 1 : 0) << 16);

            NBTTagList vars = new NBTTagList();
            for (Float nextRankVariable : nextRankVariables)
            {
                vars.appendTag(new NBTTagFloat(nextRankVariable));
            }
            //System.arraycopy(DataUtils.unbox(nextRankVariables), 0, data, DATA_SIZE, nextRankVariables.length);

            ret.setString("id", id);
            ret.setIntArray("d", data);
            ret.setTag("f", vars);

            return ret;
        }

        public static RewardData fromNbt(NBTTagCompound tag)
        {
            RewardData ret = new RewardData();
            int[] data = tag.getIntArray("d");

            ret.id = tag.getString("id");
            ret.currentRank = data[0] & 0xffff;
            ret.nextRank = data[0] >> 16;
            ret.nextRankCost = data[1];
            ret.currencyType = BaseReward.CurrencyType.values()[data[2] & 0xffff];
            ret.available = (data[2] >> 16) != 0;

            NBTTagList vars = tag.getTagList("f", Constants.NBT.TAG_FLOAT);
            ret.nextRankVariables = new Float[vars.tagCount()];
            for(int i = 0; i < vars.tagCount(); i++)
            {
                ret.nextRankVariables[i] = vars.getFloatAt(i);
            }

            return ret;
        }
    }
}
