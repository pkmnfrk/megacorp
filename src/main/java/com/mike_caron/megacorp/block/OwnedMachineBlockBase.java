package com.mike_caron.megacorp.block;

import com.mike_caron.megacorp.impl.Corporation;
import com.mike_caron.megacorp.item.CorporateCard;
import com.mike_caron.megacorp.item.ModItems;
import com.mike_caron.mikesmodslib.block.MachineBlockBase;
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
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.UUID;

public class OwnedMachineBlockBase
    extends MachineBlockBase
{

    public OwnedMachineBlockBase(Material material, String name)
    {
        super(material, name);
    }

    private TileEntityOwnedBase getTE(IBlockAccess worldIn, BlockPos pos)
    {
        TileEntity te = worldIn.getTileEntity(pos);

        if(te instanceof TileEntityOwnedBase)
            return (TileEntityOwnedBase)te;

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
                TileEntityOwnedBase te = getTE(worldIn, pos);
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

    @Override
    protected void addBlockProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data)
    {
        super.addBlockProbeInfo(mode, info, player, world, blockState, data);

        TileEntityOwnedBase teb = getTE(world, data.getPos());
        Corporation corp = null;

        if(teb != null) corp = teb.getCorporation();

        if(corp != null)
        {
            info.horizontal(info.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                .item(new ItemStack(ModItems.corporateCard))
                .vertical()
                //.text("Corporation")
                .text(corp.getName());
        }
        else
        {
            info.horizontal(info.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                .item(new ItemStack(ModItems.corporateCard))
                .vertical()
                .text(new TextComponentTranslation("tile.megacorp:misc.unowned").getFormattedText());
        }
    }


}
