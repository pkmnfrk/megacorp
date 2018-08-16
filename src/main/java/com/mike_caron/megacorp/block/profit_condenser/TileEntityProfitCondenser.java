package com.mike_caron.megacorp.block.profit_condenser;

import com.mike_caron.megacorp.block.TileEntityBase;
import com.mike_caron.megacorp.fluid.ModFluids;
import com.mike_caron.megacorp.recipes.PCRecipe;
import com.mike_caron.megacorp.recipes.PCRecipeManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//@GameRegistry.ObjectHolder(MegaCorpMod.modId)
public class TileEntityProfitCondenser
    extends TileEntityBase
        implements ITickable
{
    private PCRecipe currentRecipe = null;
    private int ticksLeft = 0;
    private boolean stalled = false;

    @Nonnull
    public final FluidTank inputFluidTank = new FluidTank(10000)
    {
        @Override
        public boolean canFillFluidType(FluidStack fluid)
        {
            if(fluid == null || fluid.getFluid() != ModFluids.MONEY)
                return false;

            return super.canFillFluidType(fluid);
        }

        @Override
        protected void onContentsChanged()
        {
            super.onContentsChanged();
            TileEntityProfitCondenser.this.markDirty();
        }
    };

    @Nonnull
    public final FluidTank outputFluidTank = new FluidTank(10000)
    {
        @Override
        protected void onContentsChanged()
        {
            super.onContentsChanged();
            TileEntityProfitCondenser.this.markDirty();
        }
    };

    public TileEntityProfitCondenser()
    {
        inputFluidTank.setCanDrain(false);
        inputFluidTank.setCanFill(true);

        outputFluidTank.setCanDrain(true);
        outputFluidTank.setCanFill(false);
    }


    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        if(compound.hasKey("InputTank"))
        {
            inputFluidTank.readFromNBT(compound.getCompoundTag("InputTank"));
        }

        if(compound.hasKey("OutputTank"))
        {
            inputFluidTank.readFromNBT(compound.getCompoundTag("OutputTank"));
        }


        if(compound.hasKey("CurrentRecipe"))
        {
            String name = compound.getString("CurrentRecipe");
            ticksLeft = compound.getInteger("TicksLeft");

            PCRecipe recipe = PCRecipeManager.getRecipeWithName(name);
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

        ret.setTag("InputTank", inputFluidTank.writeToNBT(new NBTTagCompound()));
        ret.setTag("OutputTank", outputFluidTank.writeToNBT(new NBTTagCompound()));

        if(currentRecipe != null)
        {
            ret.setString("CurrentRecipe", currentRecipe.name);
            ret.setInteger("TicksLeft", ticksLeft);
        }

        return ret;
    }

    private EnumFacing getInputSide()
    {
        IBlockState state = world.getBlockState(pos);

        EnumFacing forward = state.getValue(BlockProfitCondenser.FACING);
        EnumFacing ret = forward.rotateY();

        if(state.getValue(BlockProfitCondenser.SWAPPED))
        {
            ret = ret.getOpposite();
        }

        return ret;
    }

    private EnumFacing getOutputSide()
    {
        return getInputSide().getOpposite();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            if(facing == getInputSide() || facing == getOutputSide())
                return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            if(facing == getInputSide())
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(inputFluidTank);
            if(facing == getOutputSide())
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(outputFluidTank);
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
                if(currentRecipe.output.amount != outputFluidTank.fillInternal(currentRecipe.output, false))
                {
                    //?? this should be impossible
                    ticksLeft = 1;
                    stalled = true;
                }
                else
                {
                    outputFluidTank.fillInternal(currentRecipe.output, true);
                    currentRecipe = null;

                    checkForRecipe();
                }
            }
            this.markDirty();
        }
    }

    public float getProgress()
    {
        if(currentRecipe == null) return 0f;
        if(stalled) return 1f;

        return 1f - (((float)ticksLeft) / currentRecipe.ticks);
    }

    public PCRecipe getCurrentRecipe()
    {
        return currentRecipe;
    }

    private boolean recursion = false;
    private void checkForRecipe()
    {
        if(recursion) return;

        recursion = true;
        //if we're in the middle of a recipe, don't do anything
        if(currentRecipe == null)
        {

            FluidStack stack1 = inputFluidTank.getFluid();

            //look for a recipe with these ingredients
            PCRecipe recipe = PCRecipeManager.getRecipeForIngredients(stack1);
            if(recipe != null)
            {
                //try removing the correct number of ingredients.
                FluidStack drained = inputFluidTank.drainInternal(recipe.input, false);
                if(drained != null && drained.amount == recipe.input.amount)
                {
                    //simulation succeeded, do for real
                    inputFluidTank.drainInternal(recipe.input, true);

                    //start working on this recipe
                    currentRecipe = recipe;
                    ticksLeft = recipe.ticks;

                    this.markDirty();
                }
            }
        }
        recursion = false;
    }
}
