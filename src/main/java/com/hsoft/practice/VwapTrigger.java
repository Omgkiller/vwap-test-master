package com.hsoft.practice;


import com.hsoft.api.MarketDataListener;
import com.hsoft.api.PricingDataListener;
import com.hsoft.api.VwapTriggerListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Entry point for the candidate to resolve the exercise
 */
public class VwapTrigger implements PricingDataListener, MarketDataListener {

  private final VwapTriggerListener vwapTriggerListener;
  private Map<String, VwapQueue> pricingDataMap = new HashMap<>();
  private Map<String, Double> fairValueMap = new HashMap<>();
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
    VwapQueue.PricingData pricingData = new VwapQueue.PricingData(quantity, price);
    pricingDataMap.computeIfAbsent(productId, v -> new VwapQueue(new ConcurrentLinkedQueue<>())).add(pricingData);
    triggerVwapEvent(productId);
  }

  @Override
  public void fairValueChanged(String productId, double fairValue) {
    fairValueMap.put(productId, fairValue);
    triggerVwapEvent(productId);
  }

  private void triggerVwapEvent(String productId) {
    VwapQueue vwapQueue = pricingDataMap.get(productId);
    double vwap = 0.0;
    if (vwapQueue != null)
    {
      vwap = vwapQueue.getVwap();
    }
    double fairValue = Optional.ofNullable(fairValueMap.get(productId)).orElse(0.0);
    if (vwap > fairValue)
    {
      this.vwapTriggerListener.vwapTriggered(productId, vwap, fairValue);
    }
  }
}