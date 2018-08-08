package com.mike_caron.megacorp.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IGuiUpdater extends INBTSerializable<NBTTagCompound>
{
    int getId();
    void setGuiListener(Runnable onUpdate);
}
