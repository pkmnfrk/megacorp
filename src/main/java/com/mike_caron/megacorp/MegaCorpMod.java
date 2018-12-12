package com.mike_caron.megacorp;

import com.mike_caron.megacorp.command.MegaCorpCommand;
import com.mike_caron.megacorp.integrations.MainCompatHandler;
import com.mike_caron.megacorp.proxy.IModProxy;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod(
        modid = MegaCorpMod.modId,
        name = MegaCorpMod.name,
        version = MegaCorpMod.version,
        acceptedMinecraftVersions = "[1.12.2]"
        ,dependencies = "" +
                        ";after:tconstruct" +
                        ";after:thermalexpansion" +
                        ";after:theoneprobe" +
                        ";after:waila" +
                        ";after:enderio" +
                        ";after:gamestages"
)
@Mod.EventBusSubscriber
public class MegaCorpMod
{
    public static final String modId = "megacorp";
    public static final String name = "MegaCorp";
    public static final String version = "0.4.0";

    public static final Logger logger = LogManager.getLogger(modId);

    public static final CreativeTab creativeTab = new CreativeTab();

    @SuppressWarnings("unused")
    @Mod.Instance(modId)
    public static MegaCorpMod instance;

    @SidedProxy(
            serverSide = "com.mike_caron.megacorp.proxy.CommonProxy",
            clientSide = "com.mike_caron.megacorp.proxy.ClientProxy"
    )
    public static IModProxy proxy;

    static {
        FluidRegistry.enableUniversalBucket();
    }

    @Mod.EventHandler
    public  void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);

        MainCompatHandler.registerAllPreInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);

        MainCompatHandler.registerAllInit();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent evt)
    {
        evt.registerServerCommand(new MegaCorpCommand());
    }

    @Mod.EventHandler
    public void serverUnload(FMLServerStoppingEvent evt)
    {
    }

}
