package com.mike_caron.megacorp.impl.quests;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.api.IQuestFactory;
import com.mike_caron.megacorp.impl.Quest;
import com.mike_caron.megacorp.impl.QuestLocalization;
import com.mike_caron.megacorp.impl.QuestManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class QuestFactories
{
    private QuestFactories(){}

    public static abstract class ModMaterialBase
        implements IQuestFactory
    {
        protected String modprefix = "";
        protected String type = "";

        private float randomFactor = 0.25f;
        protected float levelScale = 0.9f;

        protected float baseQty(JsonObject material)
        {
            return material.get("rarity").getAsFloat();
        }

        protected float multQty(JsonObject material)
        {
            return 1.5f;
        }

        protected float baseProfit(JsonObject material) { return 1f; }

        @Override
        @Nonnull
        public List<Quest> createQuests(@Nullable JsonObject tag)
        {
            if(tag == null)
                return new ArrayList<>();

            JsonArray materials = tag.getAsJsonArray("materials");

            List<Quest> ret = new ArrayList<>(materials.size());

            for(JsonElement kvp : materials)
            {
                JsonObject material = kvp.getAsJsonObject();

                Quest q;

                if(material.has("meta"))
                {
                    Item item = Item.getByNameOrId(modprefix + ":" + type);
                    if(item == null)
                    {
                        MegaCorpMod.logger.error("Item " + modprefix + ":" + type + " cannot be found");
                        continue;
                    }

                    ItemStack is = new ItemStack(item, 1, material.get("meta").getAsInt());
                    q = new Quest(
                        modprefix + ":" + type + "_" + material.get("id").getAsString(),
                        is,
                        baseQty(material),
                        multQty(material),
                        randomFactor,
                        levelScale,
                        baseProfit(material)
                    );
                }
                else
                {
                    q = new Quest(
                        modprefix + ":" + type + "_" + material.get("id").getAsString(),
                        type + material.get("id").getAsString(),
                        baseQty(material),
                        multQty(material),
                        randomFactor,
                        levelScale,
                        baseProfit(material)
                    );
                }
                q.extraData.put("material", material.get("id").getAsString());

                ret.add(q);
            }

            return ret;
        }

        @Override
        public QuestLocalization localize(String locale, Quest quest)
        {
            String material = (String)quest.extraData.get("material");
            String id = modprefix + ":" + type + material;
            if(!QuestManager.INSTANCE.localizationExists(locale, id))
                id = modprefix + ":" + type + "Generic";

            QuestLocalization localization = QuestManager.INSTANCE.getLocalizationFor(locale, id);
            return localization.withDescription(String.format(localization.description, material));
        }
    }


}
