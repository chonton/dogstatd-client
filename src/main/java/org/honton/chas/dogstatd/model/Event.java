package org.honton.chas.dogstatd.model;

import java.nio.CharBuffer;

import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * Event is message with title, text, and metadata.
 */
@Slf4j
@Setter
@ToString
@Accessors(chain = true)
public class Event implements Message {

  public enum Priority {
    normal, low
  }

  public enum Alert {
    success, info, warning, error
  }

  private final String title;
  private final String text;
  private final String[] tags;
  private Long timestamp;
  private String host;
  private String key;
  private Priority priority;
  private String source;
  private Alert alert;
  
  /**
   * Create an event to be sent to DogStatD.
   * 
   * @param title The title of the event.
   * @param text The text about the event.
   * @param tags Any additional data about the value.
   */
  Event(@NonNull String title, @NonNull String text, String... tags) {
    this.title = title;
    this.text = text;
    this.tags = tags;
  }

  @Override
  public boolean validate() {
    if(title.isEmpty()) {
      log.warn("title should be non-empty");
      return false;
    }
    if (host != null && !Validator.fieldIsValid(host)) {
      log.warn("host contains control characters or is empty");
      return false;
    }
    if (key != null && !Validator.fieldIsValid(key)) {
      log.warn("key contains control characters or is empty");
      return false;
    }
    if (source != null && !Validator.fieldIsValid(source)) {
      log.warn("source contains control characters or is empty");
      return false;
    }
    return Tags.validate(log, tags);
  }

  @Override
  public void format(CharBuffer chars) {
    chars.append("_e{").append(Integer.toString(title.length())).append(',')
        .append(Integer.toString(text.length())).append("}:").append(title).append('|')
        .append(text);

    if (timestamp != null) {
      chars.append("|d:").append(timestamp.toString());
    }

    if (host != null) {
      chars.append("|h:").append(host);
    }

    if (key != null) {
      chars.append("|k:").append(key);
    }

    if (priority != null) {
      chars.append("|p:").append(priority.name());
    }

    if (source != null) {
      chars.append("|s:").append(source);
    }

    if (alert != null) {
      chars.append("|t:").append(alert.name());
    }

    Tags.format(chars, tags);
    chars.append('\n');
  }
}
