package com.mike_caron.megacorp.fluid;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.ModMaterials;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Mod.EventBusSubscriber
public class ModFluids
{
    public static FluidMoney MONEY;

    public static void register()
    {
        MONEY = new FluidMoney();
    }
}
