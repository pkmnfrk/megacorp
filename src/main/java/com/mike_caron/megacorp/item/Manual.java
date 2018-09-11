package com.mike_caron.megacorp.item;

import com.mike_caron.megacorp.MegaCorpMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        player.openGui(MegaCorpMod.instance, 100, worldIn, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
        return EnumActionResult.PASS;
    }
}
