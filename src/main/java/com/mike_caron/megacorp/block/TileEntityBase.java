package com.mike_caron.megacorp.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TileEntityBase
        extends TileEntity
{
    protected UUID owner;

    public boolean canInteractWith(EntityPlayer entityPlayer)
    {
        return !isInvalid() && entityPlayer.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D; //8 blocks
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);

        if(owner != null)
        {
            compound.setString("Owner", owner.toString());
        }

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        if(compound.hasKey("Owner"))
        {
            owner = UUID.fromString(compound.getString("Owner"));
        }
        else
        {
            owner = null;
        }
    }

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

    public UUID getOwner()
    {
        return owner;
    }

    public void setOwner(UUID owner)
    {
        this.owner = owner;
        this.markDirty();
    }

    public void handleGuiButton(EntityPlayerMP player, int button) {}
    public void handleGuiString(EntityPlayerMP player, int element, String string) {}
}
