package com.mike_caron.megacorp.fluid;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.mikesmodslib.fluid.FluidBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Stream;

@Mod.EventBusSubscriber
public class ModFluids
{
    public static Fluid MONEY;
    public static Fluid DENSE_MONEY;

    public static void register()
    {
        MONEY = new FluidBase("money",
            new ResourceLocation(MegaCorpMod.modId, "liquid/money_still"),
            new ResourceLocation(MegaCorpMod.modId, "liquid/money_flow")
        )
                .setDensity(1500)
                .setViscosity(1000)
                .setTemperature(400)
                .setLuminosity(15)
                .setRarity(EnumRarity.RARE)
                .setColor(new Color(0, 128, 0));
        DENSE_MONEY = new FluidBase("dense_money",
            new ResourceLocation(MegaCorpMod.modId, "liquid/dense_money_still"),
            new ResourceLocation(MegaCorpMod.modId, "liquid/dense_money_flow"))
                .setDensity(1500000)
                .setViscosity(25000)
                .setTemperature(1000)
                .setLuminosity(15)
                .setRarity(EnumRarity.EPIC)
                .setColor(new Color(0, 64, 0));
    }

    public static Stream<Fluid> getAllFluids()
    {
        return Arrays.stream(ModFluids.class.getDeclaredFields()).filter(f -> Modifier.isStatic(f.getModifiers()) && Fluid.class.isAssignableFrom(f.getType())).map(f -> {
            try
            {
                return (Fluid) f.get(null);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException("Unable to reflect upon myself??");
            }
        });
    }
}
