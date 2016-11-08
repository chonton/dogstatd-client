package org.honton.chas.dogstatd.model;

import java.nio.CharBuffer;

import org.junit.Assert;
import org.junit.Test;

public class ValidatorTest {

  @Test
  public void testNameValidator() {
    Assert.assertTrue(Validator.nameIsValid("ABCDEFGHIJKLMNOPQRSTUVWXYZ.abcdefghijklmnopqrstuvwxyz_0123456789"));
    Assert.assertFalse(Validator.nameIsValid(""));
    Assert.assertFalse(Validator.nameIsValid("$"));
    Assert.assertFalse(Validator.nameIsValid("aΔ"));
  }

  @Test
  public void testTagValidator() {
    Assert.assertTrue(Validator.tagIsValid("abcdefghijklmnopqrstuvwxyz:-./_0123456789"));
    Assert.assertTrue(Validator.tagIsValid(CharBuffer.allocate(200).toString().replace('\0', 'x')));
    Assert.assertFalse(Validator.tagIsValid(CharBuffer.allocate(201).toString().replace('\0', 'x')));
    Assert.assertFalse(Validator.tagIsValid(""));
    Assert.assertFalse(Validator.tagIsValid("A"));
    Assert.assertFalse(Validator.tagIsValid("aΔ"));
  }

  @Test
  public void testValueValidator() {
    Assert.assertTrue(Validator.fieldIsValid("Aa:-./_0Δ"));
    Assert.assertFalse(Validator.fieldIsValid(""));
    Assert.assertFalse(Validator.fieldIsValid("A|"));
    Assert.assertFalse(Validator.fieldIsValid("A\n"));
  }
}
