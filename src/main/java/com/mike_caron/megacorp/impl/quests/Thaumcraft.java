package com.mike_caron.megacorp.impl.quests;

import com.google.gson.JsonObject;
import com.mike_caron.megacorp.api.IQuestFactory;
import com.mike_caron.megacorp.impl.Quest;
import com.mike_caron.megacorp.impl.QuestLocalization;
import com.mike_caron.megacorp.impl.QuestManager;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.consumables.ItemPhial;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Thaumcraft
    implements IQuestFactory
{
    @Nonnull
    @Override
    public List<Quest> createQuests(@Nullable JsonObject tag)
    {
        List<Quest> ret = new ArrayList<>();

        for(Map.Entry<String, Aspect> aspect : Aspect.aspects.entrySet())
        {
            ItemStack crystal = ThaumcraftApiHelper.makeCrystal(aspect.getValue());
            ItemStack phial = ItemPhial.makePhial(aspect.getValue(), 10);

            ret.add(new Quest(
                "thaumcraft:crystal_essence:" + aspect.getKey(),
                "thaumcraft:crystal_essence:generic",
                crystal,
                4,
                1.25f,
                1.1f,
                1f,
                8f,
                null
            ));

            ret.add(new Quest(
                "thaumcraft:phial:" + aspect.getKey(),
                "thaumcraft:phial:generic",
                phial,
                3,
                1.15f,
                1.4f,
                1f,
                18f,
                null
            ));

        }

        return ret;
    }

    @Override
    public QuestLocalization localize(String locale, Quest quest)
    {
        QuestLocalization localization = QuestManager.INSTANCE.getLocalizationFor(locale, quest.getLangKey());
        return localization;
    }
}
