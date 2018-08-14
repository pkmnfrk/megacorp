package com.mike_caron.megacorp.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

public class TileEntityBase extends TileEntity
{
    public boolean canInteractWith(EntityPlayer entityPlayer)
    {
        return !isInvalid() && entityPlayer.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D; //8 blocks
    }

    public void handleGuiButton(EntityPlayerMP player, int button) {}

    public void handleGuiString(EntityPlayerMP player, int element, String string) {}

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
}
