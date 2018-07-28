package com.mike_caron.megacorp.integrations;

import com.mike_caron.megacorp.fluid.ModFluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import java.util.ArrayList;
import java.util.List;

public class TConCompatability
{
    private static boolean registered = false;

    public static void register()
    {
        if(registered) return;

        registered = true;

        registerRecipes();
    }

    private static void registerRecipes()
    {
        registerFluid(ModFluids.MONEY, "Money");
        registerFluid(ModFluids.DENSE_MONEY, "DenseMoney");

        NBTTagList tagList = new NBTTagList();

        NBTTagCompound fluid = new NBTTagCompound();
        fluid.setString("FluidName", "money");
        fluid.setInteger("Amount", 12);
        tagList.appendTag(fluid);

        fluid = new NBTTagCompound();
        fluid.setString("FluidName", "emerald");
        fluid.setInteger("Amount", 666);
        tagList.appendTag(fluid);

        fluid = new NBTTagCompound();
        fluid.setString("FluidName", "gold");
        fluid.setInteger("Amount", 144 * 9);
        tagList.appendTag(fluid);

        NBTTagCompound message = new NBTTagCompound();
        message.setTag("alloy", tagList);
        FMLInterModComms.sendMessage("tconstruct", "alloy", message);
    }

    private static void registerFluid(Fluid fluid, String oreSuffix)
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("fluid", fluid.getName());
        tag.setString("ore", oreSuffix);
        tag.setBoolean("toolforge", true);

        FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);
    }
}
