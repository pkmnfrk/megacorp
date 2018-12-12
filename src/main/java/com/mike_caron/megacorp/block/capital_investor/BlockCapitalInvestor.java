package com.mike_caron.megacorp.block.capital_investor;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.OwnedMachineBlockBase;
import com.mike_caron.megacorp.item.Bottle;
import com.mike_caron.mikesmodslib.util.FluidUtils;
import com.mike_caron.mikesmodslib.util.TOPUtils;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
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

public class BlockCapitalInvestor
    extends OwnedMachineBlockBase
{
    public BlockCapitalInvestor()
    {
        super(Material.IRON, "capital_investor");


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
        return new TileEntityCapitalInvestor();
    }

    @Nullable
    private TileEntityCapitalInvestor getTE(IBlockAccess worldIn, BlockPos pos)
    {
        TileEntity ret = worldIn.getTileEntity(pos);
        if(ret instanceof TileEntityCapitalInvestor) return (TileEntityCapitalInvestor) ret;
        return null;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ))
            return true;

        if(worldIn.isRemote)
            return true;

        TileEntityCapitalInvestor te = getTE(worldIn, pos);

        if(te == null)
            return false;

        if(FluidUtils.drainPlayerHandOfFluid(worldIn, pos, playerIn, te.fluidTank))
        {
            return true;
        }

        if(FluidUtils.fillPlayerHandWithFluid(worldIn, pos, playerIn, te.fluidTank))
        {
            return true;
        }

        playerIn.openGui(MegaCorpMod.instance, 1, worldIn, pos.getX(), pos.getY(), pos.getZ());

        return true;
    }

    @Override
    protected void addBlockProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data)
    {
        super.addBlockProbeInfo(mode, info, player, world, blockState, data);

        TileEntityCapitalInvestor te = getTE(world, data.getPos());

        if(te == null) return;

        if(player.isSneaking())
        {
            TOPUtils.addFluidTank(info, te.fluidTank);
        }
    }

    @Override
    protected void getExtraDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state)
    {
        super.getExtraDrops(drops, world, pos, state);

        TileEntityCapitalInvestor te = getTE(world, pos);

        if(te != null)
        {
            if (te.fluidTank.getFluidAmount() > 0)
            {
                drops.add(Bottle.with(te.fluidTank.getFluid()));
            }
        }
    }
}
