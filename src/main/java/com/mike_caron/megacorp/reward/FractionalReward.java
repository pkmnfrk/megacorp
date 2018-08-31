package com.mike_caron.megacorp.reward;

import com.google.gson.JsonObject;
import com.mike_caron.megacorp.api.IReward;
import com.mike_caron.megacorp.api.IRewardFactory;
import org.apache.commons.lang3.math.Fraction;

public class FractionalReward
    extends BaseReward
{
    Fraction baseValue;
    int numeratorPerRank;
    int denominatorPerRank;

    protected FractionalReward(String id)
    {
        super(id);
    }

    @Override
    public int[] getValuesForRank(int rank)
    {
        Fraction frac = Fraction.getReducedFraction(rank + 2, 8);

        return new int[] { frac.getNumerator(), frac.getDenominator() };
    }

    public static class Factory
        implements IRewardFactory
    {
        @Override
        public IReward createReward(String id, JsonObject json)
        {
            FractionalReward ret = new FractionalReward(id);

            ret.loadFromJson(json);

            if(json.has("baseValue"))
            {
                ret.baseValue = Fraction.getFraction(json.get("baseValue").getAsString());
            }

            if(json.has("numPerRank"))
            {
                ret.numeratorPerRank = json.get("numPerRank").getAsInt();
            }

            if(json.has("denomPerRank"))
            {
                ret.denominatorPerRank = json.get("denomPerRank").getAsInt();
            }

            return ret;
        }
    }
}
