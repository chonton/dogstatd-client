package org.honton.chas.dogstatd.model;

import org.junit.Test;

public class MetricTest {

  @Test(expected = NullPointerException.class)
  public void testNameNull() {
    new Metric<String>(null, "value", 'x') {};
  }

  @Test(expected = NullPointerException.class)
  public void testValueNull() {
    new Metric<String>("name", null, 'x') {};
  }
}
