package org.honton.chas.dogstatd.model;

import java.nio.CharBuffer;

import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * A ServiceCheck is the current status of a service.
 */
@Accessors(chain = true)
@Slf4j
@ToString
@Setter
public class ServiceCheck implements Message {
  public enum Status {
    OK, WARNING, CRITICAL, UNKNOWN
  }

  private final String name;
  private final Status value;
  private final String[] tags;
  private Long timestamp;
  private String host;
  private String message;

  /**
   * Create an ServiceCheck value to be sent to DogStatD.
   * 
   * @param name The name of the service.
   * @param value The status.
   * @param tags Any additional data about the status.
   */
  public ServiceCheck(@NonNull String name, @NonNull Status value, String... tags) {
    this.name = name;
    this.value = value;
    this.tags = tags;
  }

  /**
   * Validate the message to be sent.
   * <ul>
   * <li>Use {@link Validator#nameIsValid(String)} to validate the metric name.</li>
   * <li>If host is set, use {@link Validator#fieldIsValid(String)} to validate the host.</li>
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
    if (host != null && !Validator.fieldIsValid(host)) {
      log.warn("host contains control characters or is empty");
      return false;
    }
    return Tags.validate(log, tags);
  }

  // _sc|name|status|metadata
  public void format(CharBuffer chars) {
    chars.append("_sc|").append(name)
        .append('|').append(Integer.toString(value.ordinal()));

    if (timestamp != null) {
      chars.append("|d:").append(timestamp.toString());
    }

    if (host != null) {
      chars.append("|h:").append(host);
    }

    Tags.format(chars, tags);

    if (message != null) {
      chars.append("|m:").append(message);
    }
    chars.append('\n');
  }
}
