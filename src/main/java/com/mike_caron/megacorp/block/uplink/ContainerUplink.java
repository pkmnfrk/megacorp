package com.mike_caron.megacorp.block.uplink;

import com.mike_caron.megacorp.block.TEContainerBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerUplink
        extends TEContainerBase
{
    public ContainerUplink(IInventory playerInventory, TileEntityUplink te)
    {
        super(playerInventory, te);
    }

    private TileEntityUplink getTE()
    {
        return (TileEntityUplink)this.te;
    }

    @Override
    protected int playerInventoryY()
    {
        return 84;
    }

    @Override
    protected int playerInventoryX()
    {
        return 8;
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        TileEntityUplink te = getTE();

    }

    @Override
    protected void onReadNBT(NBTTagCompound tag)
    {

    }

    @Override
    protected void onWriteNBT(NBTTagCompound tag)
    {

    }

    @Override
    public int getId()
    {
        return 2;
    }
}
