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
import net.minecraftforge.registries.IForgeRegistry;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Stream;

@Mod.EventBusSubscriber
@GameRegistry.ObjectHolder(MegaCorpMod.modId)
public class ModItems
{
    //@GameRegistry.ObjectHolder("ingot_money")
    //public static Item ingotMoney;

    //@GameRegistry.ObjectHolder("ingot_dense_money")
    //public static Item ingotDenseMoney;

    @GameRegistry.ObjectHolder("corporate_card")
    public static CorporateCard corporateCard;

    @GameRegistry.ObjectHolder("black_card")
    public static ItemBase blackCard;

    @GameRegistry.ObjectHolder("bottle")
    public static ItemBase bottle;

    @GameRegistry.ObjectHolder("work_voucher")
    public static ItemBase work_voucher;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();

        registry.register(new CorporateCard());
        registry.register(new DebugItemBase()
                            .setRegistryName("black_card")
                            .setTranslationKey("megacorp:black_card")
                            .setCreativeTab(MegaCorpMod.creativeTab)
                            .setMaxStackSize(1)
        );
        registry.register(new Bottle());
        registry.register(new WorkVoucher());


        /*
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
        */

        /*
        OreDictionary.registerOre("ingotMoney", ingotMoney);
        OreDictionary.registerOre("ingotDenseMoney", ingotDenseMoney);
        */
    }

    @SideOnly(Side.CLIENT)
    public static void initModels()
    {
        //initModel(ingotMoney);
        //initModel(ingotDenseMoney);

        getAllItems().forEach(ItemBase::initModel);
    }

    public static Stream<ItemBase> getAllItems()
    {
        return Arrays.stream(ModItems.class.getDeclaredFields()).filter(f -> Modifier.isStatic(f.getModifiers()) && ItemBase.class.isAssignableFrom(f.getType())).map(f -> {
            try
            {
                return (ItemBase) f.get(null);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException("Unable to reflect upon myself??");
            }
        });
    }

    private static void initModel(Item item)
    {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
