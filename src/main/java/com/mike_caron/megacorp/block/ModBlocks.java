package com.mike_caron.megacorp.block;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.ModMaterials;
import com.mike_caron.megacorp.fluid.ModFluids;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidFinite;
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
    public static BlockFluidMoney money;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.register(new BlockFluidMoney());

        //GameRegistry.registerTileEntity(TransmutationChamberTileEntity.class, new ResourceLocation(MegaCorpMod.modId, TransmutationChamber.id));
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();

        registry.register(
                new ItemBlock(money)
                .setRegistryName(money.getRegistryName())
        );

        try
        {
            for (Field field : ModBlocks.class.getDeclaredFields())
            {
                if (Modifier.isStatic(field.getModifiers()) && BlockBase.class.isAssignableFrom(field.getType()))
                {
                    BlockBase block = (BlockBase) field.get(null);

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
    }

    @SideOnly(Side.CLIENT)
    public static void initModels()
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(money), 0, new ModelResourceLocation(money.getRegistryName(), "normal"));

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
    }
}
