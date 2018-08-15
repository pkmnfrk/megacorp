package com.mike_caron.megacorp.item;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class DebugItemBase
    extends ItemBase
{
    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return EnumRarity.EPIC;
    }

    @Override
    public boolean hasEffect(ItemStack stack)
    {
        return true;
    }
}
