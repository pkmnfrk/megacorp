package com.mike_caron.megacorp.impl.quests;

import com.mike_caron.megacorp.api.IQuestFactory;
import com.mike_caron.megacorp.impl.Quest;
import com.mike_caron.megacorp.impl.QuestLocalization;
import com.mike_caron.megacorp.impl.QuestManager;
import com.mike_caron.megacorp.util.OreDictUtil;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public abstract class PamsHarvestcraft
    implements IQuestFactory
{
    protected String dictPrefix;

    protected float baseQty(ItemStack is)
    {
        // this stuff can vary wildly in value, so we'll base it off of its quality

        if(is.getItem() instanceof ItemFood)
        {
            //Let's say the max theoretical is 30
            ItemFood food = (ItemFood)is.getItem();

            float amount = food.getHealAmount(is);
            if(amount > 0)
            {
                //lower = better
                return Math.max(1f, 0.1f + ((float)Math.log(30 - amount) * 3f));
            }
        }

        return 4f;
    }

    protected float multQty(ItemStack is)
    {
        // this stuff can vary wildly in value, so we'll base it off of its quality

        if(is.getItem() instanceof ItemFood)
        {
            //Let's say the max theoretical is 30
            ItemFood food = (ItemFood)is.getItem();

            float amount = food.getHealAmount(is);
            if(amount > 0)
            {
                //lower = better
                return Math.max(1.25f, 0.1f + (float)Math.log(30 - amount) / 1.8f);
            }
        }

        return 1.8f;
    }

    protected float baseProfit(ItemStack is)
    {
        // this stuff can vary wildly in value, so we'll base it off of its quality

        if(is.getItem() instanceof ItemFood)
        {
            //Let's say the max theoretical is 30
            ItemFood food = (ItemFood)is.getItem();

            float amount = food.getHealAmount(is);
            if(amount > 0)
            {
                return (amount + 1) / 3f;
            }
        }

        return 1.8f;
    }

    @Override
    public List<Quest> createQuests()
    {
        List<String> dicts = OreDictUtil.getDictsWithWildcards(dictPrefix);

        NonNullList<ItemStack> allItems = NonNullList.create();

        for(String dict : dicts)
        {
            allItems.addAll(OreDictionary.getOres(dict));
        }

        List<Quest> ret = new ArrayList<>(allItems.size());

        for(ItemStack is : allItems)
        {
            String id = is.getItem().getRegistryName().toString();

            if(is.getMetadata() != 0)
            {
                id += "_" + is.getMetadata();
            }

            Quest q = new Quest(
                id,
                is,
                baseQty(is), //4f
                multQty(is), //1.8f
                0.5f,
                0.9f,
                baseProfit(is)
            );

            q.extraData.put("Name", is.getDisplayName());

            ret.add(q);
        }

        return ret;
    }

    @Override
    public QuestLocalization localize(String locale, Quest quest)
    {
        String material = (String)quest.extraData.get("Name");
        String id = quest.id;
        if(!QuestManager.INSTANCE.localizationExists(locale, id))
            id = "harvestcraft:" + dictPrefix + "Generic";

        QuestLocalization localization = QuestManager.INSTANCE.getLocalizationFor(locale, id);
        return localization.withDescription(String.format(localization.description, material));
    }

    public static class Fruit
        extends PamsHarvestcraft
    {
        public Fruit()
        {
            dictPrefix = "listAllfruit";
        }
    }

    public static class Veggies
        extends PamsHarvestcraft
    {
        public Veggies()
        {
            dictPrefix = "listAllveggie";
        }
    }

    public static class Nuts
        extends PamsHarvestcraft
    {
        public Nuts()
        {
            dictPrefix = "listAllnut";
        }
    }

    public static class Food
        extends PamsHarvestcraft
    {
        public Food()
        {
            dictPrefix = "food";
        }

        @Override
        protected float baseProfit(ItemStack is)
        {
            return super.baseProfit(is) * 2;
        }
    }
}
