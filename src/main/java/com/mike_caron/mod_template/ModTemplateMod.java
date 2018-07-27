package com.mike_caron.mod_template;

import com.mike_caron.mod_template.integrations.MainCompatHandler;
import com.mike_caron.mod_template.network.CtoSMessage;
import com.mike_caron.mod_template.network.PacketHandlerServer;
import com.mike_caron.mod_template.proxy.IModProxy;
import net.minecraftforge.common.MinecraftForge;
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
        modid = ModTemplateMod.modId,
        name = ModTemplateMod.name,
        version = ModTemplateMod.version,
        acceptedMinecraftVersions = "[1.12.2]"
        //,dependencies = "required-after:projecte@[1.12-PE1.3.1,)"
)
@Mod.EventBusSubscriber
public class ModTemplateMod
{
    public static final String modId = "mod_template";
    public static final String name = "Equivalent Integrations";
    public static final String version = "0.1.4";

    public static final Logger logger = LogManager.getLogger(modId);

    public static final CreativeTab creativeTab = new CreativeTab();

    @SuppressWarnings("unused")
    @Mod.Instance(modId)
    public static ModTemplateMod instance;

    @SidedProxy(
            serverSide = "com.mike_caron.mod_template.proxy.CommonProxy",
            clientSide = "com.mike_caron.mod_template.proxy.ClientProxy"
    )
    public static IModProxy proxy;

    public static SimpleNetworkWrapper networkWrapper;

    @Mod.EventHandler
    public  void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);

        MainCompatHandler.registerAll();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(modId);
        networkWrapper.registerMessage(PacketHandlerServer.class, CtoSMessage.class, 2, Side.SERVER);
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
