package com.mike_caron.megacorp.fluid;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.ModMaterials;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Mod.EventBusSubscriber
public class ModFluids
{
    public static Fluid MONEY;
    public static Fluid DENSE_MONEY;

    public static void register()
    {
        MONEY = new FluidBase("money")
                .setDensity(1500)
                .setViscosity(1000)
                .setTemperature(400)
                .setLuminosity(15)
                .setRarity(EnumRarity.RARE)
                .setColor(new Color(0, 128, 0));
        DENSE_MONEY = new FluidBase("dense_money")
                .setDensity(1500000)
                .setViscosity(25000)
                .setTemperature(1000)
                .setLuminosity(15)
                .setRarity(EnumRarity.EPIC)
                .setColor(new Color(0, 64, 0));
    }
}
