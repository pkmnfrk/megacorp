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
    public static final FluidBase MONEY = (FluidBase) new FluidBase(
            "money",
            new ResourceLocation(MegaCorpMod.modId, "blocks/liquid/money_still"),
            new ResourceLocation(MegaCorpMod.modId, "blocks/liquid/money_flow")
    )
            .setMaterial(ModMaterials.MONEY)
            .setDensity(1500)
            .setGaseous(false)
            .setViscosity(1000)
            .setTemperature(400)
            .setLuminosity(15)
            .setRarity(EnumRarity.RARE)
            .setColor(Color.GREEN)
    ;

    public static void register()
    {
        try
        {
            for (Field field : ModFluids.class.getDeclaredFields())
            {
                if (Modifier.isStatic(field.getModifiers()) && FluidBase.class.isAssignableFrom(field.getType()))
                {
                    FluidBase item = (FluidBase) field.get(null);

                    if(! FluidRegistry.isFluidRegistered(item.getName()))
                    {
                        MegaCorpMod.logger.info("Registering fluid {}", item.getName());
                        FluidRegistry.registerFluid(item);
                    }
                }
            }
        }
        catch(IllegalAccessException ex)
        {
            throw new RuntimeException("Unable to reflect upon myself??");
        }
        //soulboundTalisman.initModel();
    }
}
