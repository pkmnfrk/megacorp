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
    public int[] getValuesForRank(int rank)
    {
        Fraction frac = baseValue.add(valuePerRank.multiplyBy(Fraction.getFraction(rank)));

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

            if(json.has("valuePerRank"))
            {
                ret.valuePerRank = Fraction.getFraction(json.get("valuePerRank").getAsString());
            }
            else
            {
                int num = 1, den = 1;

                if(json.has("numPerRank"))
                {
                    num = json.get("numPerRank").getAsInt();
                }

                if(json.has("denomPerRank"))
                {
                    den = json.get("denomPerRank").getAsInt();
                }

                ret.valuePerRank = Fraction.getFraction(num, den);
            }



            return ret;
        }
    }
}
