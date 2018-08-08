package com.mike_caron.megacorp.block;

import com.mike_caron.megacorp.api.CorporationManager;
import com.mike_caron.megacorp.api.ICorporation;
import com.mike_caron.megacorp.integrations.ITOPInfoProvider;
import com.mike_caron.megacorp.item.CorporateCard;
import com.mike_caron.megacorp.item.ModItems;
import mcjty.theoneprobe.api.ElementAlignment;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.UUID;

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

        if(teb.getOwner() != null)
        {
            ICorporation corp = CorporationManager.getInstance(world).getCorporationForOwner(teb.getOwner());

            IProbeInfo box = probeInfo
                .vertical(probeInfo.defaultLayoutStyle().borderColor(0xff008000));

            box.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                .item(new ItemStack(ModItems.corporateCard))
                .vertical()
                    //.text("Corporation")
                    .text(corp.getName());

            addMegaCorpProbeInfo(mode, box, player, world, blockState, data);
        }

    }

    protected void addMegaCorpProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data)
    {

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
