package org.honton.chas.dogstatd.model;

import java.nio.CharBuffer;

import org.junit.Assert;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TagsTest {

  @Test
  public void testTagsFormat() {
    CharBuffer chars = CharBuffer.allocate(512);
    Tags.format(chars);
    Assert.assertEquals("", chars.flip().toString());
    
    chars.clear();
    Tags.format(chars, "tag1");
    Assert.assertEquals("|#tag1", chars.flip().toString());
    
    chars.clear();
    Tags.format(chars, "tag1", "tag2:value2");
    Assert.assertEquals("|#tag1,tag2:value2", chars.flip().toString());
  }

  @Test
  public void testTagsValidate() {
    Assert.assertTrue(Tags.validate(log));
    Assert.assertTrue(Tags.validate(log, "a"));
    Assert.assertTrue(Tags.validate(log, "a", "b"));

    Assert.assertFalse(Tags.validate(log, ""));
    Assert.assertFalse(Tags.validate(log, "a", ""));
  }
}
