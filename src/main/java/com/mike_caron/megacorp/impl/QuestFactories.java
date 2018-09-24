package com.mike_caron.megacorp.impl;

import com.mike_caron.megacorp.api.IQuestFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class QuestFactories
{
    private QuestFactories(){}

    public static class ThermalFoundation
        implements IQuestFactory
    {
        private static final Map<String, Double> materials = new HashMap<>();

        static {
            materials.put("Iron", 4.0);
            materials.put("Gold", 2.0);
            materials.put("Copper", 4.0);
            materials.put("Tin", 3.5);
            materials.put("Silver", 2.5);
            materials.put("Lead", 3.0);
            materials.put("Aluminum", 2.0);
            materials.put("Nickel", 1.5);
            materials.put("Iridium", 0.25);
            materials.put("Platinum", 0.25);
            //materials.put("Mana_infused", 1.0);
            materials.put("Steel", 1.5);
            materials.put("Electrum", 2.0);
            materials.put("Invar", 2.5);
            materials.put("Bronze", 3.0);
            materials.put("Constantan", 2.0);
            materials.put("Signalum", 1.5);
            materials.put("Lumium", 1.5);
            materials.put("Enderium", 1.0);
        }

        @Override
        public List<Quest> createQuests()
        {
            //return null;
            List<Quest> ret = new ArrayList<>(materials.size() * 4);

            for(Map.Entry<String, Double> kvp : materials.entrySet())
            {
                ret.add(new Quest(
                    "thermalfoundation:ingot_" + kvp.getKey(),
                    "ingot" + kvp.getKey(),
                    kvp.getValue().floatValue(),
                    1f,
                    0.25f,
                    0.9f
                ));

                ret.add(new Quest(
                    "thermalfoundation:gear_" + kvp.getKey(),
                    "gear" + kvp.getKey(),
                    kvp.getValue().floatValue(),
                    0.25f,
                    0.25f,
                    0.9f
                ));

                ret.add(new Quest(
                    "thermalfoundation:plate_" + kvp.getKey(),
                    "plate" + kvp.getKey(),
                    kvp.getValue().floatValue(),
                    0.9f,
                    0.25f,
                    0.9f
                ));

                ret.add(new Quest(
                    "thermalfoundation:coin_" + kvp.getKey(),
                    "coin" + kvp.getKey(),
                    kvp.getValue().floatValue(),
                    4f,
                    0.5f,
                    0.9f
                ));
            }

            return ret;
        }
    }
}
