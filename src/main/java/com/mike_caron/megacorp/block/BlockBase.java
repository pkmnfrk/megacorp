package com.mike_caron.megacorp.block;

import com.mike_caron.megacorp.MegaCorpMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBase extends Block
{
    public BlockBase(Material material, String name)
    {
        super(material);
        setRegistryName(name);
        setUnlocalizedName(getRegistryName().toString());
        setCreativeTab(MegaCorpMod.creativeTab);
    }

    @SuppressWarnings("ConstantConditions")
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if(!worldIn.isRemote)
        {

            NonNullList<ItemStack> drops = NonNullList.create();

            this.getExtraDrops(drops, worldIn, pos, state);

            drops.forEach(stack -> InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack));

        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
    {
        return false;
    }

    protected void getExtraDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state)
    {
    }
}
