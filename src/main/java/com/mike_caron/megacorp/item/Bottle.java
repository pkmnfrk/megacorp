package com.mike_caron.megacorp.item;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.client.models.ModelBottle;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.NumberFormat;

public class Bottle
    extends ItemBase
{
    @Override
    public void initModel()
    {
        ModelLoader.setCustomMeshDefinition(ModItems.bottle, stack -> ModelBottle.LOCATION);
    }

    public Bottle()
    {
        super();

        setRegistryName("bottle");
        setUnlocalizedName("megacorp:bottle");
        setMaxStackSize(1);

    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new Handler(stack);
    }

    public static FluidStack getFluid(ItemStack stack)
    {
        if(stack.isEmpty()) return null;
        if(stack.getItem() != ModItems.bottle) return null;

        NBTTagCompound tag = stack.getTagCompound();

        if(tag == null) return null;

        Fluid fluid = FluidRegistry.getFluid(tag.getString("Fluid"));
        if(fluid == null) return null;

        int amount = tag.getInteger("Amount");
        if(amount == 0) return null;

        return new FluidStack(fluid, amount);
    }

    @Override
    public boolean getShareTag()
    {
        return true;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        FluidStack fluid = Bottle.getFluid(stack);

        if(fluid == null || fluid.amount == 0)
        {
            return new TextComponentTranslation("item.megacorp:bottle.empty").getUnformattedText();
        }

        String cap = NumberFormat.getIntegerInstance().format(fluid.amount);

        return new TextComponentTranslation("item.megacorp:bottle.filled", fluid.getLocalizedName(), cap).getUnformattedText();
    }

    @Nullable
    @Override
    public NBTTagCompound getNBTShareTag(ItemStack stack)
    {
        return stack.getTagCompound();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if(tab != MegaCorpMod.creativeTab) return;

        for(Fluid fluid : FluidRegistry.getBucketFluids())
        {
            items.add(Bottle.with(new FluidStack(fluid, 1000)));
        }

    }

    public static ItemStack with(FluidStack stack)
    {
        if(stack == null || stack.amount == 0)
            return ItemStack.EMPTY;

        NBTTagCompound compound = new NBTTagCompound();

        compound.setString("Fluid", stack.getFluid().getName());
        compound.setInteger("Amount", stack.amount);

        ItemStack ret = new ItemStack(ModItems.bottle, 1);
        ret.setTagCompound(compound);

        return ret;
    }

    public static class Handler
        implements IFluidHandlerItem, ICapabilityProvider
    {
        private ItemStack container;
        private FluidStack stored = null;

        public Handler(@Nonnull ItemStack container)
        {
            this.container = container;

        }

        @Nonnull
        @Override
        public ItemStack getContainer()
        {
            FluidStack fluid = currentFluid();

            return Bottle.with(fluid);
        }

        @Override
        public IFluidTankProperties[] getTankProperties()
        {
            FluidStack fluid = currentFluid();
            return new IFluidTankProperties[]{
                new FluidTankProperties(fluid, fluid != null ? fluid.amount : 0, false, true)
            };
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack fluidStack, boolean doit)
        {
            if(fluidStack == null || fluidStack.getFluid() == null) return null;

            FluidStack current = currentFluid();

            if(current == null || current.amount == 0) return null;

            if(current.getFluid() != fluidStack.getFluid()) return null;

            int amt = Math.min(current.amount, fluidStack.amount);
            if(amt <= 0) return null;

            if(doit)
            {
                stored = new FluidStack(current.getFluid(), current.amount - amt);
            }

            return new FluidStack(current.getFluid(), amt);
        }

        @Nullable
        @Override
        public FluidStack drain(int i, boolean b)
        {
            FluidStack current = currentFluid();

            if(current == null) return null;

            return drain(new FluidStack(current.getFluid(), i), b);
        }

        @Override
        public int fill(FluidStack resource, boolean doFill)
        {
            return 0;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing enumFacing)
        {
            if(capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
                return true;

            return false;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing enumFacing)
        {
            if(capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
                return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(this);

            return null;
        }

        private FluidStack currentFluid()
        {
            if(stored != null)
                return stored;

            return getFluid(container);
        }
    }
}
