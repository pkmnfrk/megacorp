package com.mike_caron.megacorp.integrations;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.ModBlocks;
import com.mike_caron.megacorp.fluid.ModFluids;
import com.mike_caron.megacorp.item.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import cofh.api.util.ThermalExpansionHelper;

public class ThermalExpansionCompatability
{
    private static boolean registered = false;

    public static void register()
    {
        if(registered) return;

        registered = true;

        registerRecipes();

        MegaCorpMod.logger.info("Thermal Expansion compatibility locked and loaded");
    }

    private static void registerRecipes()
    {
        /*
        ThermalExpansionHelper.addSmelterRecipe(
                20000,
                new ItemStack(Item.getByNameOrId("emerald"), 12),
                new ItemStack(Item.getByNameOrId("gold_block"), 12),
                new ItemStack(ModItems.ingotMoney, 1));

        ThermalExpansionHelper.addCrucibleRecipe(10000,
                new ItemStack(ModItems.ingotMoney),
                new FluidStack(ModFluids.MONEY, 144));

        ThermalExpansionHelper.addCrucibleRecipe(80000,
                new ItemStack(ModBlocks.money_block),
                new FluidStack(ModFluids.MONEY, 144 * 9));

        ThermalExpansionHelper.addCrucibleRecipe(100000,
                new ItemStack(ModItems.ingotDenseMoney),
                new FluidStack(ModFluids.DENSE_MONEY, 144));

        ThermalExpansionHelper.addCrucibleRecipe(800000,
                new ItemStack(ModBlocks.dense_money_block),
                new FluidStack(ModFluids.DENSE_MONEY, 144 * 9));
        */

        ThermalExpansionHelper.addRefineryRecipe(100000,
                new FluidStack(ModFluids.MONEY, 1000),
                new FluidStack(ModFluids.DENSE_MONEY, 1),
                ItemStack.EMPTY);

    }


}
