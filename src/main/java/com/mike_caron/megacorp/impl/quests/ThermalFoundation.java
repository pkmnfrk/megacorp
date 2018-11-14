package com.mike_caron.megacorp.impl.quests;

import com.google.gson.JsonObject;

public abstract class ThermalFoundation
    extends QuestFactories.ModMaterialBase
{

    @Override
    protected float baseProfit(JsonObject material)
    {
        return 20f * 11f / material.get("rarity").getAsFloat() / 9;
    }

    public ThermalFoundation()
    {
        modprefix = "thermalfoundation";

        levelScale = 0.75f;
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
        protected float baseQty(JsonObject material)
        {
            return super.baseQty(material) * 4;
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
        protected float baseQty(JsonObject material)
        {
            return 1.5f;
        }

        @Override
        protected float baseProfit(JsonObject material)
        {
            return super.baseProfit(material) * 1f;
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
        protected float baseQty(JsonObject material)
        {
            return super.baseQty(material) * 3;
        }

        @Override
        protected float baseProfit(JsonObject material)
        {
            return super.baseProfit(material) * 0.5f;
        }
    }

    public static class Balls
        extends ThermalFoundation
    {
        public Balls()
        {
            type = "ball";
        }

        @Override
        protected float baseProfit(JsonObject material)
        {
            return super.baseProfit(material) * 0.1f;
        }
    }
}
