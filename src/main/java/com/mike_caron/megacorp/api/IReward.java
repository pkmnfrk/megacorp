package com.mike_caron.megacorp.api;

import com.mike_caron.megacorp.reward.BaseReward;

public interface IReward
{
    String getFactoryClass();
    void setFactoryClass(String clazz);
    String getId();
    int numRanks();
    int costForRank(int rank);
    float[] getValuesForRank(int rank);
    BaseReward.CurrencyType getCurrency();
    String[][] getGameStages();
    boolean getOwnerOnly();
}
