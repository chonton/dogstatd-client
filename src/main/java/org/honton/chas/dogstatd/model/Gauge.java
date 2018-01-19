package org.honton.chas.dogstatd.model;

/**
 * A Gauge records a value of a metric at a particular time.
 */
public class Gauge extends Metric<Number> {

  /**
   * Create an Gauge value to be sent to DogStatD.
   * 
   * @param name The name of the gauge.
   * @param value The value of the gauge.
   * @param tags Any additional data about the value.
   */
  public Gauge(String name, Number value, String... tags) {
    super(name, value, 'g', tags);
  }
}
