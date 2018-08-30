package com.mike_caron.megacorp.reward;

import com.google.gson.JsonObject;
import com.mike_caron.megacorp.api.IReward;
import com.mike_caron.megacorp.api.IRewardFactory;
import org.apache.commons.lang3.math.Fraction;

public class FasterGeneration
    implements IReward
{
    @Override
    public int numRanks()
    {
        return 10;
    }

    @Override
    public Object[] getValuesForRank(int rank)
    {
        Fraction frac = Fraction.getReducedFraction(rank + 2, 8);

        return new Object[] { frac.getNumerator(), frac.getDenominator() };
    }

    @Override
    public int costForRank(int rank)
    {
        return 500 * rank;
    }

    public static class Factory
        implements IRewardFactory
    {
        @Override
        public IReward createReward(JsonObject json)
        {
            FasterGeneration ret = new FasterGeneration();
            return ret;
        }
    }
}
