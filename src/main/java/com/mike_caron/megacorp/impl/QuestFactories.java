package com.mike_caron.megacorp.impl;

import com.mike_caron.megacorp.api.IQuestFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class QuestFactories
{
    private QuestFactories(){}

    public static class ThermalFoundationBase
        implements IQuestFactory
    {
        private static final Map<String, Integer> materials = new HashMap<>();
        static {
            materials.put("Iridium", 1);
            materials.put("Platinum", 1);
            materials.put("Enderium", 2);
            //materials.put("Mana_infused", 3);
            materials.put("Signalum", 4);
            materials.put("Lumium", 4);
            materials.put("Steel", 5);
            materials.put("Gold", 5);
            materials.put("Nickel", 6);
            materials.put("Aluminum", 6);
            materials.put("Constantan", 6);
            materials.put("Electrum", 7);
            materials.put("Silver", 7);
            materials.put("Invar", 8);
            materials.put("Bronze", 9);
            materials.put("Lead", 9);
            materials.put("Tin", 10);
            materials.put("Iron", 11);
            materials.put("Copper", 11);
        }

        protected String type;

        private float randomFactor = 0.25f;
        private float levelScale = 0.9f;

        protected float baseQty(int value)
        {
            return (float)value;
        }

        protected float multQty(int value)
        {
            return 1.5f;
        }



        @Override
        public List<Quest> createQuests()
        {
            //return null;
            List<Quest> ret = new ArrayList<>(materials.size());

            for(Map.Entry<String, Integer> kvp : materials.entrySet())
            {
                //if(kvp.getKey().equals("Iron") || kvp.getKey().equals("Gold"))
                //    continue;

                Quest q = new Quest(
                    "thermalfoundation:" + type + "_" + kvp.getKey(),
                    type + kvp.getKey(),
                    baseQty(kvp.getValue()),
                    multQty(kvp.getValue()),
                    randomFactor,
                    levelScale
                );

                q.extraData.put("material", kvp.getKey());

                ret.add(q);
            }

            return ret;
        }

        @Override
        public QuestLocalization localize(String locale, Quest quest)
        {
            String material = (String)quest.extraData.get("material");
            String id = "thermalfoundation:" + type + material;
            if(!QuestManager.INSTANCE.localizationExists(locale, id))
                id = "thermalfoundation:" + type + "Generic";

            QuestLocalization localization = QuestManager.INSTANCE.getLocalizationFor(locale, id);
            return localization.withDescription(String.format(localization.description, material));
        }
    }

    public static class ThermalFoundationIngots
    extends ThermalFoundationBase
    {
        public ThermalFoundationIngots()
        {
            type = "ingot";
        }
    }

    public static class ThermalFoundationGears
        extends ThermalFoundationBase
    {
        public ThermalFoundationGears()
        {
            type = "gear";
        }

        @Override
        protected float baseQty(int value)
        {
            return 2f;
        }
    }

    public static class ThermalFoundationPlates
        extends ThermalFoundationBase
    {
        public ThermalFoundationPlates()
        {
            type = "plate";
        }

        @Override
        protected float baseQty(int value)
        {
            return Math.max(1, value * 8f / 11);
        }
    }

    public static class ThermalFoundationCoins
        extends ThermalFoundationBase
    {
        public ThermalFoundationCoins()
        {
            type = "coin";
        }

        @Override
        protected float baseQty(int value)
        {
            return 3;
        }

        @Override
        protected float multQty(int value)
        {
            return Math.max(1.5f, value * 4f / 11f);
        }
    }
}
