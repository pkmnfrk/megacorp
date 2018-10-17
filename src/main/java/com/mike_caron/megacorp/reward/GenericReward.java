package com.mike_caron.megacorp.reward;

import com.google.gson.JsonObject;
import com.mike_caron.megacorp.api.IReward;
import com.mike_caron.megacorp.api.IRewardFactory;

public class GenericReward extends BaseReward
{
    float startValue = 0;
    float rankValue;

    protected GenericReward(String id)
    {
        super(id);
    }

    @Override
    public float[] getValuesForRank(int rank)
    {
        return new float[] { startValue + rankValue * rank };
    }

    public static class Factory
        implements IRewardFactory
    {
        @Override
        public IReward createReward(String id, JsonObject json)
        {
            GenericReward ret = new GenericReward(id);

            ret.loadFromJson(json);

            ret.rankValue = json.get("rankValue").getAsFloat();
            if(json.has("startValue"))
            {
                ret.startValue = json.get("startValue").getAsFloat();
            }
            return ret;
        }
    }
}
