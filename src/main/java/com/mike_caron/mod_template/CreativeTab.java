package com.mike_caron.mod_template;

import com.mike_caron.mod_template.item.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTab extends CreativeTabs
{
    public CreativeTab()
    {
        super(ModTemplateMod.modId);
    }

    @Override
    public ItemStack getTabIconItem()
    {
        //return new ItemStack(ModItems.efficiencyCatalyst, 1);
        throw new RuntimeException("Not implemented: an icon for the creative tab");
    }
}
