package com.mike_caron.megacorp.block.capital_investor;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.api.IReward;
import com.mike_caron.megacorp.block.TileEntityOwnedBase;
import com.mike_caron.megacorp.fluid.ModFluids;
import com.mike_caron.megacorp.impl.Corporation;
import com.mike_caron.megacorp.impl.RewardManager;
import com.mike_caron.megacorp.integrations.gamestages.GameStagesCompatability;
import com.mike_caron.megacorp.reward.BaseReward;
import com.mike_caron.megacorp.util.LastResortUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class TileEntityCapitalInvestor
    extends TileEntityOwnedBase
        //implements ITickable
{
    public final FluidTank fluidTank = new FluidTank(50000)
    {
        @Override
        public boolean canFillFluidType(FluidStack fluid)
        {
            if(fluid == null || (fluid.getFluid() != ModFluids.MONEY && fluid.getFluid() != ModFluids.DENSE_MONEY))
                return false;

            return super.canFillFluidType(fluid);
        }

        @Override
        protected void onContentsChanged()
        {
            super.onContentsChanged();
            markDirty();
        }
    };

    public TileEntityCapitalInvestor()
    {
        fluidTank.setCanDrain(true);
        fluidTank.setCanFill(true);

    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        if(compound.hasKey("tank"))
        {
            fluidTank.readFromNBT(compound.getCompoundTag("tank"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound ret = super.writeToNBT(compound);

        ret.setTag("tank", fluidTank.writeToNBT(new NBTTagCompound()));

        return ret;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != null)
        {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != null)
        {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidTank);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void handleGuiButton(EntityPlayerMP player, int button, String extraData)
    {
        if(button == ContainerCapitalInvestor.GUI_BUY_REWARD)
        {
            IReward reward = RewardManager.INSTANCE.getRewardWithId(extraData);
            Corporation corp = getCorporation();

            if(corp == null) return;

            EntityPlayer ownerPlayer = LastResortUtils.getPlayer(owner);

            if(!GameStagesCompatability.hasStagesUnlocked(ownerPlayer, reward.getGameStages()))
                return;

            Optional<Pair<Integer, BaseReward.CurrencyType>> cost = Optional.empty();
            try
            {
                cost = corp.getCostForReward(extraData);
            }
            catch(IllegalArgumentException ex)
            {
                MegaCorpMod.logger.error("Tried to inquire about invalid reward " + extraData);
            }

            if(cost.isPresent())
            {
                int c = cost.get().getLeft();
                BaseReward.CurrencyType m = cost.get().getRight();

                if(!validateCurrency(m)) return;

                FluidStack drained = fluidTank.drainInternal(c, false);

                if(drained != null && drained.amount == c)
                {
                    drained = fluidTank.drainInternal(drained, true);

                    corp.purchaseReward(extraData);
                }
            }

        }
    }

    private boolean validateCurrency(BaseReward.CurrencyType currency)
    {
        if(fluidTank.getFluid() == null) return false;

        switch (currency)
        {
            case MONEY: return fluidTank.getFluid().getFluid() == ModFluids.MONEY;
            case DENSE_MONEY: return fluidTank.getFluid().getFluid() == ModFluids.DENSE_MONEY;
        }

        return false;
    }
}
