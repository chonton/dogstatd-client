package org.honton.chas.dogstatd.model;

/**
 * A Counter measures how many occurrences per second.
 */
public class Counter extends Metric<Number> {

  /**
   * Create an Counter value to be sent to DogStatD.
   * 
   * @param name The name of the counter.
   * @param value The counts.
   * @param tags Any additional data about the value.
   */
  public Counter(String name, Number value, String... tags) {
    super(name, value, 'c', tags);
  }
}
