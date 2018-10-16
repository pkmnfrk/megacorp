package com.mike_caron.megacorp.impl.quests;

public abstract class ThermalFoundation
    extends QuestFactories.ModMaterialBase
{

    @Override
    protected float baseProfit(int value)
    {
        return 20f * 11f / value;
    }

    public ThermalFoundation()
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

    public static class Ingots
        extends ThermalFoundation
    {
        public Ingots()
        {
            type = "ingot";
        }
    }

    public static class Gears
        extends ThermalFoundation
    {
        public Gears()
        {
            type = "gear";
        }

        @Override
        protected float baseQty(int value)
        {
            return 2f;
        }
    }

    public static class Plates
        extends ThermalFoundation
    {
        public Plates()
        {
            type = "plate";
        }

        @Override
        protected float baseQty(int value)
        {
            return 1.5f;
        }

        @Override
        protected float baseProfit(int value)
        {
            return super.baseProfit(value) * 1f;
        }
    }

    public static class Coins
        extends ThermalFoundation
    {
        public Coins()
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
            return 1.5f;
        }

        @Override
        protected float baseProfit(int value)
        {
            return super.baseProfit(value) * 0.5f;
        }
    }
}
