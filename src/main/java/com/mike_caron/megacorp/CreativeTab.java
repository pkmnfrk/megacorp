package com.mike_caron.megacorp;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTab extends CreativeTabs
{
    public CreativeTab()
    {
        super(MegaCorpMod.modId);
    }

    @Override
    public ItemStack getTabIconItem()
    {
        //return new ItemStack(ModItems.efficiencyCatalyst, 1);
        throw new RuntimeException("Not implemented: an icon for the creative tab");
    }
}
