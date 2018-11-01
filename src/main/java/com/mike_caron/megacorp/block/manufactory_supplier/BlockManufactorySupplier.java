package com.mike_caron.megacorp.block.manufactory_supplier;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.OwnedMachineBlockBase;
import com.mike_caron.megacorp.util.ItemUtils;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
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
import java.awt.Color;

public class BlockManufactorySupplier
    extends OwnedMachineBlockBase
{
    public BlockManufactorySupplier()
    {
        super(Material.IRON, "manufactory_supplier");


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
        return new TileEntityManufactorySupplier();
    }

    @Nullable
    private TileEntityManufactorySupplier getTE(IBlockAccess worldIn, BlockPos pos)
    {
        TileEntity ret = worldIn.getTileEntity(pos);
        if(ret instanceof TileEntityManufactorySupplier) return (TileEntityManufactorySupplier) ret;
        return null;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ))
            return true;

        if(worldIn.isRemote)
            return true;

        TileEntityManufactorySupplier te = getTE(worldIn, pos);

        if(te == null)
            return false;

        playerIn.openGui(MegaCorpMod.instance, 2, worldIn, pos.getX(), pos.getY(), pos.getZ());

        return true;
    }

    @Override
    protected void addMegaCorpProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data)
    {
        super.addMegaCorpProbeInfo(mode, info, player, world, blockState, data);

        TileEntityManufactorySupplier te = getTE(world, data.getPos());

        if(te == null) return;

        IProbeInfo vert = null;

        if(te.getDesiredItems() != null)
        {
            vert = info.vertical();
            vert
                .horizontal()
                .item(te.getDesiredItems().get(0))
                .progress(te.getProgress(), te.getLevelUpThreshold(),
                    new ProgressStyle()
                        .backgroundColor(Color.black.getRGB())
                        .filledColor(Color.yellow.getRGB())
                        .alternateFilledColor(Color.orange.getRGB())
                        .suffix("xp")
                );
        }
        if(player.isSneaking())
        {
            if(vert == null)
                vert = info.vertical();

            vert
                .horizontal()
                .item(ItemUtils.CLOCK)
                .progress(te.getTicksRemaining(), te.getTicksPerCycle());
        }


    }

    @Override
    public void getExtraDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state)
    {
        super.getExtraDrops(drops, world, pos, state);

        //TODO: Add work voucher?
    }
}
