package org.honton.chas.dogstatd.model;

import java.nio.CharBuffer;

import org.junit.Assert;
import org.junit.Test;

public class CounterTest {

  @Test
  public void testFormat() {
    Counter counter = new Counter("name", 1, "tag");
    Assert.assertTrue(counter.validate());
    CharBuffer chars = CharBuffer.allocate(512);
    counter.format(chars);
    Assert.assertEquals("name:1|c|#tag\n", chars.flip().toString());

    counter = new Counter("name", 3.14);
    Assert.assertTrue(counter.validate());
    chars.clear();
    counter.format(chars);
    Assert.assertEquals("name:3.14|c\n", chars.flip().toString());
  }

  @Test
  public void testInvalid() {
    Assert.assertFalse(new Counter("name", 13, "Tag2:value2").validate());
    Assert.assertFalse(new Counter("1", 13).validate());
  }
}
