package org.honton.chas.dogstatd.model;

import java.nio.CharBuffer;

import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a metric to be sent.
 * 
 * <ul>
 * <li>metric.name should be a String with no colons, bars or @ characters and pass naming
 * policy.</li>
 * <li>value should be a number</li>
 * <li>type should be c for Counter, g for Gauge, h for Histogram, ms for Timer or s for Set.</li>
 * <li>tags are optional, and should be a comma separated list of tags. Colons are used for key
 * value tags. Note that the key device is reserved, tags like “device:xyc” will be dropped by
 * Datadog.</li>
 * </ul>
 */
@Slf4j
@ToString
class Metric<T> implements Message {
  private final String name;
  protected final T value;
  private final char type;
  private final String[] tags;

  Metric(@NonNull String name, @NonNull T value, char type, String... tags) {
    this.name = name;
    this.value = value;
    this.type = type;
    this.tags = tags;
  }

  /**
   * Validate the message to be sent.
   * <ul>
   * <li>Use {@link Validator#nameIsValid(String)} to validate the metric name.</li>
   * <li>Use {@link Validator#tagIsValid(String)} to validate each tag.</li>
   * </ul>
   * @return true, if the message is valid and should be sent.
   */
  @Override
  public boolean validate() {
    if (!Validator.nameIsValid(name)) {
      log.warn("invalid name '{}'", name);
      return false;
    }
    return Tags.validate(log, tags);
  }

  // metric.name:value|type|@sample_rate|#tag1:value,tag2
  public void format(CharBuffer chars) {
    chars.append(name).append(':').append(value.toString()).append('|').append(type);
    Tags.format(chars, tags);
    chars.append('\n');
  }
}
