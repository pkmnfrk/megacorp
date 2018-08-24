package com.mike_caron.megacorp.storage;

import net.minecraft.tileentity.TileEntity;

public class ChangedEvent<T extends TileEntity>
{
    public final T tileEntity;

    public ChangedEvent(T tileEntity)
    {
        this.tileEntity = tileEntity;
    }
}
