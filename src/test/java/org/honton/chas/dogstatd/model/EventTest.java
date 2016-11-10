package org.honton.chas.dogstatd.model;

import java.nio.CharBuffer;

import org.junit.Assert;
import org.junit.Test;

public class EventTest {

  @Test
  public void testFormat() {
    Event event = new Event("title", "text", "tag");
    Assert.assertTrue(event.validate());
    CharBuffer chars = CharBuffer.allocate(512);
    event.format(chars);
    Assert.assertEquals("_e{5,4}:title|text|#tag\n", chars.flip().toString());

    event = new Event("Δ", "three\nline\ntext");
    Assert.assertTrue(event.validate());
    chars.clear();    
    event.format(chars);
    Assert.assertEquals("_e{1,15}:Δ|three\nline\ntext\n", chars.flip().toString());
  }

  @Test
  public void testMeta() {
    CharBuffer chars = CharBuffer.allocate(512);
    
    new Event("t", "t").setTimestamp(0L).format(chars);
    Assert.assertEquals("_e{1,1}:t|t|d:0\n", chars.flip().toString());
    
    chars.clear();
    new Event("t", "t").setHost("localhost").format(chars);
    Assert.assertEquals("_e{1,1}:t|t|h:localhost\n", chars.flip().toString());
    
    chars.clear();
    new Event("t", "t").setKey("key").format(chars);
    Assert.assertEquals("_e{1,1}:t|t|k:key\n", chars.flip().toString());
    
    chars.clear();
    new Event("t", "t").setPriority(Event.Priority.low).format(chars);
    Assert.assertEquals("_e{1,1}:t|t|p:low\n", chars.flip().toString());
    
    chars.clear();
    new Event("t", "t").setSource("source").format(chars);
    Assert.assertEquals("_e{1,1}:t|t|s:source\n", chars.flip().toString());
    
    chars.clear();
    new Event("t", "t").setAlert(Event.Alert.error).format(chars);
    Assert.assertEquals("_e{1,1}:t|t|t:error\n", chars.flip().toString());
  }

  @Test(expected = NullPointerException.class)
  public void testTitleNull() {
    new Event(null, "text");
  }

  @Test(expected = NullPointerException.class)
  public void testTextNull() {
    new Event("title", null);
  }

  @Test
  public void testInvalid() {
    Assert.assertFalse(new Event("title", "text", "Tag2:value2").validate());
    Assert.assertFalse(new Event("", "text").validate());
    Assert.assertFalse(new Event("t", "t").setHost("").validate());
    Assert.assertFalse(new Event("t", "t").setKey("\t").validate());
    Assert.assertFalse(new Event("t", "t").setSource("|").validate());
  }
}
