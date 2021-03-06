package com.mike_caron.megacorp.block.uplink;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.api.ICorporationManager;
import com.mike_caron.megacorp.block.OwnedMachineBlockBase;
import com.mike_caron.megacorp.impl.Corporation;
import com.mike_caron.megacorp.impl.CorporationManager;
import com.mike_caron.megacorp.item.ModItems;
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

public class BlockUplink
    extends OwnedMachineBlockBase
{
    public BlockUplink()
    {
        super(Material.IRON, "uplink");


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
        return new TileEntityUplink();
    }

    @Nullable
    private TileEntityUplink getTE(IBlockAccess worldIn, BlockPos pos)
    {
        TileEntity ret = worldIn.getTileEntity(pos);
        if(ret instanceof TileEntityUplink) return (TileEntityUplink) ret;
        return null;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ))
            return true;

        if(worldIn.isRemote)
            return true;

        TileEntityUplink te = getTE(worldIn, pos);

        if(te == null)
            return false;

        ItemStack inHand = playerIn.inventory.getCurrentItem();
        if(inHand.getItem() == ModItems.blackCard)
        {
            ICorporationManager manager = CorporationManager.get(te.getWorld());
            if(manager.ownerHasCorporation(te.getOwner()))
            {
                Corporation corp = (Corporation)manager.getCorporationForOwner(te.getOwner());
                corp.addProfit(1000);
            }
            return true;
        }

        playerIn.openGui(MegaCorpMod.instance, 2, worldIn, pos.getX(), pos.getY(), pos.getZ());

        return true;
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data)
    {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);

        TileEntityUplink te = getTE(world, data.getPos());

        if(te == null) return;


    }

    @Override
    public void getExtraDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state)
    {
        super.getExtraDrops(drops, world, pos, state);

        TileEntityUplink te = getTE(world, pos);

        if(te == null) return;

        for(int i = 0; i < te.cardInventory.getSlots(); i++)
        {
            ItemStack stack = te.cardInventory.getStackInSlot(i);
            if(stack.isEmpty()) continue;
            drops.add(stack);
        }
    }
}
