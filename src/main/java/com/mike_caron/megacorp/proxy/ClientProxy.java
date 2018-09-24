package com.mike_caron.megacorp.proxy;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.ModBlocks;
import com.mike_caron.megacorp.client.models.ModelBottle;
import com.mike_caron.megacorp.item.ModItems;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
            try
            {
                Field f = OreDictionary.class.getDeclaredField("idToStack");
                f.setAccessible(true);
                List<NonNullList<ItemStack>> oreRegistry = (List<NonNullList<ItemStack>>)f.get(null);
                List<Integer> relevantIds = new ArrayList<>();

                for(int oreId = 0; oreId < oreRegistry.size(); oreId ++)
                {
                    NonNullList<ItemStack> ores = oreRegistry.get(oreId);
                    for(ItemStack is : ores)
                    {
                        if(is.isItemEqual(event.getItemStack()))
                        {
                            relevantIds.add(oreId);
                            break;
                        }
                    }
                }

                if(relevantIds.size() > 0)
                {
                    f = OreDictionary.class.getDeclaredField("idToName");
                    f.setAccessible(true);
                    List<String> idToName = (List<String>)f.get(null);

                    for (int id : relevantIds)
                    {
                        event.getToolTip().add("OD: " + idToName.get(id));
                    }
                }

            }
            catch (Exception ex)
            {
                MegaCorpMod.logger.error("Error getting ore dict data", ex);
            }
        }
    }
}
