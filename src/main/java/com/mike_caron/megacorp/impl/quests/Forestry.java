package com.mike_caron.megacorp.impl.quests;

import com.google.gson.JsonObject;
import com.mike_caron.megacorp.api.IQuestFactory;
import com.mike_caron.megacorp.impl.Quest;
import com.mike_caron.megacorp.impl.QuestLocalization;
import com.mike_caron.megacorp.impl.QuestManager;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class Forestry
{

    public static class Tubes
        extends QuestFactories.ModMaterialBase
    {
        @Override
        protected float baseProfit(JsonObject material)
        {
            return material.get("rarity").getAsFloat() * 2;
        }

        public Tubes()
        {
            modprefix = "forestry";
            type = "thermionic_tubes";
        }
    }

    public static class Bees
        implements IQuestFactory
    {
        public Bees()
        {

        }

        @Override
        @Nonnull
        public List<Quest> createQuests(@Nullable JsonObject tag)
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
