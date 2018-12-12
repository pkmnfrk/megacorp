package com.mike_caron.megacorp.block;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.ModConfig;
import com.mike_caron.megacorp.block.capital_investor.BlockCapitalInvestor;
import com.mike_caron.megacorp.block.capital_investor.TileEntityCapitalInvestor;
import com.mike_caron.megacorp.block.liquid_shipping_depot.BlockLiquidShippingDepot;
import com.mike_caron.megacorp.block.liquid_shipping_depot.TileEntityLiquidShippingDepot;
import com.mike_caron.megacorp.block.manufactory_supplier.BlockManufactorySupplier;
import com.mike_caron.megacorp.block.manufactory_supplier.TileEntityManufactorySupplier;
import com.mike_caron.megacorp.block.profit_condenser.BlockProfitCondenser;
import com.mike_caron.megacorp.block.profit_condenser.TileEntityProfitCondenser;
import com.mike_caron.megacorp.block.profit_materializer.BlockProfitMaterializer;
import com.mike_caron.megacorp.block.profit_materializer.TileEntityProfitMaterializer;
import com.mike_caron.megacorp.block.sbs.BlockSBS;
import com.mike_caron.megacorp.block.sbs.TileEntitySBS;
import com.mike_caron.megacorp.block.shipping_depot.BlockShippingDepot;
import com.mike_caron.megacorp.block.shipping_depot.TileEntityShippingDepot;
import com.mike_caron.megacorp.block.uplink.BlockUplink;
import com.mike_caron.megacorp.block.uplink.TileEntityUplink;
import com.mike_caron.megacorp.block.vending_machine.BlockVendingMachine;
import com.mike_caron.megacorp.block.vending_machine.TileEntityVendingMachine;
import com.mike_caron.megacorp.fluid.ModFluids;
import com.mike_caron.mikesmodslib.block.BlockBase;
import com.mike_caron.mikesmodslib.block.BlockFluidBase;
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

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Stream;

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

    @GameRegistry.ObjectHolder("shipping_depot")
    public static BlockShippingDepot shipping_depot;

    @GameRegistry.ObjectHolder("liquid_shipping_depot")
    public static BlockLiquidShippingDepot liquid_shipping_depot;

    @GameRegistry.ObjectHolder("vending_machine")
    public static BlockVendingMachine vending_machine;

    @GameRegistry.ObjectHolder("manufactory_supplier")
    public static BlockManufactorySupplier manufactory_supplier;

    //@GameRegistry.ObjectHolder("money_block")
    //public static BlockBase money_block;

    //@GameRegistry.ObjectHolder("dense_money_block")
    //public static BlockBase dense_money_block;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.register(new BlockFluidBase(ModFluids.MONEY, "money", MapColor.GREEN).setCreativeTab(MegaCorpMod.creativeTab));
        registry.register(new BlockFluidBase(ModFluids.DENSE_MONEY, "dense_money", MapColor.GREEN_STAINED_HARDENED_CLAY).setCreativeTab(MegaCorpMod.creativeTab));
        registry.register(new BlockSBS().setCreativeTab(MegaCorpMod.creativeTab));
        registry.register(new BlockUplink().setCreativeTab(MegaCorpMod.creativeTab));
        registry.register(new BlockProfitMaterializer().setCreativeTab(MegaCorpMod.creativeTab));
        registry.register(new BlockProfitCondenser().setCreativeTab(MegaCorpMod.creativeTab));
        registry.register(new BlockCapitalInvestor().setCreativeTab(MegaCorpMod.creativeTab));
        registry.register(new BlockShippingDepot().setCreativeTab(MegaCorpMod.creativeTab));
        registry.register(new BlockLiquidShippingDepot());
        registry.register(new BlockManufactorySupplier().setCreativeTab(MegaCorpMod.creativeTab));

        //registry.register(money_block = (BlockBase)new BlockBase(Material.IRON, "money_block").setHardness(10));
        //registry.register(dense_money_block = (BlockBase)new BlockBase(Material.IRON, "dense_money_block").setHardness(20));

        //money_block.setHarvestLevel("pickaxe", 2);
        //dense_money_block.setHarvestLevel("pickaxe", 3);

        GameRegistry.registerTileEntity(TileEntityProfitMaterializer.class, new ResourceLocation(MegaCorpMod.modId, "profit_materializer"));
        GameRegistry.registerTileEntity(TileEntityUplink.class, new ResourceLocation(MegaCorpMod.modId, "uplink"));
        GameRegistry.registerTileEntity(TileEntitySBS.class, new ResourceLocation(MegaCorpMod.modId, "small_business_simulator"));
        GameRegistry.registerTileEntity(TileEntityProfitCondenser.class, new ResourceLocation(MegaCorpMod.modId, "profit_condenser"));
        GameRegistry.registerTileEntity(TileEntityShippingDepot.class, new ResourceLocation(MegaCorpMod.modId, "shipping_depot"));
        GameRegistry.registerTileEntity(TileEntityLiquidShippingDepot.class, new ResourceLocation(MegaCorpMod.modId, "liquid_shipping_depot"));
        GameRegistry.registerTileEntity(TileEntityCapitalInvestor.class, new ResourceLocation(MegaCorpMod.modId, "capital_investor"));
        GameRegistry.registerTileEntity(TileEntityManufactorySupplier.class, new ResourceLocation(MegaCorpMod.modId, "manufactory_supplier"));

        if(ModConfig.vendingMachineEnabled)
        {
            registry.register(new BlockVendingMachine());
            GameRegistry.registerTileEntity(TileEntityVendingMachine.class, new ResourceLocation(MegaCorpMod.modId, "vending_machine"));
        }
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();

        getAllBlocks().forEach(block -> {
            if (block != null)
                registry.register(
                    new ItemBlock(block)
                        .setRegistryName(block.getRegistryName())
                );
        });

        //OreDictionary.registerOre("blockMoney", money_block);
        //OreDictionary.registerOre("blockDenseMoney", dense_money_block);
    }

    @SideOnly(Side.CLIENT)
    public static void initModels()
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(money), 0, new ModelResourceLocation(money.getRegistryName(), "normal"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(dense_money), 0, new ModelResourceLocation(dense_money.getRegistryName(), "normal"));

        getAllBlocks().filter(b -> b instanceof BlockBase).map(b -> (BlockBase)b).forEach(BlockBase::initModel);
    }

    public static Stream<Block> getAllBlocks()
    {
        return Arrays.stream(ModBlocks.class.getDeclaredFields()).filter(f -> Modifier.isStatic(f.getModifiers()) && Block.class.isAssignableFrom(f.getType())).map(f -> {
            try
            {
                Block ret = (Block)f.get(null);

                if(ret == null)
                {
                    //MegaCorpMod.logger.error("Block " + f.getName() + " is null");
                    return null;
                }
                return ret;
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException("Unable to reflect upon myself??");
            }
        });
    }

    public static void renderFluids() {
        money.render();
        dense_money.render();
    }
}
