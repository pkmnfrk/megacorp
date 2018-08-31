package com.mike_caron.megacorp.block.capital_investor;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.TileEntityOwnedBase;
import com.mike_caron.megacorp.fluid.ModFluids;
import com.mike_caron.megacorp.impl.Corporation;
import com.mike_caron.megacorp.impl.CorporationManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

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
            if(fluid.getFluid() != ModFluids.MONEY)
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
            //IReward reward = RewardManager.INSTANCE.getRewardWithId(extraData);
            Corporation corp = (Corporation)CorporationManager.get(world).getCorporationForOwner(owner);

            Optional<Integer> cost = Optional.empty();
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
                int c = cost.get();

                FluidStack drained = fluidTank.drainInternal(c, false);

                if(drained != null && drained.amount == c)
                {
                    drained = fluidTank.drainInternal(drained, true);

                    corp.purchaseReward(extraData);
                }
            }

        }
    }
}
