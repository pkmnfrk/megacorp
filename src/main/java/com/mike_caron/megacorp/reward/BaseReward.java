package com.mike_caron.megacorp.reward;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.mike_caron.megacorp.api.IReward;
import com.mike_caron.megacorp.util.DataUtils;

public abstract class BaseReward implements IReward
{
    String factoryClass;
    String id;
    int numRanks;
    CostScale costScale;
    float costScaleFactor;
    int firstCost;
    CurrencyType currencyType = CurrencyType.MONEY;
    String[][] gameStages;

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
    public abstract float[] getValuesForRank(int rank);

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
        if(json.has("ranks"))
        {
            this.numRanks = json.get("ranks").getAsInt();
        }

        if(json.has("firstCost"))
        {
            this.firstCost = json.get("firstCost").getAsInt();
        }

        if(json.has("costScale"))
        {
            this.costScale = CostScale.valueOf(json.get("costScale").getAsString().toUpperCase());
        }

        if(json.has("costScaleFactor"))
        {
            this.costScaleFactor = json.get("costScaleFactor").getAsFloat();
        }

        if(json.has("currency"))
        {
            this.currencyType = CurrencyType.valueOf(json.get("currency").getAsString().toUpperCase());
        }

        if(json.has("game_stages"))
        {
            this.gameStages = DataUtils.loadJsonNestedArray(json.get("game_stages"));
        }
    }

    public String getFactoryClass()
    {
        return factoryClass;
    }

    public void setFactoryClass(String clazz)
    {
        this.factoryClass = clazz;
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
