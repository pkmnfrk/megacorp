package com.mike_caron.megacorp.impl.quests;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.api.IQuestFactory;
import com.mike_caron.megacorp.impl.Quest;
import com.mike_caron.megacorp.impl.QuestLocalization;
import com.mike_caron.megacorp.impl.QuestManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class QuestFactories
{
    private QuestFactories(){}

    public static abstract class ModMaterialBase
        implements IQuestFactory
    {
        protected final Map<String, Integer> materials = new HashMap<>();
        protected final Map<String, String> names = new HashMap<>();
        protected final Map<String, Integer> metas = new HashMap<>();

        protected String modprefix = "";
        protected String type = "";

        private float randomFactor = 0.25f;
        protected float levelScale = 0.9f;

        protected float baseQty(int value)
        {
            return (float)value;
        }

        protected float multQty(int value)
        {
            return 1.5f;
        }

        protected float baseProfit(int value) { return 1f; }

        @Override
        public List<Quest> createQuests()
        {
            List<Quest> ret = new ArrayList<>(materials.size());

            for(Map.Entry<String, Integer> kvp : materials.entrySet())
            {
                Quest q;
                if(metas.containsKey(kvp.getKey()))
                {
                    Item item = Item.getByNameOrId(modprefix + ":" + type);
                    if(item == null)
                    {
                        MegaCorpMod.logger.error("Item " + modprefix + ":" + type + " cannot be found");
                        continue;
                    }

                    ItemStack is = new ItemStack(item, 1, metas.get(kvp.getKey()));
                    q = new Quest(
                        modprefix + ":" + type + "_" + kvp.getKey(),
                        is,
                        baseQty(kvp.getValue()),
                        multQty(kvp.getValue()),
                        randomFactor,
                        levelScale,
                        baseProfit(kvp.getValue())
                    );
                }
                else
                {
                    q = new Quest(
                        modprefix + ":" + type + "_" + kvp.getKey(),
                        type + kvp.getKey(),
                        baseQty(kvp.getValue()),
                        multQty(kvp.getValue()),
                        randomFactor,
                        levelScale,
                        baseProfit(kvp.getValue())
                    );
                }
                q.extraData.put("material", names.get(kvp.getKey()));

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

        protected void add(String material, int value, String name, int meta)
        {
            materials.put(material, value);
            names.put(material, name);
            metas.put(material, meta);
        }

        protected void add(String material, int value)
        {
            add(material, value, material);
        }

        protected void add(String material, int value, String name)
        {
            materials.put(material, value);
            names.put(material, name);
        }

        protected void add(String material, int value, int meta)
        {
            add(material, value, material, meta);
        }
    }


}
