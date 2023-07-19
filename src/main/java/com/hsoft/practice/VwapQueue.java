package com.hsoft.practice;

import java.util.Queue;

 class VwapQueue
{
    private double vwap = 0.0;
    private final int rollingWindowCapacity;
    private final Queue<PricingData> vwapQueue;

    VwapQueue(Queue<PricingData> vwapQueue, int rollingWindowCapacity)
    {
        this.vwapQueue = vwapQueue;
        this.rollingWindowCapacity = rollingWindowCapacity;
    }

    synchronized boolean add(PricingData pricingData) {
        if (vwapQueue.size() >= rollingWindowCapacity)
        {
            vwapQueue.poll();
        }
        boolean canAdd = vwapQueue.add(pricingData);
        recalculateVwap();
        return canAdd;
    }

    public double getVwap() {
        return vwap;
    }

    private void recalculateVwap() {
        double sumPriceQuantity = 0.0;
        double sumQuantity = 0.0;
        for (PricingData data : vwapQueue) {
            sumPriceQuantity += data.price * data.quantity;
            sumQuantity += data.quantity;
        }
        vwap = sumPriceQuantity / sumQuantity;
    }

    static class PricingData
    {
        private final double price;
        private final double quantity;

        PricingData(long quantity, double price)
        {
            this.price = price;
            this.quantity = quantity;
        }
    }
}

