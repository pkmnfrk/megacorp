package com.mike_caron.megacorp.api;

import com.mike_caron.megacorp.impl.WorkOrder;

import java.util.UUID;

public interface ICorporation
{
    UUID getOwner();
    String getName();
    long getAvailableProfit();
    long getTotalProfit();
    int consumeProfit(int amount);

    boolean completeWorkOrder(WorkOrder workOrder);
}
