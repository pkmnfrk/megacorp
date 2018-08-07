package com.mike_caron.megacorp.api;

import java.util.UUID;

public interface ICorporation
{
    UUID getOwner();
    String getName();
    long getAvailableProfit();



}
