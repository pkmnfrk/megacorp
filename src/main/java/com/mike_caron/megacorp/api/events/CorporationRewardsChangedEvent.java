package com.mike_caron.megacorp.api.events;

import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.UUID;

public class CorporationRewardsChangedEvent
    extends Event
{
    public final UUID owner;
    public final String rewardId;
    public final int newRank;

    public CorporationRewardsChangedEvent(UUID owner, String rewardId, int newRank)
    {
        this.owner = owner;
        this.rewardId = rewardId;
        this.newRank = newRank;
    }

    @Override
    public boolean isCancelable()
    {
        return false;
    }
}
