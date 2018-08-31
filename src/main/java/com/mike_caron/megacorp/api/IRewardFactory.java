package com.mike_caron.megacorp.api;

import com.google.gson.JsonObject;

public interface IRewardFactory
{
    IReward createReward(String id, JsonObject json);
}
