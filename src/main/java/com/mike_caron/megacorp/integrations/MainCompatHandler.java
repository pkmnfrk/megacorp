package com.mike_caron.megacorp.integrations;

import com.mike_caron.megacorp.MegaCorpMod;
import net.minecraftforge.fml.common.Loader;

public class MainCompatHandler
{
    public static void registerAllPreInit()
    {
        registerTOP();
        registerWaila();
        registerTConstruct();
        registerEnderio();
    }

    public static void registerAllInit()
    {
        registerThermalExpansion();
    }

    public static void registerTOP()
    {
        if(Loader.isModLoaded("theoneprobe"))
        {
            TOPCompatibility.register();
        }
    }

    public static void registerWaila()
    {
        if(Loader.isModLoaded("waila"))
        {
            WailaCompatibility.register();
        }
    }

    public static void registerTConstruct()
    {
        if(Loader.isModLoaded("tconstruct"))
        {
            TConCompatability.register();
        }
    }

    public static void registerThermalExpansion()
    {
        if(Loader.isModLoaded("thermalexpansion"))
        {
            ThermalExpansionCompatability.register();
        }
    }

    public static void registerEnderio()
    {
        if(Loader.isModLoaded("enderio"))
        {
            EnderioCompatability.register();
        }
    }
}
