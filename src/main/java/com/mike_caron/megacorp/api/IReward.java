package com.mike_caron.megacorp.api;

public interface IReward
{
    String getId();
    int numRanks();
    int costForRank(int rank);
    Object[] getValuesForRank(int rank);
}
