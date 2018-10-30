package com.mike_caron.megacorp.block.vending_machine;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.MachineBlockBase;
import com.mike_caron.megacorp.item.Bottle;
import com.mike_caron.megacorp.util.FluidUtils;
import com.mike_caron.megacorp.util.TOPUtils;
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

public class BlockVendingMachine
    extends MachineBlockBase
{
    public BlockVendingMachine()
    {
        super(Material.IRON, "vending_machine");


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
        return new TileEntityVendingMachine();
    }

    @Nullable
    private TileEntityVendingMachine getTE(IBlockAccess worldIn, BlockPos pos)
    {
        TileEntity ret = worldIn.getTileEntity(pos);
        if(ret instanceof TileEntityVendingMachine) return (TileEntityVendingMachine) ret;
        return null;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ))
            return true;

        if(worldIn.isRemote)
            return true;

        TileEntityVendingMachine te = getTE(worldIn, pos);

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
    public boolean hasInfo(EntityPlayer player)
    {
        return player.isSneaking();
    }

    @Override
    protected void addMegaCorpProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data)
    {
        TileEntityVendingMachine te = getTE(world, data.getPos());

        if(te == null) return;

        if(player.isSneaking())
        {
            super.addMegaCorpProbeInfo(mode, info, player, world, blockState, data);

            TOPUtils.addFluidTank(info, te.fluidTank);
        }
    }

    @Override
    protected void getExtraDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state)
    {
        super.getExtraDrops(drops, world, pos, state);

        TileEntityVendingMachine te = getTE(world, pos);

        if(te != null)
        {
            if (te.fluidTank.getFluidAmount() > 0)
            {
                drops.add(Bottle.with(te.fluidTank.getFluid()));
            }
        }
    }
}
