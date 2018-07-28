package com.mike_caron.megacorp.integrations;

import net.minecraftforge.fml.common.Loader;

public class MainCompatHandler
{
    public static void registerAll()
    {
        registerTOP();
        registerWaila();
        registerTConstruct();
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
}
