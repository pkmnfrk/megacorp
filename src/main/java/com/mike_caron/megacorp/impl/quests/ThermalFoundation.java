package com.mike_caron.megacorp.impl.quests;

public abstract class ThermalFoundation
    extends QuestFactories.ModMaterialBase
{

    @Override
    protected float baseProfit(int value)
    {
        return 20f * 11f / value / 9;
    }



    public ThermalFoundation()
    {
        modprefix = "thermalfoundation";

        levelScale = 0.75f;

        add("Iridium", 1);
        add("Platinum", 1);
        add("Enderium", 2);
        add("Mana_infused", 3);
        add("Signalum", 4);
        add("Lumium", 4);
        add("Steel", 5);
        add("Gold", 5);
        add("Nickel", 6);
        add("Aluminum", 6);
        add("Constantan", 6);
        add("Electrum", 7);
        add("Silver", 7);
        add("Invar", 8);
        add("Bronze", 9);
        add("Lead", 9);
        add("Tin", 10);
        add("Iron", 11);
        add("Copper", 11);
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
            return super.baseQty(value) * 4;
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
            return super.baseQty(value) * 3;
        }

        @Override
        protected float baseProfit(int value)
        {
            return super.baseProfit(value) * 0.5f;
        }
    }
}
