package com.mike_caron.megacorp;

import com.mike_caron.megacorp.integrations.MainCompatHandler;
import com.mike_caron.megacorp.network.CtoSMessage;
import com.mike_caron.megacorp.network.MessageUpdateGui;
import com.mike_caron.megacorp.proxy.IModProxy;
import com.mike_caron.megacorp.recipes.SBSRecipeManager;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod(
        modid = MegaCorpMod.modId,
        name = MegaCorpMod.name,
        version = MegaCorpMod.version,
        acceptedMinecraftVersions = "[1.12.2]"
        //,dependencies = "required-after:projecte@[1.12-PE1.3.1,)"
        ,dependencies = "after:tconstruct;after:thermalexpansion;after:theoneprobe;after:waila;after:enderio"
)
@Mod.EventBusSubscriber
public class MegaCorpMod
{
    public static final String modId = "megacorp";
    public static final String name = "Mega Corp";
    public static final String version = "0.1.0";

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

    public static SimpleNetworkWrapper networkWrapper;

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
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(modId);
        networkWrapper.registerMessage(CtoSMessage.Handler.class, CtoSMessage.class, 2, Side.SERVER);
        networkWrapper.registerMessage(MessageUpdateGui.Handler.class, MessageUpdateGui.class, 3, Side.CLIENT);

        SBSRecipeManager.addDefaultRecipes();
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent evt)
    {
    }

    @Mod.EventHandler
    public void serverUnload(FMLServerStoppingEvent evt)
    {
    }

}
