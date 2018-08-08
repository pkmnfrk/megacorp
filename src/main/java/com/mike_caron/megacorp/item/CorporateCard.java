package com.mike_caron.megacorp.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class CorporateCard extends ItemBase
{
    public CorporateCard()
    {
        super();

        setRegistryName("corporate_card");
        setUnlocalizedName("megacorp:corporate_card");
        //intentionally not set:
        //setCreativeTab(MegaCorpMod.creativeTab);

    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        String temp = TextFormatting.OBFUSCATED + "unknown" + TextFormatting.RESET;
        tooltip.add(I18n.format("item.megacorp:corporate_card.description", temp));
    }

    public static UUID getOwner(ItemStack stack)
    {
        if(stack.getItem() != ModItems.corporateCard || !stack.hasTagCompound() || !stack.getTagCompound().hasKey("Owner"))
            return null;

        return UUID.fromString(stack.getTagCompound().getString("Owner"));
    }
}
