package com.mike_caron.megacorp.block;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.capital_investor.BlockCapitalInvestor;
import com.mike_caron.megacorp.block.profit_condenser.BlockProfitCondenser;
import com.mike_caron.megacorp.block.profit_materializer.BlockProfitMaterializer;
import com.mike_caron.megacorp.block.profit_materializer.TileEntityProfitMaterializer;
import com.mike_caron.megacorp.block.sbs.BlockSBS;
import com.mike_caron.megacorp.block.uplink.BlockUplink;
import com.mike_caron.megacorp.block.uplink.TileEntityUplink;
import com.mike_caron.megacorp.fluid.ModFluids;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Mod.EventBusSubscriber
@GameRegistry.ObjectHolder (MegaCorpMod.modId)
public class ModBlocks
{
    //@GameRegistry.ObjectHolder(TransmutationChamber.id)
    //public static TransmutationChamber transmutationChamber;

    @GameRegistry.ObjectHolder("money")
    public static BlockFluidBase money;

    @GameRegistry.ObjectHolder("dense_money")
    public static BlockFluidBase dense_money;

    @GameRegistry.ObjectHolder("small_business_simulator")
    public static BlockSBS small_business_simulator;

    @GameRegistry.ObjectHolder("uplink")
    public static BlockUplink uplink;

    @GameRegistry.ObjectHolder("profit_materializer")
    public static BlockProfitMaterializer profit_materializer;

    @GameRegistry.ObjectHolder("profit_condenser")
    public static BlockProfitCondenser profit_condenser;

    @GameRegistry.ObjectHolder("capital_investor")
    public static BlockCapitalInvestor capital_investor;

    //@GameRegistry.ObjectHolder("money_block")
    //public static BlockBase money_block;

    //@GameRegistry.ObjectHolder("dense_money_block")
    //public static BlockBase dense_money_block;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.register(new BlockFluidBase(ModFluids.MONEY, "money", MapColor.GREEN));
        registry.register(new BlockFluidBase(ModFluids.DENSE_MONEY, "dense_money", MapColor.GREEN_STAINED_HARDENED_CLAY));
        registry.register(new BlockSBS());
        registry.register(new BlockUplink());
        registry.register(new BlockProfitMaterializer());
        registry.register(new BlockProfitCondenser());
        registry.register(new BlockCapitalInvestor());

        //registry.register(money_block = (BlockBase)new BlockBase(Material.IRON, "money_block").setHardness(10));
        //registry.register(dense_money_block = (BlockBase)new BlockBase(Material.IRON, "dense_money_block").setHardness(20));

        //money_block.setHarvestLevel("pickaxe", 2);
        //dense_money_block.setHarvestLevel("pickaxe", 3);

        GameRegistry.registerTileEntity(TileEntityProfitMaterializer.class, new ResourceLocation(MegaCorpMod.modId, "profit_materializer"));
        GameRegistry.registerTileEntity(TileEntityUplink.class, new ResourceLocation(MegaCorpMod.modId, "uplink"));
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();

        try
        {
            for (Field field : ModBlocks.class.getDeclaredFields())
            {
                if (Modifier.isStatic(field.getModifiers()) && Block.class.isAssignableFrom(field.getType()))
                {
                    Block block = (Block) field.get(null);

                    registry.register(
                            new ItemBlock(block)
                            .setRegistryName(block.getRegistryName())
                    );
                }
            }
        }
        catch(IllegalAccessException ex)
        {
            throw new RuntimeException("Unable to reflect upon myself??");
        }

        //OreDictionary.registerOre("blockMoney", money_block);
        //OreDictionary.registerOre("blockDenseMoney", dense_money_block);
    }

    @SideOnly(Side.CLIENT)
    public static void initModels()
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(money), 0, new ModelResourceLocation(money.getRegistryName(), "normal"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(dense_money), 0, new ModelResourceLocation(dense_money.getRegistryName(), "normal"));

        try
        {
            for (Field field : ModBlocks.class.getDeclaredFields())
            {
                if (Modifier.isStatic(field.getModifiers()) && BlockBase.class.isAssignableFrom(field.getType()))
                {
                    BlockBase block = (BlockBase) field.get(null);

                    block.initModel();
                }
            }
        }
        catch(IllegalAccessException ex)
        {
            throw new RuntimeException("Unable to reflect upon myself??");
        }
    }

    public static void renderFluids() {
        money.render();
        dense_money.render();
    }
}
