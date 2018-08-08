package com.mike_caron.megacorp.block;

import com.mike_caron.megacorp.item.CorporateCard;
import com.mike_caron.megacorp.item.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.UUID;

public class MachineBlockBase extends FacingBlockBase
{

    public MachineBlockBase(Material material, String name)
    {
        super(material, name);

        setHardness(10f);
        setHarvestLevel("pickaxe", 1);

    }

    private TileEntityBase getTE(IBlockAccess worldIn, BlockPos pos)
    {
        TileEntity te = worldIn.getTileEntity(pos);

        if(te instanceof TileEntityBase)
            return (TileEntityBase)te;

        return null;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(!worldIn.isRemote)
        {
            ItemStack heldItem = playerIn.getHeldItem(hand);
            if (heldItem.getItem() == ModItems.corporateCard)
            {
                TileEntityBase te = getTE(worldIn, pos);
                if(te != null && te.getOwner() == null)
                {
                    UUID owner = CorporateCard.getOwner(heldItem);
                    if(owner != null)
                    {
                        te.setOwner(owner);
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
