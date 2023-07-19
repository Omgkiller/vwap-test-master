package com.hsoft.practice;


import com.hsoft.api.MarketDataListener;
import com.hsoft.api.PricingDataListener;
import com.hsoft.api.VwapTriggerListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Entry point for the candidate to resolve the exercise
 */
public class VwapTrigger implements PricingDataListener, MarketDataListener {

  private final VwapTriggerListener vwapTriggerListener;
  private final Map<String, VwapQueue> pricingDataMap = new ConcurrentHashMap<>();
  private final Map<String, Double> fairValueMap = new ConcurrentHashMap<>();
  private final int RollingWindowCapacity = 5;
  /**
   * This constructor is mainly available to ease unit test by not having to provide a VwapTriggerListener
   */
  protected VwapTrigger() {
    this.vwapTriggerListener = (productId, vwap, fairValue) -> {
      // ignore
    };
  }

  public VwapTrigger(VwapTriggerListener vwapTriggerListener) {
    this.vwapTriggerListener = vwapTriggerListener;
  }

  @Override
  public void transactionOccurred(String productId, long quantity, double price) {
    VwapQueue vwapQueue = pricingDataMap.computeIfAbsent(productId, v -> new VwapQueue(new LinkedList<>(), RollingWindowCapacity));
    vwapQueue.add(new VwapQueue.PricingData(quantity, price));
    double fairValue = fairValueMap.getOrDefault(productId, 0.0);
    triggerVwapEvent(productId, vwapQueue.getVwap(), fairValue);
  }

  @Override
  public void fairValueChanged(String productId, double fairValue) {
    fairValueMap.put(productId, fairValue);
    VwapQueue vwapQueue = pricingDataMap.get(productId);
    double vwap = (vwapQueue != null) ? vwapQueue.getVwap() : 0.0;
    triggerVwapEvent(productId, vwap, fairValue);
  }

  private void triggerVwapEvent(String productId, double vwap, double fairValue) {
    if (vwap > fairValue) {
      this.vwapTriggerListener.vwapTriggered(productId, vwap, fairValue);
    }
  }
}