package com.mike_caron.megacorp.api;

import java.util.UUID;

public interface ICorporationManager
{
    boolean ownerHasCorporation(UUID owner);
    ICorporation getCorporationForOwner(UUID owner);
    ICorporation createCorporation(UUID owner);
}
