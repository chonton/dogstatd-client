package org.honton.chas.dogstatd.outside;

import java.nio.CharBuffer;
import org.honton.chas.dogstatd.model.Gauge;
import org.junit.Assert;
import org.junit.Test;

public class GaugeTest {

  @Test
  public void testFormat() {
    Gauge gauge = new Gauge("gauge.name", 1, "tag");
    Assert.assertTrue(gauge.validate());
    CharBuffer chars = CharBuffer.allocate(512);
    gauge.format(chars);
    Assert.assertEquals("gauge.name:1|g|#tag\n", chars.flip().toString());

    gauge = new Gauge("Name", 3.14);
    Assert.assertTrue(gauge.validate());
    chars.clear();
    gauge.format(chars);
    Assert.assertEquals("Name:3.14|g\n", chars.flip().toString());
  }

  @Test
  public void testInvalid() {
    Assert.assertFalse(new Gauge("name", 13, "Tag2:value2").validate());
    Assert.assertFalse(new Gauge("1", 13).validate());
  }

  @Test(expected = NullPointerException.class)
  public void testNullNumber() {
    new Gauge("null", null);
  }
}
