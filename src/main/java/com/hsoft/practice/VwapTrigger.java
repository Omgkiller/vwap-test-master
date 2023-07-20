package com.hsoft.practice;


import com.hsoft.api.MarketDataListener;
import com.hsoft.api.PricingDataListener;
import com.hsoft.api.VwapTriggerListener;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Entry point for the candidate to resolve the exercise
 */
public class VwapTrigger implements PricingDataListener, MarketDataListener {
    private final VwapTriggerListener vwapTriggerListener;
    private final Map<String, MarketDataQueue> pricingDataMap = new ConcurrentHashMap<>();
    private static final int ROLLING_WINDOW_CAPACITY = 5;

    /**
     * This constructor is mainly available to ease unit test by not having to provide a VwapTriggerListener
     */
    protected VwapTrigger () {
        this.vwapTriggerListener = (productId, vwap, fairValue) -> {
            // ignore
        };
    }

    public VwapTrigger (VwapTriggerListener vwapTriggerListener) {
        this.vwapTriggerListener = vwapTriggerListener;
    }

    @Override
    public void transactionOccurred (String productId, long quantity, double price) {
        MarketDataQueue marketDataQueue = getVwapQueue(productId);
        marketDataQueue.add(new MarketDataQueue.PricingData(quantity, price));
        double fairValue = marketDataQueue.getFairValue();
        triggerVwapEvent(productId, marketDataQueue.getVwap(), fairValue);
    }

    @Override
    public void fairValueChanged (String productId, double fairValue) {
        MarketDataQueue marketDataQueue = getVwapQueue(productId);
        marketDataQueue.setFairValue(fairValue);
        double vwap = marketDataQueue.getVwap();
        triggerVwapEvent(productId, vwap, fairValue);
    }

    private MarketDataQueue getVwapQueue (String productId) {
        return pricingDataMap.computeIfAbsent(productId, v -> new MarketDataQueue(new LinkedList<>(), ROLLING_WINDOW_CAPACITY));
    }

    private void triggerVwapEvent (String productId, double vwap, double fairValue) {
        if (vwap > fairValue) {
            this.vwapTriggerListener.vwapTriggered(productId, vwap, fairValue);
        }
    }
}