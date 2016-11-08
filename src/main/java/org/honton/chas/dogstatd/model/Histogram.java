package org.honton.chas.dogstatd.model;

/**
 * A Histogram tracks the distribution of a set of values.
 * Every histogram tracks average, minimum, maximum, median, 95th percentile, and the count of a set of values.
 */
public class Histogram extends Metric<Number> {

  /**
   * Create an Histogram value to be sent to DogStatD.
   * 
   * @param name The name of the histogram.
   * @param value The value to add the set.
   * @param tags Any additional data about the value.
   */
  public Histogram(String name, Number value, String... tags) {
    super(name, value, 'h', tags);
  }
}
