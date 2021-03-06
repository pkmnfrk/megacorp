package com.mike_caron.megacorp.block.profit_condenser;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.MachineBlockBase;
import com.mike_caron.megacorp.item.Bottle;
import com.mike_caron.megacorp.util.FluidUtils;
import com.mike_caron.megacorp.util.TOPUtils;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
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
import java.util.List;

public class BlockProfitCondenser extends MachineBlockBase
{
    public static final PropertyBool SWAPPED = PropertyBool.create("swapped");

    public BlockProfitCondenser()
    {
        super(Material.IRON, "profit_condenser");


    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return super.getStateFromMeta(meta).withProperty(SWAPPED, (meta & 4) == 4);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return super.getMetaFromState(state) + (state.getValue(SWAPPED) ? 4 : 0);
    }

    @Override
    protected void addAdditionalPropeties(List<IProperty<?>> properties)
    {
        super.addAdditionalPropeties(properties);

        properties.add(SWAPPED);
    }

    @Override
    protected IBlockState addStateProperties(IBlockState blockState)
    {
        return super.addStateProperties(blockState).withProperty(SWAPPED, false);
    }

    @Nullable
    private TileEntityProfitCondenser getTE(IBlockAccess worldIn, BlockPos pos)
    {
        TileEntity ret = worldIn.getTileEntity(pos);
        if(ret instanceof TileEntityProfitCondenser) return (TileEntityProfitCondenser) ret;
        return null;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityProfitCondenser();
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ))
            return true;

        if(worldIn.isRemote)
            return true;

        TileEntityProfitCondenser te = getTE(worldIn, pos);

        if(te == null)
            return false;

        if(FluidUtils.drainPlayerHandOfFluid(worldIn, pos, playerIn, te.inputFluidTank))
        {
            return true;
        }

        if(FluidUtils.fillPlayerHandWithFluid(worldIn, pos, playerIn, te.outputFluidTank))
        {
            return true;
        }

        playerIn.openGui(MegaCorpMod.instance, 1, worldIn, pos.getX(), pos.getY(), pos.getZ());

        return true;
    }

    @Override
    protected void addMegaCorpProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data)
    {
        super.addMegaCorpProbeInfo(mode, info, player, world, blockState, data);

        TileEntityProfitCondenser te = getTE(world, data.getPos());

        if(te == null) return;

        if(player.isSneaking())
        {
            TOPUtils.addFluidTank(info, te.inputFluidTank);
            TOPUtils.addFluidTank(info, te.outputFluidTank);
        }

        //if(te.getCurrentRecipe() != null)
        //{
        TOPUtils.addProgress(info, te.getProgress());
        //}
    }

    @Override
    protected void getExtraDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state)
    {
        super.getExtraDrops(drops, world, pos, state);

        TileEntityProfitCondenser te = getTE(world, pos);

        if(te != null)
        {
            if (te.inputFluidTank.getFluidAmount() > 0)
            {
                drops.add(Bottle.with(te.inputFluidTank.getFluid()));
            }

            if (te.outputFluidTank.getFluidAmount() > 0)
            {
                drops.add(Bottle.with(te.outputFluidTank.getFluid()));
            }

            if(te.getCurrentRecipe() != null)
            {
                //need to refund the ingredients
                drops.add(Bottle.with(te.getCurrentRecipe().input));
            }
        }
    }

}
