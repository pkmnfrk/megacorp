package com.mike_caron.megacorp.block.sbs;

import com.mike_caron.megacorp.block.TileEntityBase;
import com.mike_caron.megacorp.recipes.SBSRecipe;
import com.mike_caron.megacorp.recipes.SBSRecipeManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//@GameRegistry.ObjectHolder(MegaCorpMod.modId)
public class TileEntitySBS
    extends TileEntityBase
        implements ITickable
{

    private SBSRecipe currentRecipe = null;
    private int ticksLeft = 0;
    private boolean stalled = false;

    public TileEntitySBS()
    {
        fluidTank.setCanDrain(true);
        fluidTank.setCanFill(false);

    }
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
        public void setStackInSlot(int slot, @Nonnull ItemStack stack)
        {
            super.setStackInSlot(slot, stack);
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            //checkForRecipe();

            super.onContentsChanged(slot);

            TileEntitySBS.this.markDirty();
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            ItemStack currentStack = getStackInSlot(slot);

            if(!currentStack.isItemEqual(stack))
            {
                ItemStack otherStack = getOtherSlot(slot);

                if(!SBSRecipeManager.hasRecipeWithIngredient(stack, otherStack))
                    return stack;
            }

            return super.insertItem(slot, stack, simulate);
        }

        private ItemStack getOtherSlot(int slot)
        {
            if(slot == 0)
                return getStackInSlot(1);
            if(slot == 1)
                return getStackInSlot(0);
            throw new RuntimeException("What? What slot is " + slot);
        }
    };


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

        if(compound.hasKey("CurrentRecipe"))
        {
            String name = compound.getString("CurrentRecipe");
            ticksLeft = compound.getInteger("TicksLeft");

            SBSRecipe recipe = SBSRecipeManager.getRecipeWithName(name);
            if(recipe != null)
            {
                currentRecipe = recipe;
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound ret = super.writeToNBT(compound);

        ret.setTag("tank", fluidTank.writeToNBT(new NBTTagCompound()));
        ret.setTag("inventory", reagents.serializeNBT());
        if(currentRecipe != null)
        {
            ret.setString("CurrentRecipe", currentRecipe.name);
            ret.setInteger("TicksLeft", ticksLeft);
        }

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

        stalled = false;

        if(currentRecipe != null)
        {
            ticksLeft -= 1;

            if(ticksLeft == 0)
            {
                if(currentRecipe.output.amount != fluidTank.fillInternal(currentRecipe.output, false))
                {
                    //?? this should be impossible
                    ticksLeft = 1;
                    stalled = true;
                }
                else
                {
                    fluidTank.fillInternal(currentRecipe.output, true);
                    currentRecipe = null;

                    checkForRecipe();
                }
            }
            this.markDirty();
        }

        if(currentRecipe == null)
        {
            checkForRecipe();
        }
    }

    private boolean recursion = false;
    private void checkForRecipe()
    {
        if(recursion) return;

        recursion = true;
        //if we're in the middle of a recipe, don't do anything
        if(currentRecipe == null)
        {
            int slot1 = 0, slot2 = 1;
            ItemStack stack1 = reagents.getStackInSlot(slot1);
            ItemStack stack2 = reagents.getStackInSlot(slot2);

            //look for a recipe with these ingredients
            SBSRecipe recipe = SBSRecipeManager.getRecipeForIngredients(stack1, stack2);
            if(recipe != null)
            {
                //if the ingredients are backwards, swap the slots
                if(recipe.input1.isItemEqual(stack2))
                {
                    slot1 = 1;
                    slot2 = 0;
                }

                //try removing the correct number of ingredients.
                if(reagents.extractItem(slot1, recipe.input1.getCount(), true).getCount() == recipe.input1.getCount()
                    && reagents.extractItem(slot2, recipe.input2.getCount(), true).getCount() == recipe.input2.getCount())
                {
                    //simulation succeeded, do for real
                    reagents.extractItem(slot1, recipe.input1.getCount(), false);
                    reagents.extractItem(slot2, recipe.input2.getCount(), false);

                    //start working on this recipe
                    currentRecipe = recipe;
                    ticksLeft = recipe.ticks;

                    this.markDirty();
                }
            }
        }
        recursion = false;
    }

    public float getProgress()
    {
        if(currentRecipe == null) return 0f;
        if(stalled) return 1f;

        return 1f - (((float)ticksLeft) / currentRecipe.ticks);
    }

    public SBSRecipe getCurrentRecipe()
    {
        return currentRecipe;
    }
}
