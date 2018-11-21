package com.mike_caron.megacorp.reward;

import com.google.gson.JsonObject;
import com.mike_caron.megacorp.api.IReward;
import com.mike_caron.megacorp.api.IRewardFactory;
import org.apache.commons.lang3.math.Fraction;

public class FractionalReward
    extends BaseReward
{
    Fraction baseValue;
    Fraction valuePerRank;

    protected FractionalReward(String id)
    {
        super(id);
    }

    @Override
    public float[] getValuesForRank(int rank)
    {
        Fraction frac = baseValue.add(valuePerRank.multiplyBy(Fraction.getFraction(rank)));

        return new float[] { frac.getNumerator(), frac.getDenominator() };
    }

    @Override
    protected void loadFromJson(JsonObject json)
    {
        super.loadFromJson(json);

        if(json.has("baseValue"))
        {
            baseValue = Fraction.getFraction(json.get("baseValue").getAsString());
        }

        if(json.has("valuePerRank"))
        {
            valuePerRank = Fraction.getFraction(json.get("valuePerRank").getAsString());
        }
        else
        {
            int num = valuePerRank.getNumerator(), den = valuePerRank.getDenominator();

            if(json.has("numPerRank"))
            {
                num = json.get("numPerRank").getAsInt();
            }

            if(json.has("denomPerRank"))
            {
                den = json.get("denomPerRank").getAsInt();
            }

            valuePerRank = Fraction.getFraction(num, den);
        }
    }

    public static class Factory
        implements IRewardFactory
    {
        @Override
        public IReward createReward(String id, JsonObject json)
        {
            FractionalReward ret = new FractionalReward(id);

            ret.loadFromJson(json);

            return ret;
        }

        @Override
        public void updateReward(IReward reward, JsonObject json)
        {
            ((FractionalReward)reward).loadFromJson(json);
        }
    }
}
