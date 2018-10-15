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
    public int[] getValuesForRank(int rank)
    {
        int value = (int)(baseValue * (float)Math.pow(multiplier, rank - 1));

        return new int[] { value };
    }

    public static class Factory
        implements IRewardFactory
    {
        @Override
        public IReward createReward(String id, JsonObject json)
        {
            MultiplicativeReward ret = new MultiplicativeReward(id);

            ret.loadFromJson(json);

            if(json.has("baseValue"))
            {
                ret.baseValue = json.get("baseValue").getAsInt();
            }

            if(json.has("multiplier"))
            {
                ret.multiplier = json.get("multiplier").getAsFloat();
            }

            return ret;
        }
    }
}
