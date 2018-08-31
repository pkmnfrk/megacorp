package com.mike_caron.megacorp.reward;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.mike_caron.megacorp.api.IReward;
import com.mike_caron.megacorp.api.IRewardFactory;
import org.apache.commons.lang3.math.Fraction;

public class FasterGeneration
    implements IReward
{
    String id;

    public FasterGeneration(String id)
    {
        this.id = id;
    }

    @Override
    public int numRanks()
    {
        return 10;
    }

    @Override
    public Object[] getValuesForRank(int rank)
    {
        Fraction frac = Fraction.getReducedFraction(rank + 2, 8);

        return new Object[]{frac.getNumerator(), frac.getDenominator()};
    }

    @Override
    public int costForRank(int rank)
    {
        Preconditions.checkArgument(rank > 0 && rank <= numRanks());

        return 500 * rank;
    }

    public String getId()
    {
        return this.id;
    }

    public static class Factory
        implements IRewardFactory
    {
        @Override
        public IReward createReward(String id, JsonObject json)
        {
            FasterGeneration ret = new FasterGeneration(id);
            return ret;
        }
    }
}
