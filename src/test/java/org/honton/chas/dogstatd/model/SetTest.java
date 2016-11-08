package org.honton.chas.dogstatd.model;

import java.nio.CharBuffer;

import org.junit.Assert;
import org.junit.Test;

public class SetTest {

  @Test
  public void testFormat() {
    Set set = new Set("name", "value", "tag");
    Assert.assertTrue(set.validate());
    CharBuffer chars = CharBuffer.allocate(512);
    set.format(chars);
    Assert.assertEquals("name:value|s|#tag\n", chars.flip().toString());

    set = new Set("name", "entry");
    Assert.assertTrue(set.validate());
    chars.clear();
    set.format(chars);
    Assert.assertEquals("name:entry|s\n", chars.flip().toString());
  }

  @Test
  public void testInvalid() {
    Assert.assertFalse(new Set("name", "value", "Tag2:value2").validate());
    Assert.assertFalse(new Set("1", "value").validate());
  }
}
