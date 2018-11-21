package com.mike_caron.megacorp;

import com.mike_caron.megacorp.block.ModBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTab extends CreativeTabs
{
    public CreativeTab()
    {
        super(MegaCorpMod.modId);
    }

    @Override
    public ItemStack createIcon()
    {
        return new ItemStack(ModBlocks.uplink, 1);
    }
}
