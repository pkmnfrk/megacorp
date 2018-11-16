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

    @Override
    protected void loadFromJson(JsonObject json)
    {
        super.loadFromJson(json);

        if(json.has("rankValue"))
        {
            rankValue = json.get("rankValue").getAsFloat();
        }

        if(json.has("startValue"))
        {
            startValue = json.get("startValue").getAsFloat();
        }
    }

    public static class Factory
        implements IRewardFactory
    {
        @Override
        public IReward createReward(String id, JsonObject json)
        {
            GenericReward ret = new GenericReward(id);

            ret.loadFromJson(json);

            return ret;
        }

        @Override
        public void updateReward(IReward reward, JsonObject json)
        {
            ((GenericReward)reward).loadFromJson(json);
        }
    }
}
