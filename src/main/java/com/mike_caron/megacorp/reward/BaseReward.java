package com.mike_caron.megacorp.reward;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.mike_caron.megacorp.api.IReward;

public abstract class BaseReward implements IReward
{
    String id;
    int numRanks;
    CostScale costScale;
    float costScaleFactor;
    int firstCost;
    CurrencyType currencyType = CurrencyType.MONEY;

    protected BaseReward(String id)
    {
        this.id = id;
    }

    @Override
    public int numRanks()
    {
        return numRanks;
    }

    @Override
    public int costForRank(int rank)
    {
        Preconditions.checkArgument(rank > 0 && rank <= numRanks);

        float finalCost = 1000;

        if(costScale == CostScale.ADD)
        {
            finalCost = firstCost + (rank - 1) * costScaleFactor;
        }
        else if(costScale == CostScale.MULT)
        {
            finalCost = firstCost * (float)Math.pow(costScaleFactor, rank - 1);
        }

        return (int)finalCost;
    }

    @Override
    public abstract int[] getValuesForRank(int rank);

    public String getId()
    {
        return this.id;
    }

    public CurrencyType getCurrency()
    {
        return this.currencyType;
    }

    protected void loadFromJson(JsonObject json)
    {
        this.numRanks = json.get("ranks").getAsInt();
        this.firstCost = json.get("firstCost").getAsInt();
        this.costScale = CostScale.valueOf(json.get("costScale").getAsString().toUpperCase());
        this.costScaleFactor = json.get("costScaleFactor").getAsFloat();
        if(json.has("currency"))
        {
            this.currencyType = CurrencyType.valueOf(json.get("currency").getAsString().toUpperCase());
        }
    }

    public enum CostScale
    {
        MULT,
        ADD
    }

    public enum CurrencyType
    {
        MONEY,
        DENSE_MONEY
    }
}
