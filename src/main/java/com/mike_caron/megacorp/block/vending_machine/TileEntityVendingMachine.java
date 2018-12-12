package com.mike_caron.megacorp.block.vending_machine;

import com.mike_caron.megacorp.fluid.ModFluids;
import com.mike_caron.megacorp.impl.VendingItem;
import com.mike_caron.megacorp.impl.VendingManager;
import com.mike_caron.megacorp.integrations.gamestages.GameStagesCompatability;
import com.mike_caron.megacorp.reward.BaseReward;
import com.mike_caron.mikesmodslib.util.ItemUtils;
import com.mike_caron.mikesmodslib.block.TileEntityBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityVendingMachine
    extends TileEntityBase
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

    private String vendingMachineKind = null;

    public TileEntityVendingMachine()
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
        if(button == ContainerVendingMachine.GUI_BUY_REWARD)
        {
            VendingItem vending = VendingManager.INSTANCE.getItem(extraData);

            if(vending == null)
                return;

            if(!validateCurrency(vending.currency))
                return;

            if(!validateGameStages(player, vending.stagesRequired))
                return;

            FluidStack drained = fluidTank.drainInternal(vending.cost, false);

            if(drained == null || drained.amount != vending.cost)
                return;

            drained = fluidTank.drainInternal(drained, true);

            ItemUtils.giveToPlayerOrDrop(vending.itemStack, player);

            markDirty();
        }
    }

    private boolean validateGameStages(EntityPlayer player, @Nullable String[][] stages)
    {
        if(stages == null)
            return true;

        return GameStagesCompatability.hasStagesUnlocked(player, stages);
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
