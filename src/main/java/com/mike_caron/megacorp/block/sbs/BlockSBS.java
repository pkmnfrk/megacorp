package com.mike_caron.megacorp.block.sbs;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.MachineBlockBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockSBS
    extends MachineBlockBase
{
    public BlockSBS()
    {
        super(Material.IRON, "small_business_simulator");


    }


    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntitySBS();
    }

    @Override
    public void getExtraDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state)
    {
        super.getExtraDrops(drops, world, pos, state);

        TileEntitySBS te = getTE(world, pos);

        if(te != null)
        {
            for (int i = 0; i < te.reagents.getSlots(); i++)
            {
                ItemStack stack = te.reagents.getStackInSlot(i);
                if (stack.isEmpty())
                    continue;
                drops.add(stack);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ))
            return true;

        if(worldIn.isRemote)
            return true;

        TileEntitySBS te = getTE(worldIn, pos);

        if(te == null)
            return false;

        playerIn.openGui(MegaCorpMod.instance, 3, worldIn, pos.getX(), pos.getY(), pos.getZ());

        return true;
    }

    private TileEntitySBS getTE(IBlockAccess worldIn, BlockPos pos)
    {
        TileEntity te = worldIn.getTileEntity(pos);

        if(te instanceof TileEntitySBS)
            return (TileEntitySBS)te;

        return null;
    }
}
