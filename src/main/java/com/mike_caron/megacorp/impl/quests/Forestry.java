package com.mike_caron.megacorp.impl.quests;

import com.mike_caron.megacorp.api.IQuestFactory;
import com.mike_caron.megacorp.impl.Quest;
import com.mike_caron.megacorp.impl.QuestLocalization;
import com.mike_caron.megacorp.impl.QuestManager;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Forestry
{

    public static class Tubes
        extends QuestFactories.ModMaterialBase
    {
        @Override
        protected float baseProfit(int value)
        {
            return value * 2;
        }

        public Tubes()
        {
            modprefix = "forestry";
            type = "thermionic_tubes";

            add("Copper", 1, 0);
            add("Tin", 1, 1);
            add("Bronze", 2, 2);
            add("Golden", 4, 4);
            add("Diamantine", 8, 5);
            add("Obsidian", 5, 6);
            add("Blazing", 6, 7);
            add("Emerald", 10, 9);
            add("Apatine", 3, 10);
            add("Lapis", 7, 11);
            add("Ender", 7, 12);
        }
    }

    public static class Bees
        implements IQuestFactory
    {
        public Bees()
        {

        }

        @Override
        public List<Quest> createQuests()
        {
            List<Quest> ret = new ArrayList<>();

            if(BeeManager.beeRoot != null)
            {
                List<ItemStack> items = new ArrayList<>();

                for (IBee bee : BeeManager.beeRoot.getIndividualTemplates())
                {
                    ItemStack is = BeeManager.beeRoot.getMemberStack(bee, EnumBeeType.DRONE);

                    items.add(is);
                }

                ret.add(new Quest(
                    "forestry:bee_drone_ge",
                    items,
                    2,
                    1.5f,
                    1.25f,
                    0.8f,
                    5f
                ));
            }

            return ret;
        }

        @Override
        public QuestLocalization localize(String locale, Quest quest)
        {
            String id = "forestry:bee_drone_geGeneric";

            QuestLocalization localization = QuestManager.INSTANCE.getLocalizationFor(locale, id);
            return localization.withDescription(localization.description);
        }
    }

}
