package com.mike_caron.megacorp.api;

import net.minecraft.world.World;

public class CorporationManager
{
    public static ICorporationManager getInstance(World world)
    {
        return getRealManager(world);
    }

    private static com.mike_caron.megacorp.impl.CorporationManager getRealManager(World world)
    {
        return com.mike_caron.megacorp.impl.CorporationManager.get(world);
    }
}
