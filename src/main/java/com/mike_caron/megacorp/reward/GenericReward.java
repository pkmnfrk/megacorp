package com.mike_caron.megacorp.reward;

import com.google.gson.JsonObject;
import com.mike_caron.megacorp.api.IReward;
import com.mike_caron.megacorp.api.IRewardFactory;

public class GenericReward
    implements IReward
{
    int numRanks;
    int rankValue;
    CostScale costScale;
    float costScaleFactor;
    int firstCost;

    private GenericReward(int numRanks, int rankValue, int firstCost, CostScale costScale, float costScaleFactor)
    {
        this.numRanks = numRanks;
        this.rankValue = rankValue;
        this.firstCost = firstCost;
        this.costScale = costScale;
        this.costScaleFactor = costScaleFactor;
    }

    @Override
    public int numRanks()
    {
        return numRanks;
    }

    @Override
    public int costForRank(int rank)
    {
        float finalCost = 1000;

        if(costScale == CostScale.ADD)
        {
            finalCost = firstCost + (rank - 1) * costScaleFactor;
        }
        else if(costScale == CostScale.MULT)
        {
            finalCost = firstCost * (float)Math.pow(costScaleFactor, rank);
        }

        return (int)finalCost;
    }

    @Override
    public Object[] getValuesForRank(int rank)
    {
        return new Object[] { rankValue * rank };
    }

    public enum CostScale
    {
        MULT,
        ADD
    }

    public static class Factory
        implements IRewardFactory
    {
        @Override
        public IReward createReward(JsonObject json)
        {
            int numRanks = json.get("ranks").getAsInt();
            int rankValue = json.get("rankValue").getAsInt();
            int firstCost = json.get("firstCost").getAsInt();
            CostScale costScale = CostScale.valueOf(json.get("costScale").getAsString().toUpperCase());
            float costScaleFactor = json.get("costScaleFactor").getAsFloat();

            return new GenericReward(numRanks, rankValue, firstCost, costScale, costScaleFactor);
        }
    }
}
