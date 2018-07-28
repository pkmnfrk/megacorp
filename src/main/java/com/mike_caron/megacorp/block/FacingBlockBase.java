package com.mike_caron.megacorp.block;

import mcjty.theoneprobe.api.IProbeHitData;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class FacingBlockBase extends BlockBase
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public FacingBlockBase(Material material, String name)
    {
        super(material, name);

        setDefaultState(addStateProperties(this.blockState.getBaseState()));
    }

    protected IBlockState addStateProperties(IBlockState blockState)
    {
        return blockState.withProperty(FACING, EnumFacing.NORTH);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        List<IProperty<?>> props = new ArrayList<>();
        addAdditionalPropeties(props);

        return new BlockStateContainer(this, props.toArray(new IProperty<?>[props.size()]));
    }

    protected void addAdditionalPropeties(List<IProperty<?>> properties)
    {
        properties.add(FACING);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getIndex() - 2;
    }

    public static EnumFacing getFacingFromEntity(BlockPos clickedBlock, EntityLivingBase entity)
    {
        return EnumFacing.getFacingFromVector(
                (float) entity.posX - clickedBlock.getX(),
                (float) entity.posY - clickedBlock.getY(),
                (float) entity.posZ - clickedBlock.getZ()
        );
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState()
                .withProperty(FACING, EnumFacing.getFront((meta & 3) + 2))
        ;

    }
}
