package org.honton.chas.dogstatd.model;

import java.nio.CharBuffer;

import org.junit.Assert;
import org.junit.Test;

public class HistogramTest {

  @Test
  public void testFormat() {
    Histogram histogram = new Histogram("histogram.name", 1, "tag");
    Assert.assertTrue(histogram.validate());
    CharBuffer chars = CharBuffer.allocate(512);
    histogram.format(chars);
    Assert.assertEquals("histogram.name:1|h|#tag\n", chars.flip().toString());

    histogram = new Histogram("Name", 42);
    Assert.assertTrue(histogram.validate());
    chars.clear();
    histogram.format(chars);
    Assert.assertEquals("Name:42|h\n", chars.flip().toString());
  }

  @Test
  public void testInvalid() {
    Assert.assertFalse(new Histogram("name", 13, "Tag2:value2").validate());
    Assert.assertFalse(new Histogram("1", 13).validate());
  }
}
