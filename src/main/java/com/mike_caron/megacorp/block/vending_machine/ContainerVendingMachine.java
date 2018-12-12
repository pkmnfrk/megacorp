package com.mike_caron.megacorp.block.vending_machine;

import com.mike_caron.megacorp.api.CorporationManager;
import com.mike_caron.megacorp.api.ICorporationManager;
import com.mike_caron.megacorp.impl.VendingItem;
import com.mike_caron.megacorp.impl.VendingManager;
import com.mike_caron.megacorp.integrations.gamestages.GameStagesCompatability;
import com.mike_caron.megacorp.reward.BaseReward;
import com.mike_caron.mikesmodslib.util.StringUtil;
import com.mike_caron.mikesmodslib.block.TEContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class ContainerVendingMachine
    extends TEContainerBase
{
    public static final int GUI_BUY_REWARD = 1;

    public int fluidAmount = 0;
    public int fluidCapacity = 1;
    public String fluid = null;

    public int rewardsSerial = 0;
    public int lastRewardsSerial = -1;

    public List<VendingData> rewardList = null;

    EntityPlayer player;

    public ContainerVendingMachine(IInventory playerInventory, TileEntityVendingMachine te, EntityPlayer player)
    {
        super(playerInventory, te);

        this.player = player;

        init();
    }

    private TileEntityVendingMachine getTE()
    {
        return (TileEntityVendingMachine) this.te;
    }

    @Override
    protected int playerInventoryY()
    {
        return 154;
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

        TileEntityVendingMachine te = getTE();

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

                VendingData data = VendingData.fromNbt(reward);

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

        TileEntityVendingMachine te = getTE();

        ICorporationManager manager = CorporationManager.getInstance(te.getWorld());

        NBTTagList rewards = new NBTTagList();

        if (rewardList == null)
        {
            buildRewardList(player);
        }

        for (VendingData reward : rewardList)
        {
            rewards.appendTag(reward.serialize());
        }

        tag.setTag("rewards", rewards);


        if(fluid != null)
        {
            tag.setString("fluid", fluid);
        }
        tag.setInteger("fluidAmount", fluidAmount);
        tag.setInteger("fluidCapacity", fluidCapacity);

    }

    private void buildRewardList(EntityPlayer player)
    {
        rewardList = new ArrayList<>();


        for(VendingItem reward : VendingManager.INSTANCE.getItems())
        {
            if(!GameStagesCompatability.hasStagesUnlocked(player, reward.stagesRequired))
                continue;

            VendingData rd = new VendingData();
            rd.itemStack = reward.itemStack;
            rd.cost = reward.cost;
            rd.currencyType = reward.currency;
            rd.id = reward.id;

            rd.available = reward.currency.name().toLowerCase().equals(fluid) && rd.cost <= fluidAmount;

            rewardList.add(rd);
        }
    }

    @Override
    public int getId()
    {
        return 5;
    }

    public static class VendingData
    {
        public static final int DATA_SIZE = 3;
        public ItemStack itemStack;
        public int cost;
        public BaseReward.CurrencyType currencyType;
        public boolean available;
        public String id;

        public NBTTagCompound serialize()
        {
            NBTTagCompound ret = new NBTTagCompound();

            ret.setTag("item", itemStack.serializeNBT());
            ret.setInteger("cost", cost);
            ret.setString("cost_type", currencyType.name());
            ret.setBoolean("available", available);
            ret.setString("id", id);

            return ret;
        }

        public static VendingData fromNbt(NBTTagCompound tag)
        {
            VendingData ret = new VendingData();

            ret.itemStack = new ItemStack(tag.getCompoundTag("item"));
            ret.cost = tag.getInteger("cost");
            ret.currencyType = BaseReward.CurrencyType.valueOf(tag.getString("cost_type"));
            ret.available = tag.getBoolean("available");
            ret.id = tag.getString("id");

            return ret;
        }
    }
}
