package org.honton.chas.dogstatd.model;

import java.nio.CharBuffer;

import org.slf4j.Logger;

/**
 * Tag routines
 */
public final class Tags {

  private Tags() {}

  /**
   * Validate an array of tags.  Uses {@link Validator#tagIsValid(String)} to validate each tag.
   * @param log The logger to inform of invalid tags
   * @param tags The tags to validate
   * @return true, if all tags are valid
   */
  public static boolean validate(Logger log, String... tags) {
    for (String tag : tags) {
      if (!Validator.tagIsValid(tag)) {
        log.warn("invalid tag '{}'", tag);
        return false;
      }
    }
    return true;
  }

  /**
   * Format tags into a CharBuffer
   * @param chars The buffer to receive the formatted tags
   * @param tags The tags to format
   */
  public static void format(CharBuffer chars, String... tags) {
    if (tags.length > 0) {
      String sep = "|#";
      for (String tag : tags) {
        chars.append(sep).append(tag);
        sep = ",";
      }
    }
  }
}
