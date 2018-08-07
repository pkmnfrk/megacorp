package com.mike_caron.megacorp.block.profit_materializer;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.MachineBlockBase;
import com.mike_caron.megacorp.fluid.ModFluids;
import com.mike_caron.megacorp.integrations.ITOPInfoProvider;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nullable;
import java.awt.*;

public class BlockProfitMaterializer
        extends MachineBlockBase
        implements ITOPInfoProvider
{
    public BlockProfitMaterializer()
    {
        super(Material.IRON, "profit_materializer");


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
        return new TileEntityProfitMaterializer();
    }

    @Nullable
    private TileEntityProfitMaterializer getTE(IBlockAccess worldIn, BlockPos pos)
    {
        TileEntity ret = worldIn.getTileEntity(pos);
        if(ret instanceof TileEntityProfitMaterializer) return (TileEntityProfitMaterializer) ret;
        return null;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(worldIn.isRemote)
            return true;

        TileEntityProfitMaterializer te = getTE(worldIn, pos);

        if(te == null)
            return false;

        playerIn.openGui(MegaCorpMod.instance, 1, worldIn, pos.getX(), pos.getY(), pos.getZ());

        return true;
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data)
    {
        TileEntityProfitMaterializer te = getTE(world, data.getPos());

        if(te == null) return;

        FluidStack fluid = te.fluidTank.getFluid();
        int fluidColor = Color.BLUE.getRGB();

        if(fluid != null)
        {
            if(fluid.getFluid() != null)
            {
                fluidColor = fluid.getFluid().getColor();
            }

            probeInfo
                .horizontal()
                .item(FluidUtil.getFilledBucket(new FluidStack(ModFluids.MONEY, 1000)))
                .vertical()
                .text(fluid.getLocalizedName())
                .progress(fluid.amount, te.fluidTank.getCapacity(), new ProgressStyle()
                    .filledColor(fluidColor)
                    .suffix("mB")
                )
            ;
        }

    }
}
