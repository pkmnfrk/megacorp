package com.mike_caron.megacorp.storage;

import net.minecraft.tileentity.TileEntity;

public interface ChangedListener<T extends TileEntity>
{
    void changed(ChangedEvent<T> event);
}
