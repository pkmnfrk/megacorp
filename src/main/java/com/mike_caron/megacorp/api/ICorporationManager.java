package com.mike_caron.megacorp.api;

import java.util.UUID;

public interface ICorporationManager
{
    ICorporation getCorporationForOwner(UUID owner);
}
