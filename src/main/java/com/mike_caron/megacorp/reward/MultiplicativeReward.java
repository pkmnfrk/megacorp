package com.mike_caron.megacorp.reward;

import com.google.gson.JsonObject;
import com.mike_caron.megacorp.api.IReward;
import com.mike_caron.megacorp.api.IRewardFactory;

public class MultiplicativeReward
    extends BaseReward
{
    int baseValue = 1;
    float multiplier = 1;

    protected MultiplicativeReward(String id)
    {
        super(id);
    }

    @Override
    public float[] getValuesForRank(int rank)
    {
        float value = baseValue * (float)Math.pow(multiplier, rank - 1);

        return new float[] { value };
    }

    @Override
    protected void loadFromJson(JsonObject json)
    {
        super.loadFromJson(json);

        if(json.has("baseValue"))
        {
            baseValue = json.get("baseValue").getAsInt();
        }

        if(json.has("multiplier"))
        {
            multiplier = json.get("multiplier").getAsFloat();
        }
    }

    public static class Factory
        implements IRewardFactory
    {
        @Override
        public IReward createReward(String id, JsonObject json)
        {
            MultiplicativeReward ret = new MultiplicativeReward(id);

            ret.loadFromJson(json);

            return ret;
        }

        @Override
        public void updateReward(IReward reward, JsonObject json)
        {
            ((MultiplicativeReward)reward).loadFromJson(json);
        }
    }
}
