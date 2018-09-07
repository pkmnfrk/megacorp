package com.mike_caron.megacorp.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
        setTranslationKey("megacorp:corporate_card");
        setMaxStackSize(1);

        //intentionally not set:
        //setCreativeTab(MegaCorpMod.creativeTab);

    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        UUID owner = getOwner(stack);
        String message;

        if(owner == null)
        {
            message = "item.megacorp:corporate_card.notlinked";
        }
        else
        {
            message = "item.megacorp:corporate_card.linked";
        }

        tooltip.add(I18n.format(message));
    }

    public static ItemStack stackForCorp(UUID owner)
    {
        if(owner == null)
        {
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(ModItems.corporateCard, 1);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("Owner", owner.toString());
        stack.setTagCompound(tag);

        return stack;
    }

    public static UUID getOwner(ItemStack stack)
    {
        if(stack.getItem() != ModItems.corporateCard || !stack.hasTagCompound() || !stack.getTagCompound().hasKey("Owner"))
            return null;

        return UUID.fromString(stack.getTagCompound().getString("Owner"));
    }
}
