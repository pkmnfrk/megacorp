package com.mike_caron.megacorp.integrations;

import com.mike_caron.megacorp.integrations.gamestages.GameStagesCompatability;
import net.minecraftforge.fml.common.Loader;

public class MainCompatHandler
{
    public static void registerAllPreInit()
    {
        com.mike_caron.mikesmodslib.integrations.MainCompatHandler.registerAllPreInit();

        registerTConstruct();
        //registerEnderio();
        registerGameStages();
    }

    public static void registerAllInit()
    {
        com.mike_caron.mikesmodslib.integrations.MainCompatHandler.registerAllInit();

        registerThermalExpansion();
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

    public static void registerGameStages()
    {
        if(Loader.isModLoaded("gamestages"))
        {
            GameStagesCompatability.register();
        }
    }
}
