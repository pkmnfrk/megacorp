package com.mike_caron.megacorp.impl.quests;

public class Enderio
    extends QuestFactories.ModMaterialBase
{

    @Override
    protected float baseProfit(int value)
    {
        return 20f * 4f / value;
    }

    public Enderio()
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

    public static class Ingots
        extends Enderio
    {
        public Ingots()
        {
            type = "ingot";
        }
    }

    public static class Balls
        extends Enderio
    {
        public Balls()
        {
            type = "ball";
        }
    }
}
