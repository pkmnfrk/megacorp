package com.mike_caron.megacorp.reward;

public class PlayerRewards
    implements IPlayerRewards
{
    private int hungerRestore;
    private int damageTimer;

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
}
