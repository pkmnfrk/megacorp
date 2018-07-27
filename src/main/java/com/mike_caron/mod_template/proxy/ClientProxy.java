package com.mike_caron.mod_template.proxy;

import com.mike_caron.mod_template.block.ModBlocks;
import com.mike_caron.mod_template.item.ModItems;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy
    extends CommonProxy
{

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        ModBlocks.initModels();
        ModItems.initModels();
    }
}
