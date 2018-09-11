package com.mike_caron.megacorp.item;

import com.mike_caron.megacorp.MegaCorpMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class Manual extends ItemBase
{
    public Manual()
    {
        super();

        setRegistryName("manual");
        setTranslationKey("megacorp:manual");
        setMaxStackSize(1);

        setCreativeTab(MegaCorpMod.creativeTab);

    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, @Nonnull EnumHand handIn)
    {
        player.openGui(MegaCorpMod.instance, 100, worldIn, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
        return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(handIn));
    }

}
