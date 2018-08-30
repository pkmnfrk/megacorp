package com.mike_caron.megacorp.api;

public interface IReward
{
    int numRanks();
    int costForRank(int rank);
    Object[] getValuesForRank(int rank);
}
