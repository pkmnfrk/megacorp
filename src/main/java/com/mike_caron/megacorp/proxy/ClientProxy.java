package com.mike_caron.megacorp.proxy;

import com.mike_caron.megacorp.block.ModBlocks;
import com.mike_caron.megacorp.client.models.ModelBottle;
import com.mike_caron.megacorp.item.ModItems;
import com.mike_caron.megacorp.util.OreDictUtil;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy
    extends CommonProxy
{

    @Override
    public void preInit(FMLPreInitializationEvent e)
    {
        super.preInit(e);

    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        ModBlocks.initModels();
        ModItems.initModels();

        ModBlocks.renderFluids();

        ModelLoaderRegistry.registerLoader(ModelBottle.CustomModelLoader.INSTANCE);
        ModelBakery.registerItemVariants(ModItems.bottle, ModelBottle.LOCATION);
    }

    static final boolean doOreDictTooltips = true;

    @SubscribeEvent
    public static void onToolTip(ItemTooltipEvent event)
    {
        if(!doOreDictTooltips) return;

        if(event.getFlags().isAdvanced())
        {
            List<String> dicts = OreDictUtil.getDictsForItem(event.getItemStack());

            for (String dict : dicts)
            {
                event.getToolTip().add("OD: " + dict);
            }
        }
    }
}
