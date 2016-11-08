package org.honton.chas.dogstatd.model;

/**
 * Sets are used to count the number of unique elements in a group.
 * Add an instance of this class to add to a Set.
 */
public class Set extends Metric<String> {

  /**
   * Add a member to the set.
   * @param name The name of the Set.
   * @param value The value to add to the Set.
   * @param tags Any additional data about the value.
   */
  public Set(String name, String value, String... tags) {
    super(name, value, 's', tags);
  }
}
