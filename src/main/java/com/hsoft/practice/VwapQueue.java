package com.hsoft.practice;

import java.util.Queue;

 class VwapQueue
{
    private double vwap = 0.0;
    private static final int capacity = 5;
    private Queue<PricingData> vwapQueue;

    VwapQueue(Queue<PricingData> vwapQueue)
    {
        this.vwapQueue = vwapQueue;
    }

    boolean add(PricingData pricingData)
    {
        if (vwapQueue.size() >= capacity)
        {
            vwapQueue.remove();
        }
        boolean canAdd = vwapQueue.add(pricingData);
        this.vwap = calVwap();
        return canAdd;
    }

    public double getVwap() {
        return vwap;
    }

    private double calVwap()
    {
        double vwp = vwapQueue.stream().map(pricingData -> pricingData.price * pricingData.quantity).reduce(Double::sum).orElse(0.0);
        double quantitySum =  vwapQueue.stream().map(pricingData -> pricingData.quantity).reduce(Double::sum).orElse(1.0);
        return vwp/quantitySum;
    }

    static class PricingData
    {
        private double price;
        private double quantity;

        PricingData(long quantity, double price)
        {
            this.price = price;
            this.quantity = quantity;
        }
    }
}

