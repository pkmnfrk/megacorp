package com.mike_caron.megacorp.block;

import com.mike_caron.megacorp.integrations.ITOPInfoProvider;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class MachineBlockBase extends FacingBlockBase implements ITOPInfoProvider
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
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data)
    {
        TileEntityBase teb = getTE(world, data.getPos());

        if(teb == null) return;

        IProbeInfo box = probeInfo
            .vertical(probeInfo.defaultLayoutStyle().borderColor(0xff008000));

        addMegaCorpProbeInfo(mode, box, player, world, blockState, data);

    }

    protected void addMegaCorpProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data)
    {

    }

}
