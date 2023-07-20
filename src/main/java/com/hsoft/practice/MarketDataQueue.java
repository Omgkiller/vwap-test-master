package com.hsoft.practice;

import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

class MarketDataQueue
{
    private final int rollingWindowCapacity;
    private final Queue<PricingData> pricingDataQueue;
    private double vwap = 0.0;
    private double fairValue  = 0;
    private double sumQuantity = 0;
    private double sumPriceQuantity = 0;
    private static final Logger logger = Logger.getLogger(MarketDataQueue.class.getName());
    MarketDataQueue(Queue<PricingData> pricingDataQueue, int rollingWindowCapacity)
    {
        assert (rollingWindowCapacity > 0);
        this.pricingDataQueue = pricingDataQueue;
        this.rollingWindowCapacity = rollingWindowCapacity;
    }

    synchronized boolean add(PricingData pricingData) {
        if (pricingDataQueue.size() >= rollingWindowCapacity)
        {
            poll();
        }
        boolean canAdd = pricingDataQueue.add(pricingData);
        vwap = recalculateVwap(pricingData);
        return canAdd;
    }

    private void poll()
    {
        PricingData firstPricingData = pricingDataQueue.poll();
        if (firstPricingData != null)
        {
            sumQuantity -= firstPricingData.quantity;
            sumPriceQuantity -= firstPricingData.quantity*firstPricingData.price;
        }
    }

    synchronized double getVwap() {
        return vwap;
    }

    synchronized double getFairValue(){
        return fairValue;
    }

    synchronized void setFairValue(double fairValue){
        this.fairValue = fairValue;
    }

    private double recalculateVwap(PricingData newData) {
        double vwap = 0;
        sumPriceQuantity += newData.price * newData.quantity;
        sumQuantity += newData.quantity;
        if (sumQuantity != 0)
        {
            vwap = sumPriceQuantity / sumQuantity;
        }
        else
        {
            logger.log(Level.WARNING, "Invalid quantity sum of 0");
        }
        return vwap;
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

