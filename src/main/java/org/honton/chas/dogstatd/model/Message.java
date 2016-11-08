package org.honton.chas.dogstatd.model;

import java.nio.CharBuffer;

/**
 * A message that can be sent to DogStatD.
 */
public interface Message {
  
  /**
   * Format the message to be sent.  In general, message includes type of record, 
   * metrics, tags and ends with newline.
   * 
   * @param buffer The buffer to hold the formatted message.
   */
  void format(CharBuffer buffer);
  
  /**
   * Validate the message to be sent.  If the message is invalid, this method should 
   * log the incorrect portion and reason for failure.
   * This method may be called from a different thread than the one invoking the 
   * {@link Sender#send(Message)} method.
   * 
   * @return true, if the message is valid and should be sent.
   */
  boolean validate();
}
