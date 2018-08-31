package com.mike_caron.megacorp.reward;

import com.google.gson.JsonObject;
import com.mike_caron.megacorp.api.IReward;
import com.mike_caron.megacorp.api.IRewardFactory;

public class GenericReward extends BaseReward
{
    int rankValue;

    protected GenericReward(String id)
    {
        super(id);
    }

    @Override
    public int[] getValuesForRank(int rank)
    {
        return new int[] { rankValue * rank };
    }

    public static class Factory
        implements IRewardFactory
    {
        @Override
        public IReward createReward(String id, JsonObject json)
        {
            GenericReward ret = new GenericReward(id);

            ret.loadFromJson(json);

            ret.rankValue = json.get("rankValue").getAsInt();

            return ret;
        }
    }
}
