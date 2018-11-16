package com.mike_caron.megacorp.reward;

import com.google.gson.JsonObject;
import com.mike_caron.megacorp.api.IReward;
import com.mike_caron.megacorp.api.IRewardFactory;
import com.mike_caron.megacorp.util.DataUtils;

public class CommandReward
    extends BaseReward
{
    public String[] commands;

    protected CommandReward(String id)
    {
        super(id);
    }

    @Override
    public float[] getValuesForRank(int rank)
    {
        return new float[0];
    }

    @Override
    protected void loadFromJson(JsonObject json)
    {
        super.loadFromJson(json);

        if(json.has("commands"))
        {
            commands = DataUtils.loadJsonArray(json.get("commands"));
        }
    }

    public static class Factory
        implements IRewardFactory
    {
        @Override
        public IReward createReward(String id, JsonObject json)
        {
            CommandReward ret = new CommandReward(id);

            ret.loadFromJson(json);

            return ret;
        }



        @Override
        public void updateReward(IReward reward, JsonObject json)
        {
            ((CommandReward)reward).loadFromJson(json);
        }
    }
}
