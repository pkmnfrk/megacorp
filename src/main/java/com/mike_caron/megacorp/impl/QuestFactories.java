package com.mike_caron.megacorp.impl;

import com.mike_caron.megacorp.api.IQuestFactory;
import com.mike_caron.megacorp.util.OreDictUtil;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

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

        protected String modprefix = "";
        protected String type = "";

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

        protected float baseProfit(int value) { return 1f; }

        @Override
        public List<Quest> createQuests()
        {
            List<Quest> ret = new ArrayList<>(materials.size());

            for(Map.Entry<String, Integer> kvp : materials.entrySet())
            {
                Quest q = new Quest(
                    modprefix + ":" + type + "_" + kvp.getKey(),
                    type + kvp.getKey(),
                    baseQty(kvp.getValue()),
                    multQty(kvp.getValue()),
                    randomFactor,
                    levelScale,
                    baseProfit(kvp.getValue())
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
            String id = modprefix + ":" + type + material;
            if(!QuestManager.INSTANCE.localizationExists(locale, id))
                id = modprefix + ":" + type + "Generic";

            QuestLocalization localization = QuestManager.INSTANCE.getLocalizationFor(locale, id);
            return localization.withDescription(String.format(localization.description, material));
        }
    }

    public static abstract class ThermalFoundationBase
        extends ModMaterialBase
    {

        @Override
        protected float baseProfit(int value)
        {
            return 20f * 11f / value;
        }

        public ThermalFoundationBase()
        {
            modprefix = "thermalfoundation";

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

    public static class EnderioBase
        extends ModMaterialBase
    {

        @Override
        protected float baseProfit(int value)
        {
            return 20f * 4f / value;
        }

        public EnderioBase()
        {
            modprefix = "enderio";

            materials.put("ElectricalSteel", 4);
            materials.put("EnergeticAlloy", 3);
            materials.put("VibrantAlloy", 2);
            materials.put("RedstoneAlloy", 4);
            materials.put("ConductiveIron", 4);
            materials.put("PulsatingIron", 3);
            materials.put("DarkSteel", 2);
            materials.put("Soularium", 2);
            materials.put("EndSteel", 1);
        }
    }

    public static class EnderioIngots
        extends EnderioBase
    {
        public EnderioIngots()
        {
            type = "ingot";
        }
    }

    public static class EnderioBalls
        extends EnderioBase
    {
        public EnderioBalls()
        {
            type = "ball";
        }
    }

    public static abstract class PamsHarvestcraftBase
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
                Quest q = new Quest(
                    is.getItem().getRegistryName().toString(),
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
    }

    public static class PamsHarvestcraftFruit
        extends PamsHarvestcraftBase
    {
        public PamsHarvestcraftFruit()
        {
            dictPrefix = "listAllfruit";
        }
    }

    public static class PamsHarvestcraftVeggies
        extends PamsHarvestcraftBase
    {
        public PamsHarvestcraftVeggies()
        {
            dictPrefix = "listAllveggie";
        }
    }

    public static class PamsHarvestcraftNuts
        extends PamsHarvestcraftBase
    {
        public PamsHarvestcraftNuts()
        {
            dictPrefix = "listAllnut";
        }
    }

    public static class PamsHarvestcraftFood
        extends PamsHarvestcraftBase
    {
        public PamsHarvestcraftFood()
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
