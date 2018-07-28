package com.mike_caron.megacorp.item;

import com.mike_caron.megacorp.MegaCorpMod;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Mod.EventBusSubscriber
@GameRegistry.ObjectHolder(MegaCorpMod.modId)
public class ModItems
{
    //@GameRegistry.ObjectHolder(SoulboundTalisman.id)
    //public static SoulboundTalisman soulboundTalisman;

    @GameRegistry.ObjectHolder("ingot_money")
    public static Item ingotMoney;

    @GameRegistry.ObjectHolder("ingot_dense_money")
    public static Item ingotDenseMoney;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();

        //registry.register(new SoulboundTalisman());
        registry.register(ingotMoney = new Item()
                .setRegistryName("ingot_money")
                .setUnlocalizedName("megacorp:ingot_money")
                .setCreativeTab(MegaCorpMod.creativeTab)

        );

        registry.register(ingotDenseMoney = new Item()
                .setRegistryName("ingot_dense_money")
                .setUnlocalizedName("megacorp:ingot_dense_money")
                .setCreativeTab(MegaCorpMod.creativeTab)

        );

        OreDictionary.registerOre("ingotMoney", ingotMoney);
        OreDictionary.registerOre("ingotDenseMoney", ingotDenseMoney);
    }

    @SideOnly(Side.CLIENT)
    public static void initModels()
    {
        initModel(ingotMoney);
        initModel(ingotDenseMoney);

        try
        {
            for (Field field : ModItems.class.getDeclaredFields())
            {
                if (Modifier.isStatic(field.getModifiers()) && ItemBase.class.isAssignableFrom(field.getType()))
                {
                    ItemBase item = (ItemBase) field.get(null);

                    item.initModel();
                }
            }
        }
        catch(IllegalAccessException ex)
        {
            throw new RuntimeException("Unable to reflect upon myself??");
        }
        //soulboundTalisman.initModel();
    }

    private static void initModel(Item item)
    {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
