package com.mike_caron.megacorp.block;

import com.mike_caron.megacorp.item.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MachineBlockBase extends FacingBlockBase
{

    public MachineBlockBase(Material material, String name)
    {
        super(material, name);

        setHardness(10f);
        setHarvestLevel("pickaxe", 1);

    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(!worldIn.isRemote)
        {
            ItemStack heldItem = playerIn.getHeldItem(hand);
            if (heldItem.getItem() == ModItems.corporateCard)
            {
                return true;
            }
        }

        return false;
    }
}
