package com.mike_caron.megacorp.fluid;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.ModMaterials;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;

import java.awt.*;

public class FluidMoney extends FluidBase
{
    public FluidMoney()
    {
        super("money",
                new ResourceLocation(MegaCorpMod.modId, "liquid/money_still"),
                new ResourceLocation(MegaCorpMod.modId, "liquid/money_flow"));

        this
            .setMaterial(Material.WATER)
            .setDensity(1500)
            .setGaseous(false)
            .setViscosity(1000)
            .setTemperature(400)
            .setLuminosity(15)
            .setRarity(EnumRarity.RARE)
            .setColor(Color.GREEN);

        FluidRegistry.registerFluid(this);
        FluidRegistry.addBucketForFluid(this);
    }
}
