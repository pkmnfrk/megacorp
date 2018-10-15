package com.mike_caron.megacorp.reward;

public interface IPlayerRewards
{
    int getHungerRestore();
    void setHungerRestore(int ticks);
    int getHealthRestore();
    void setHealthRestore(int ticks);

    int getDamageTimer();
    void setDamageTimer(int ticks);
}
