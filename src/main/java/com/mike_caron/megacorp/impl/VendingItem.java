package com.mike_caron.megacorp.impl;

import com.mike_caron.megacorp.reward.BaseReward;
import com.mike_caron.mikesmodslib.util.StringUtil;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VendingItem
{
    public final ItemStack itemStack;
    public final int cost;
    public final BaseReward.CurrencyType currency;
    public final String id;
    public final String[][] stagesRequired;

    public VendingItem(@Nonnull ItemStack itemStack, int cost, BaseReward.CurrencyType currency, @Nullable String[][] stagesRequired)
    {
        this.itemStack = itemStack;
        this.cost = cost;
        this.currency = currency;
        this.id = StringUtil.randomString(8);
        this.stagesRequired = stagesRequired;
    }

    public static class Comparator
        implements java.util.Comparator<VendingItem>
    {
        @Override
        public int compare(VendingItem o1, VendingItem o2)
        {
            if(o1.currency == BaseReward.CurrencyType.MONEY && o2.currency == BaseReward.CurrencyType.DENSE_MONEY)
                return -1;
            if(o1.currency == BaseReward.CurrencyType.DENSE_MONEY && o2.currency == BaseReward.CurrencyType.MONEY)
                return 1;

            int ir = Integer.compare(o1.cost, o2.cost);

            if(ir == 0)
            {
                return o1.itemStack.getDisplayName().compareTo(o2.itemStack.getDisplayName());
            }

            return ir;
        }
    }
}
