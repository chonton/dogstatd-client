package org.honton.chas.dogstatd.model;

import java.nio.CharBuffer;

import org.junit.Assert;
import org.junit.Test;

public class ServiceCheckTest {

  @Test
  public void testFormat() {
    ServiceCheck sc = new ServiceCheck("name", ServiceCheck.Status.OK, "tag1", "tag2:value2");
    Assert.assertTrue(sc.validate());
    CharBuffer chars = CharBuffer.allocate(512);
    sc.format(chars);
    Assert.assertEquals("_sc|name|0|#tag1,tag2:value2\n", chars.flip().toString());
  }

  @Test
  public void testMeta() {
    CharBuffer chars = CharBuffer.allocate(512);
    
    new ServiceCheck("name", ServiceCheck.Status.WARNING).setTimestamp(0L).format(chars);
    Assert.assertEquals("_sc|name|1|d:0\n", chars.flip().toString());
    
    chars.clear();
    new ServiceCheck("name", ServiceCheck.Status.CRITICAL).setHost("localhost").format(chars);
    Assert.assertEquals("_sc|name|2|h:localhost\n", chars.flip().toString());
    
    chars.clear();
    new ServiceCheck("name", ServiceCheck.Status.UNKNOWN).setMessage("messαge|").format(chars);
    Assert.assertEquals("_sc|name|3|m:messαge|\n", chars.flip().toString());
  }

  @Test
  public void testFormatNoTags() {
    ServiceCheck sc = new ServiceCheck("name", ServiceCheck.Status.UNKNOWN);
    Assert.assertTrue(sc.validate());
    CharBuffer chars = CharBuffer.allocate(512);
    sc.format(chars);
    Assert.assertEquals("_sc|name|3\n", chars.flip().toString());
  }

  @Test
  public void testInvalid() {
    Assert.assertFalse(new ServiceCheck("name", ServiceCheck.Status.UNKNOWN, "Tag2:value2").validate());
    Assert.assertFalse(new ServiceCheck("name", ServiceCheck.Status.UNKNOWN).setHost("").validate());
  }
}
