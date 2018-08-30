package com.mike_caron.megacorp.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileEntityBase extends TileEntity
{
    public boolean canInteractWith(EntityPlayer entityPlayer)
    {
        return !isInvalid() && entityPlayer.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D; //8 blocks
    }

    public void handleGuiButton(EntityPlayerMP player, int button,  String extraData) {}

    public void handleGuiString(EntityPlayerMP player, int element, String string) {}

    public void handleGuiToggle(EntityPlayerMP player, int element, boolean bool) {}

    @Nonnull
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new SPacketUpdateTileEntity(getPos(), 1, nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound getUpdateTag()
    {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }
}
