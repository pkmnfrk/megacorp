package com.mike_caron.megacorp.block.sbs;

import com.mike_caron.megacorp.block.TileEntityBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//@GameRegistry.ObjectHolder(MegaCorpMod.modId)
public class TileEntitySBS
    extends TileEntityBase
        implements ITickable
{

    @GameRegistry.ObjectHolder("minecraft:gold_block")
    public static Item gold_block;

    @GameRegistry.ObjectHolder("minecraft:emerald")
    public static Item emerald;

    public final FluidTank fluidTank = new FluidTank(10000)
    {
        @Override
        protected void onContentsChanged()
        {
            super.onContentsChanged();
            TileEntitySBS.this.markDirty();
        }
    };

    public final ItemStackHandler reagents = new ItemStackHandler(2)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            super.onContentsChanged(slot);

            TileEntitySBS.this.markDirty();
        }

        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack)
        {
            if(slot == 0)
            {
                if(!stack.isEmpty() && stack.getItem() != gold_block)
                {
                    return;
                }
            }
            else if(slot == 1)
            {
                if(!stack.isEmpty() && stack.getItem() != emerald)
                {
                    return;
                }
            }

            super.setStackInSlot(slot, stack);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            if(slot == 0)
            {
                if(stack.getItem() != gold_block)
                {
                    return stack;
                }
            }
            else if(slot == 1)
            {
                if(stack.getItem() != emerald)
                {
                    return stack;
                }
            }

            return super.insertItem(slot, stack, simulate);
        }
    };

    private int timer = 0;

    public TileEntitySBS()
    {
        fluidTank.setCanDrain(true);
        fluidTank.setCanFill(false);

    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        if(compound.hasKey("tank"))
        {
            fluidTank.readFromNBT(compound.getCompoundTag("tank"));
        }

        if(compound.hasKey("inventory"))
        {
            reagents.deserializeNBT(compound.getCompoundTag("inventory"));
        }

        if(compound.hasKey("timer"))
        {
            timer = compound.getInteger("timer");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound ret = super.writeToNBT(compound);

        ret.setTag("tank", fluidTank.writeToNBT(new NBTTagCompound()));
        ret.setInteger("timer", timer);
        ret.setTag("inventory", reagents.serializeNBT());

        return ret;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != null)
        {
            return true;
        }
        else if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null)
        {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != null)
        {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidTank);
        }
        else if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null)
        {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(reagents);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void update()
    {
        if(world == null || world.isRemote) return;

        /*
        timer += 1;

        if(timer > 8)
        {
            this.fluidTank.fillInternal(new FluidStack(ModFluids.MONEY, 2), true);
            timer = 0;
        }
        */

    }
}
