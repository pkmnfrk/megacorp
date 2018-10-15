package com.mike_caron.megacorp.reward;

public class PlayerRewards
    implements IPlayerRewards
{
    private int hungerRestore;
    private int damageTimer;
    private int healthRestore;

    public int getDamageTimer()
    {
        return damageTimer;
    }

    public void setDamageTimer(int damageTimer)
    {
        this.damageTimer = damageTimer;
    }

    @Override
    public int getHungerRestore()
    {
        return hungerRestore;
    }

    @Override
    public void setHungerRestore(int ticks)
    {
        hungerRestore = ticks;
    }

    @Override
    public int getHealthRestore()
    {
        return healthRestore;
    }

    @Override
    public void setHealthRestore(int ticks)
    {
        healthRestore = ticks;
    }
}
