package com.hsoft.practice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * One can add own unit tests here and/or in another class
 */
public class VwapTriggerTest {

  private static final double EPSILON = 0.0001;

  @Test
  void vwap_with_0_pricing_data_should_return_0() {
    MarketDataQueue.PricingData pricingData = new MarketDataQueue.PricingData(0, 3.0);
    MarketDataQueue sut = new MarketDataQueue(new LinkedList<>(), 5);

    sut.add(pricingData);
    double vwap = sut.getVwap();

    assertEquals(0, vwap, EPSILON);
  }

  @Test
  void vwap_with_size_1_should_return_work_normal() {
    MarketDataQueue.PricingData pricingData = new MarketDataQueue.PricingData(2, 3.0);
    MarketDataQueue.PricingData pricingData2 = new MarketDataQueue.PricingData(2, -5.0);
    MarketDataQueue sut = new MarketDataQueue(new LinkedList<>(), 1);
    sut.add(pricingData);
    sut.add(pricingData2);

    double vwap = sut.getVwap();

    assertEquals(-5, vwap, EPSILON);
  }

  @Test
  void edge_case_of_size_0_should_throw_error() {
    AssertionError exception = assertThrows(AssertionError.class, () -> {
      new MarketDataQueue(new LinkedList<>(), 0);
    });
  }

  @Test
  void vwap_with_negatve_pricing_data_should_return_negative() {
    MarketDataQueue.PricingData pricingData = new MarketDataQueue.PricingData(1000, -10.0);
    MarketDataQueue.PricingData pricingData2 = new MarketDataQueue.PricingData(999, -30.0);
    MarketDataQueue sut = new MarketDataQueue(new LinkedList<>(), 5);
    sut.add(pricingData);
    sut.add(pricingData2);

    double vwap = sut.getVwap();

    assertEquals(-19.9949974987437, vwap, EPSILON);
  }
  @Test
  @Timeout(value = 1, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SAME_THREAD)
  void vwap_with_large_pricing_data_should_return_fast() {
    int RollingCapacity = 100000;
    MarketDataQueue sut = new MarketDataQueue(new LinkedList<>(), RollingCapacity);

    for (int i = 1; i<=RollingCapacity; i++)
    {
      MarketDataQueue.PricingData pricingData = new MarketDataQueue.PricingData(i, i*1.5);
      sut.add(pricingData);
    }

    double vwap = sut.getVwap();

    assertEquals(100000.5, vwap, EPSILON);
  }
}
