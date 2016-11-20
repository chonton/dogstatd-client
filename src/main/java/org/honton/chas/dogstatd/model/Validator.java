package org.honton.chas.dogstatd.model;

import java.util.regex.Pattern;

/**
 * Validation routines
 */
public final class Validator {

  // Metric names must start with a letter, and after that may contain ascii alphanumerics,
  // underscore and periods.
  private static final Pattern VALID_NAME = Pattern.compile("^[A-Za-z][0-9A-Za-z_\\.]*$");

  // Tags must start with a lowercase letter, and after that may contain lowercase alphanumerics,
  // underscores, minuses, colons, periods and slashes.
  // Tags can be up to 200 characters long.
  private static final Pattern VALID_TAG = Pattern.compile("^[a-z][0-9a-z_\\-:\\./]{0,199}$");

  // No pipes
  private static final Pattern NO_PIPES = Pattern.compile("^[^\\|\\p{Cntrl}]+$");

  private Validator() {}

  /**
   * Validate a metric name.
   * 
   * @param name The name to validate.
   * @return true, if the name start with an ASCII letter, and contains only ASCII alphanumerics,
   *         underscores, and periods.
   */
  public static boolean nameIsValid(String name) {
    return VALID_NAME.matcher(name).matches();
  }

  /**
   * Validate a String tag.
   * 
   * @param tag The String tag to validate.
   * @return true, if the tag is 1 to 200 characters long and starts with a lowercase ASCII letter,
   *         and contains only lowercase ASCII alphanumerics, underscores, minuses, colons, periods
   *         and slashes.
   */
  public static boolean tagIsValid(String tag) {
    return VALID_TAG.matcher(tag).matches();
  }

  /**
   * Validate a String value.
   * 
   * @param value The value to validate.
   * @return true, if value is not empty and does not have any pipe ('|') or control characters
   */
  public static boolean fieldIsValid(String value) {
    return NO_PIPES.matcher(value).matches();
  }
}
