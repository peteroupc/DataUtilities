package com.upokecenter.test;

import java.util.*;
import java.io.*;

import org.junit.Assert;
import org.junit.Test;
import com.upokecenter.util.*;

  public class DataUtilitiesTest {
    public static List<byte[]> GenerateIllegalUtf8Sequences() {
      ArrayList<byte[]> list = new ArrayList<byte[]>();
      // Generate illegal single bytes
      for (int i = 0x80; i <= 0xff; ++i) {
        if (i < 0xc2 || i > 0xf4) {
          list.add(new byte[] { (byte)i, (byte)0x80 });
        }
        list.add(new byte[] { (byte)i });
      }
      list.add(new byte[] { (byte)0xe0, (byte)0xa0 });
      list.add(new byte[] { (byte)0xe1, (byte)0x80 });
      list.add(new byte[] { (byte)0xef, (byte)0x80 });
      list.add(new byte[] { (byte)0xf0, (byte)0x90 });
      list.add(new byte[] { (byte)0xf1, (byte)0x80 });
      list.add(new byte[] { (byte)0xf3, (byte)0x80 });
      list.add(new byte[] { (byte)0xf4, (byte)0x80 });
      list.add(new byte[] { (byte)0xf0, (byte)0x90, (byte)0x80 });
      list.add(new byte[] { (byte)0xf1, (byte)0x80, (byte)0x80 });
      list.add(new byte[] { (byte)0xf3, (byte)0x80, (byte)0x80 });
      list.add(new byte[] { (byte)0xf4, (byte)0x80, (byte)0x80 });
      // Generate illegal multibyte sequences
      for (int i = 0x00; i <= 0xff; ++i) {
        if (i < 0x80 || i > 0xbf) {
          list.add(new byte[] { (byte)0xc2, (byte)i });
          list.add(new byte[] { (byte)0xdf, (byte)i });
          list.add(new byte[] { (byte)0xe1, (byte)i, (byte)0x80 });
          list.add(new byte[] { (byte)0xef, (byte)i, (byte)0x80 });
          list.add(new byte[] { (byte)0xf1, (byte)i, (byte)0x80, (byte)0x80 });
          list.add(new byte[] { (byte)0xf3, (byte)i, (byte)0x80, (byte)0x80 });
          list.add(new byte[] { (byte)0xe0, (byte)0xa0, (byte)i });
          list.add(new byte[] { (byte)0xe1, (byte)0x80, (byte)i });
          list.add(new byte[] { (byte)0xef, (byte)0x80, (byte)i });
          list.add(new byte[] { (byte)0xf0, (byte)0x90, (byte)i, (byte)0x80 });
          list.add(new byte[] { (byte)0xf1, (byte)0x80, (byte)i, (byte)0x80 });
          list.add(new byte[] { (byte)0xf3, (byte)0x80, (byte)i, (byte)0x80 });
          list.add(new byte[] { (byte)0xf4, (byte)0x80, (byte)i, (byte)0x80 });
          list.add(new byte[] { (byte)0xf0, (byte)0x90, (byte)0x80, (byte)i });
          list.add(new byte[] { (byte)0xf1, (byte)0x80, (byte)0x80, (byte)i });
          list.add(new byte[] { (byte)0xf3, (byte)0x80, (byte)0x80, (byte)i });
          list.add(new byte[] { (byte)0xf4, (byte)0x80, (byte)0x80, (byte)i });
        }
        if (i < 0xa0 || i > 0xbf) {
          list.add(new byte[] { (byte)0xe0, (byte)i, (byte)0x80 });
        }
        if (i < 0x90 || i > 0xbf) {
          list.add(new byte[] { (byte)0xf0, (byte)i, (byte)0x80, (byte)0x80 });
        }
        if (i < 0x80 || i > 0x8f) {
          list.add(new byte[] { (byte)0xf4, (byte)i, (byte)0x80, (byte)0x80 });
        }
      }
      return list;
    }

    @Test
    public void TestCodePointAt() {
      try {
        com.upokecenter.util.DataUtilities.CodePointAt(null, 0);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      Assert.assertEquals(-1, com.upokecenter.util.DataUtilities.CodePointAt("A", -1));
      Assert.assertEquals(-1, com.upokecenter.util.DataUtilities.CodePointAt("A", 1));
      Assert.assertEquals(0x41, com.upokecenter.util.DataUtilities.CodePointAt("A", 0));

      Assert.assertEquals(0xfffd, com.upokecenter.util.DataUtilities.CodePointAt("\ud800", 0));
      Assert.assertEquals(0xfffd, com.upokecenter.util.DataUtilities.CodePointAt("\udc00", 0));
      Assert.assertEquals(0xfffd, com.upokecenter.util.DataUtilities.CodePointAt("\ud800X", 0));
      Assert.assertEquals(0xfffd, com.upokecenter.util.DataUtilities.CodePointAt("\udc00X", 0));
      Assert.assertEquals(0xfffd, com.upokecenter.util.DataUtilities.CodePointAt("\ud800\ud800", 0));
      Assert.assertEquals(0xfffd, com.upokecenter.util.DataUtilities.CodePointAt("\udc00\ud800", 0));
      Assert.assertEquals(
        0xfffd,
        com.upokecenter.util.DataUtilities.CodePointAt("\ud800\ud800\udc00", 0));
      Assert.assertEquals(
        0xfffd,
        com.upokecenter.util.DataUtilities.CodePointAt("\udc00\ud800\udc00", 0));
      Assert.assertEquals(0xfffd, com.upokecenter.util.DataUtilities.CodePointAt("\udc00\udc00", 0));
      Assert.assertEquals(0x10000, com.upokecenter.util.DataUtilities.CodePointAt("\ud800\udc00", 0));
      Assert.assertEquals(0xfffd, com.upokecenter.util.DataUtilities.CodePointAt("\ud800", 0, 0));
      Assert.assertEquals(0xfffd, com.upokecenter.util.DataUtilities.CodePointAt("\udc00", 0, 0));
      Assert.assertEquals(0xfffd, com.upokecenter.util.DataUtilities.CodePointAt("\ud800X", 0, 0));
      Assert.assertEquals(0xfffd, com.upokecenter.util.DataUtilities.CodePointAt("\udc00X", 0, 0));
      {
        Object objectTemp = 0xfffd;
        Object objectTemp2 = com.upokecenter.util.DataUtilities.CodePointAt(
            "\ud800\ud800",
            0,
            0);
        Assert.assertEquals(objectTemp, objectTemp2);
      }
      {
        Object objectTemp = 0xfffd;
        Object objectTemp2 = com.upokecenter.util.DataUtilities.CodePointAt(
            "\udc00\ud800",
            0,
            0);
        Assert.assertEquals(objectTemp, objectTemp2);
      }
      Assert.assertEquals(
        0xfffd,
        com.upokecenter.util.DataUtilities.CodePointAt("\ud800\ud800\udc00", 0, 0));
      Assert.assertEquals(
        0xfffd,
        com.upokecenter.util.DataUtilities.CodePointAt("\udc00\ud800\udc00", 0, 0));
      {
        Object objectTemp = 0xfffd;
        Object objectTemp2 = com.upokecenter.util.DataUtilities.CodePointAt(
            "\udc00\udc00",
            0,
            0);
        Assert.assertEquals(objectTemp, objectTemp2);
      }
      {
        Object objectTemp = 0x10000;
        Object objectTemp2 = com.upokecenter.util.DataUtilities.CodePointAt(
            "\ud800\udc00",
            0,
            0);
        Assert.assertEquals(objectTemp, objectTemp2);
      }

      Assert.assertEquals(0xd800, com.upokecenter.util.DataUtilities.CodePointAt("\ud800", 0, 1));
      Assert.assertEquals(0xdc00, com.upokecenter.util.DataUtilities.CodePointAt("\udc00", 0, 1));
      Assert.assertEquals(0xd800, com.upokecenter.util.DataUtilities.CodePointAt("\ud800X", 0, 1));
      Assert.assertEquals(0xdc00, com.upokecenter.util.DataUtilities.CodePointAt("\udc00X", 0, 1));
      {
        Object objectTemp = 0xd800;
        Object objectTemp2 = com.upokecenter.util.DataUtilities.CodePointAt(
            "\ud800\ud800",
            0,
            1);
        Assert.assertEquals(objectTemp, objectTemp2);
      }
      Assert.assertEquals(
        0xd800,
        com.upokecenter.util.DataUtilities.CodePointAt("\ud800\ud800\udc00", 0, 1));
      {
        Object objectTemp = 0xdc00;
        Object objectTemp2 = com.upokecenter.util.DataUtilities.CodePointAt(
            "\udc00\ud800",
            0,
            1);
        Assert.assertEquals(objectTemp, objectTemp2);
      }
      Assert.assertEquals(
        0xdc00,
        com.upokecenter.util.DataUtilities.CodePointAt("\udc00\ud800\udc00", 0, 1));
      {
        Object objectTemp = 0xdc00;
        Object objectTemp2 = com.upokecenter.util.DataUtilities.CodePointAt(
            "\udc00\udc00",
            0,
            1);
        Assert.assertEquals(objectTemp, objectTemp2);
      }
      {
        Object objectTemp = 0x10000;
        Object objectTemp2 = com.upokecenter.util.DataUtilities.CodePointAt(
            "\ud800\udc00",
            0,
            1);
        Assert.assertEquals(objectTemp, objectTemp2);
      }

      Assert.assertEquals(-1, com.upokecenter.util.DataUtilities.CodePointAt("\ud800", 0, 2));
      Assert.assertEquals(-1, com.upokecenter.util.DataUtilities.CodePointAt("\udc00", 0, 2));
      Assert.assertEquals(-1, com.upokecenter.util.DataUtilities.CodePointAt("\ud800X", 0, 2));
      Assert.assertEquals(-1, com.upokecenter.util.DataUtilities.CodePointAt("\udc00X", 0, 2));
      Assert.assertEquals(-1, com.upokecenter.util.DataUtilities.CodePointAt("\ud800\ud800", 0, 2));
      {
        long numberTemp = com.upokecenter.util.DataUtilities.CodePointAt(
            "\ud800\ud800\udc00",
            0,
            2);
        Assert.assertEquals(-1, numberTemp);
      }
      Assert.assertEquals(-1, com.upokecenter.util.DataUtilities.CodePointAt("\udc00\ud800", 0, 2));
      {
        long numberTemp = com.upokecenter.util.DataUtilities.CodePointAt(
            "\udc00\ud800\udc00",
            0,
            2);
        Assert.assertEquals(-1, numberTemp);
      }
      Assert.assertEquals(-1, com.upokecenter.util.DataUtilities.CodePointAt("\udc00\udc00", 0, 2));
      {
        Object objectTemp = 0x10000;
        Object objectTemp2 = com.upokecenter.util.DataUtilities.CodePointAt(
            "\ud800\udc00",
            0,
            2);
        Assert.assertEquals(objectTemp, objectTemp2);
      }
    }
    @Test
    public void TestCodePointBefore() {
      try {
        com.upokecenter.util.DataUtilities.CodePointBefore(null, 0);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      Assert.assertEquals(-1, com.upokecenter.util.DataUtilities.CodePointBefore("A", 0));
      Assert.assertEquals(-1, com.upokecenter.util.DataUtilities.CodePointBefore("A", -1));
      Assert.assertEquals((int)'A', com.upokecenter.util.DataUtilities.CodePointBefore("A", 1));
      Assert.assertEquals(-1, com.upokecenter.util.DataUtilities.CodePointBefore("A", 2));
      Assert.assertEquals(
        (int)'A',
        com.upokecenter.util.DataUtilities.CodePointBefore("A\ud800\udc00B", 1));
      Assert.assertEquals(
        0x10000,
        com.upokecenter.util.DataUtilities.CodePointBefore("A\ud800\udc00B", 3));
      Assert.assertEquals(
        0xfffd,
        com.upokecenter.util.DataUtilities.CodePointBefore("A\ud800\udc00B", 2));
      Assert.assertEquals(
        0xd800,
        com.upokecenter.util.DataUtilities.CodePointBefore("A\ud800\udc00B", 2, 1));
      {
        long numberTemp = com.upokecenter.util.DataUtilities.CodePointBefore(
            "A\ud800\udc00B",
            2,
            2);
        Assert.assertEquals(-1, numberTemp);
      }
      Assert.assertEquals(0xfffd, com.upokecenter.util.DataUtilities.CodePointBefore("\udc00B", 1));
      Assert.assertEquals(0xdc00, com.upokecenter.util.DataUtilities.CodePointBefore("\udc00B", 1, 1));
      Assert.assertEquals(-1, com.upokecenter.util.DataUtilities.CodePointBefore("\udc00B", 1, 2));
      Assert.assertEquals(0xfffd, com.upokecenter.util.DataUtilities.CodePointBefore("A\udc00B", 2));
      {
        Object objectTemp = 0xdc00;
        Object objectTemp2 = com.upokecenter.util.DataUtilities.CodePointBefore(
            "A\udc00B",
            2,
            1);
        Assert.assertEquals(objectTemp, objectTemp2);
      }
      Assert.assertEquals(-1, com.upokecenter.util.DataUtilities.CodePointBefore("A\udc00B", 2, 2));
    }
    @Test
    public void TestCodePointCompare() {
      int integerTemp3;
      {
        Object objectTemp = -1;
        Object objectTemp2 = ((com.upokecenter.util.DataUtilities.CodePointCompare(null,
              "A")==0) ? 0 : ((com.upokecenter.util.DataUtilities.CodePointCompare(null,
              "A")< 0) ? -1 : 1));
        Assert.assertEquals(objectTemp, objectTemp2);
      }
      integerTemp3 = ((
          com.upokecenter.util.DataUtilities.CodePointCompare("A", null)==0) ? 0 : ((
          com.upokecenter.util.DataUtilities.CodePointCompare("A", null)< 0) ? -1 : 1));
      Assert.assertEquals(1, integerTemp3);
      integerTemp3 = ((
          com.upokecenter.util.DataUtilities.CodePointCompare(null, null) == 0) ? 0 : ((
          com.upokecenter.util.DataUtilities.CodePointCompare(null, null)< 0) ? -1 : 1));
      Assert.assertEquals(0, integerTemp3);
      {
        long numberTemp = ((
            com.upokecenter.util.DataUtilities.CodePointCompare("abc", "abc")==0) ? 0 : ((
            com.upokecenter.util.DataUtilities.CodePointCompare("abc", "abc")< 0) ? -1 : 1));
        Assert.assertEquals(0, numberTemp);
      }
      {
        long numberTemp = ((
            com.upokecenter.util.DataUtilities.CodePointCompare(
              "\ud800\udc00",
              "\ud800\udc00")==0) ? 0 : ((
            com.upokecenter.util.DataUtilities.CodePointCompare(
              "\ud800\udc00",
              "\ud800\udc00")< 0) ? -1 : 1));
        Assert.assertEquals(0, numberTemp);
      }
      {
        long numberTemp = ((
            com.upokecenter.util.DataUtilities.CodePointCompare(
              "abc",
              "\ud800\udc00")==0) ? 0 : ((
            com.upokecenter.util.DataUtilities.CodePointCompare(
              "abc",
              "\ud800\udc00")< 0) ? -1 : 1));
        Assert.assertEquals(-1, numberTemp);
      }
      {
        long numberTemp = ((
            com.upokecenter.util.DataUtilities.CodePointCompare(
              "\uf000",
              "\ud800\udc00")==0) ? 0 : ((
            com.upokecenter.util.DataUtilities.CodePointCompare(
              "\uf000",
              "\ud800\udc00")< 0) ? -1 : 1));
        Assert.assertEquals(-1, numberTemp);
      }
      {
        long numberTemp = ((
            com.upokecenter.util.DataUtilities.CodePointCompare(
              "\uf000",
              "\ud800")==0) ? 0 : ((
            com.upokecenter.util.DataUtilities.CodePointCompare(
              "\uf000",
              "\ud800")< 0) ? -1 : 1));
        Assert.assertEquals(1, numberTemp);
      }
      if (!(com.upokecenter.util.DataUtilities.CodePointCompare("abc", "def") < 0)) {
 Assert.fail();
 }
      if (!(
        com.upokecenter.util.DataUtilities.CodePointCompare(
          "a\ud800\udc00",
          "a\ud900\udc00") < 0)) {
 Assert.fail();
 }
      if (!(
        com.upokecenter.util.DataUtilities.CodePointCompare(
          "a\ud800\udc00",
          "a\ud800\udc00") == 0)) {
 Assert.fail();
 }
      if (!(com.upokecenter.util.DataUtilities.CodePointCompare("a\ud800", "a\ud800")
        == 0)) {
 Assert.fail();
 }
      if (!(com.upokecenter.util.DataUtilities.CodePointCompare("a\udc00", "a\udc00")
        == 0)) {
 Assert.fail();
 }
      if (!(
        com.upokecenter.util.DataUtilities.CodePointCompare(
          "a\ud800\udc00",
          "a\ud800\udd00") < 0)) {
 Assert.fail();
 }
      if (!(
        com.upokecenter.util.DataUtilities.CodePointCompare(
          "a\ud800\ufffd",
          "a\ud800\udc00") < 0)) {
 Assert.fail();
 }
      if (!(
        com.upokecenter.util.DataUtilities.CodePointCompare(
          "a\ud800\ud7ff",
          "a\ud800\udc00") < 0)) {
 Assert.fail();
 }
      if (!(
        com.upokecenter.util.DataUtilities.CodePointCompare(
          "a\ufffd\udc00",
          "a\ud800\udc00") < 0)) {
 Assert.fail();
 }
      if (!(
        com.upokecenter.util.DataUtilities.CodePointCompare(
          "a\ud7ff\udc00",
          "a\ud800\udc00") < 0)) {
 Assert.fail();
 }
    }

    public static String Repeat(String c, int num) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < num; ++i) {
        sb.append(c);
      }
      return sb.toString();
    }

    private static void TestUtf8RoundTrip(String str) {
      {
        Object objectTemp = str;
        Object objectTemp2 = com.upokecenter.util.DataUtilities.GetUtf8String(
            com.upokecenter.util.DataUtilities.GetUtf8Bytes(str, true),
            true);
        Assert.assertEquals(objectTemp, objectTemp2);
      }
    }

    @Test
    public void TestGetUtf8Bytes() {
      try {
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\ud800", false);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\udc00", false);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\ud800\ud800", false);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\udc00\udc00", false);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\udc00\ud800", false);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8Bytes(null, true);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\ud800", false);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\udc00", false);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\ud800X", false);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\udc00X", false);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\ud800\ud800", false);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\udc00\ud800", false);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\udc00\ud800\udc00", false);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\ud800\ud800\udc00", false);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\udc00\udc00", false);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      TestCommon.AssertByteArraysEqual(
        new byte[] { (byte)0xf0, (byte)0x90, (byte)0x80, (byte)0x80 },
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\ud800\udc00", false));
      TestCommon.AssertByteArraysEqual(
        new byte[] { (byte)0xef, (byte)0xbf, (byte)0xbd },
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\ud800", true));
      TestCommon.AssertByteArraysEqual(
        new byte[] { (byte)0xef, (byte)0xbf, (byte)0xbd },
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\udc00", true));
      TestCommon.AssertByteArraysEqual(
        new byte[] { (byte)0xef, (byte)0xbf, (byte)0xbd, 88 },
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\ud800X", true));
      TestCommon.AssertByteArraysEqual(
        new byte[] { (byte)0xef, (byte)0xbf, (byte)0xbd, 88 },
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\udc00X", true));
      TestCommon.AssertByteArraysEqual(
        new byte[] { (byte)0xef, (byte)0xbf, (byte)0xbd, (byte)0xef, (byte)0xbf, (byte)0xbd },
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\ud800\ud800", true));
      TestCommon.AssertByteArraysEqual(
        new byte[] { (byte)0xef, (byte)0xbf, (byte)0xbd, (byte)0xef, (byte)0xbf, (byte)0xbd },
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\udc00\ud800", true));
      TestCommon.AssertByteArraysEqual(
        new byte[] { (byte)0xef, (byte)0xbf, (byte)0xbd, (byte)0xf0, (byte)0x90, (byte)0x80, (byte)0x80 },
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\udc00\ud800\udc00", true));
      TestCommon.AssertByteArraysEqual(
        new byte[] { (byte)0xef, (byte)0xbf, (byte)0xbd, (byte)0xf0, (byte)0x90, (byte)0x80, (byte)0x80 },
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\ud800\ud800\udc00", true));
      TestCommon.AssertByteArraysEqual(
        new byte[] { (byte)0xef, (byte)0xbf, (byte)0xbd, (byte)0xef, (byte)0xbf, (byte)0xbd },
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\udc00\udc00", true));
      TestCommon.AssertByteArraysEqual(
        new byte[] { (byte)0xf0, (byte)0x90, (byte)0x80, (byte)0x80 },
        com.upokecenter.util.DataUtilities.GetUtf8Bytes("\ud800\udc00", false));
    }
    @Test
    public void TestGetUtf8Length() {
      try {
        com.upokecenter.util.DataUtilities.GetUtf8Length(null, true);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      Assert.assertEquals(6, com.upokecenter.util.DataUtilities.GetUtf8Length("ABC\ud800", true));
      Assert.assertEquals(-1, com.upokecenter.util.DataUtilities.GetUtf8Length("ABC\ud800", false));
      try {
        com.upokecenter.util.DataUtilities.GetUtf8Length(null, true);
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8Length(null, false);
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      Assert.assertEquals(3, com.upokecenter.util.DataUtilities.GetUtf8Length("abc", true));
      Assert.assertEquals(4, com.upokecenter.util.DataUtilities.GetUtf8Length("\u0300\u0300", true));
      Assert.assertEquals(6, com.upokecenter.util.DataUtilities.GetUtf8Length("\u3000\u3000", true));
      Assert.assertEquals(6, com.upokecenter.util.DataUtilities.GetUtf8Length("\ud800\ud800", true));
      Assert.assertEquals(-1, com.upokecenter.util.DataUtilities.GetUtf8Length("\ud800\ud800", false));
      long numberTemp;
      {
        numberTemp = com.upokecenter.util.DataUtilities.GetUtf8Length("\ud800", false);
        Assert.assertEquals(-1, numberTemp);
      }
      {
        numberTemp = com.upokecenter.util.DataUtilities.GetUtf8Length("\udc00", false);
        Assert.assertEquals(-1, numberTemp);
      }
      {
        numberTemp = com.upokecenter.util.DataUtilities.GetUtf8Length("\ud800X", false);
        Assert.assertEquals(-1, numberTemp);
      }
      {
        numberTemp = com.upokecenter.util.DataUtilities.GetUtf8Length("\udc00X", false);
        Assert.assertEquals(-1, numberTemp);
      }
      {
        numberTemp = com.upokecenter.util.DataUtilities.GetUtf8Length("\ud800\ud800", false);
        Assert.assertEquals(-1, numberTemp);
      }
      {
        numberTemp = com.upokecenter.util.DataUtilities.GetUtf8Length("\udc00\ud800", false);
        Assert.assertEquals(-1, numberTemp);
      }
      {
        numberTemp = com.upokecenter.util.DataUtilities.GetUtf8Length("\udc00\ud800\udc00", false);
        Assert.assertEquals(-1, numberTemp);
      }
      {
        numberTemp = com.upokecenter.util.DataUtilities.GetUtf8Length("\ud800\ud800\udc00", false);
        Assert.assertEquals(-1, numberTemp);
      }
      {
        numberTemp = com.upokecenter.util.DataUtilities.GetUtf8Length("\udc00\udc00", false);
        Assert.assertEquals(-1, numberTemp);
      }
      {
        numberTemp = com.upokecenter.util.DataUtilities.GetUtf8Length("\ud800\udc00", false);
        Assert.assertEquals(4, numberTemp);
      }
      {
        numberTemp = com.upokecenter.util.DataUtilities.GetUtf8Length("\ud800", true);
        Assert.assertEquals(3, numberTemp);
      }
      {
        numberTemp = com.upokecenter.util.DataUtilities.GetUtf8Length("\udc00", true);
        Assert.assertEquals(3, numberTemp);
      }
      {
        numberTemp = com.upokecenter.util.DataUtilities.GetUtf8Length("\ud800X", true);
        Assert.assertEquals(4, numberTemp);
      }
      {
        numberTemp = com.upokecenter.util.DataUtilities.GetUtf8Length("\udc00X", true);
        Assert.assertEquals(4, numberTemp);
      }
      {
        numberTemp = com.upokecenter.util.DataUtilities.GetUtf8Length("\ud800\ud800", true);
        Assert.assertEquals(6, numberTemp);
      }
      {
        numberTemp = com.upokecenter.util.DataUtilities.GetUtf8Length("\udc00\ud800", true);
        Assert.assertEquals(6, numberTemp);
      }
      {
        numberTemp = com.upokecenter.util.DataUtilities.GetUtf8Length("\udc00\ud800\udc00", true);
        Assert.assertEquals(7, numberTemp);
      }
      {
        numberTemp = com.upokecenter.util.DataUtilities.GetUtf8Length("\ud800\ud800\udc00", true);
        Assert.assertEquals(7, numberTemp);
      }
      {
        numberTemp = com.upokecenter.util.DataUtilities.GetUtf8Length("\udc00\udc00", true);
        Assert.assertEquals(6, numberTemp);
      }
      {
        numberTemp = com.upokecenter.util.DataUtilities.GetUtf8Length("\ud800\udc00", false);
        Assert.assertEquals(4, numberTemp);
      }
    }
    @Test
    public void TestGetUtf8String() {
      TestUtf8RoundTrip("A" + Repeat("\u00e0", 10000));
      TestUtf8RoundTrip("AA" + Repeat("\u00e0", 10000));
      TestUtf8RoundTrip("AAA" + Repeat("\u00e0", 10000));
      TestUtf8RoundTrip("AAAA" + Repeat("\u00e0", 10000));
      TestUtf8RoundTrip("A" + Repeat("\u0ae0", 10000));
      TestUtf8RoundTrip("AA" + Repeat("\u0ae0", 10000));
      TestUtf8RoundTrip("AAA" + Repeat("\u0ae0", 10000));
      TestUtf8RoundTrip("AAAA" + Repeat("\u0ae0", 10000));
      TestUtf8RoundTrip("A" + Repeat("\ud800\udc00", 10000));
      TestUtf8RoundTrip("AA" + Repeat("\ud800\udc00", 10000));
      TestUtf8RoundTrip("AAA" + Repeat("\ud800\udc00", 10000));
      TestUtf8RoundTrip("AAAA" + Repeat("\ud800\udc00", 10000));

      try {
        com.upokecenter.util.DataUtilities.GetUtf8String(null, 0, 1, false);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8String(null, false);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8String(null, 0, 1, true);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8String(new byte[] { 0 }, -1, 1, true);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8String(new byte[] { 0 }, 2, 1, true);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8String(new byte[] { 0 }, 0, -1, true);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8String(new byte[] { 0 }, 0, 2, true);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8String(new byte[] { 0 }, 1, 1, true);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      String strtemp = com.upokecenter.util.DataUtilities.GetUtf8String(
          new byte[] { 0x41, 0x42, 0x43 },
          0,
          3,
          true);
      Assert.assertEquals(
        "ABC",
        strtemp);
      {
        String stringTemp = com.upokecenter.util.DataUtilities.GetUtf8String(
            new byte[] { 0x41, 0x42, 0x43, (byte)0x80 },
            0,
            4,
            true);
        Assert.assertEquals(
          "ABC\ufffd",
          stringTemp);
      }
      try {
        com.upokecenter.util.DataUtilities.GetUtf8String(
          new byte[] { 0x41, 0x42, 0x43, (byte)0x80 },
          0,
          4,
          false);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      List<byte[]> illegalSeqs = GenerateIllegalUtf8Sequences();
      for (byte[] seq : illegalSeqs) {
        try {
          com.upokecenter.util.DataUtilities.GetUtf8String(seq, false);
          Assert.fail("Should have failed");
        } catch (IllegalArgumentException ex) {
          // NOTE: Intentionally empty
        } catch (Exception ex) {
          Assert.fail(ex.toString());
          throw new IllegalStateException("", ex);
        }
        String strret = com.upokecenter.util.DataUtilities.GetUtf8String(seq, true);
        if (!(strret.length() > 0)) {
 Assert.fail();
 }
        Assert.assertEquals('\ufffd', strret.charAt(0));
        try {
          com.upokecenter.util.DataUtilities.GetUtf8String(seq, 0, seq.length, false);
          Assert.fail("Should have failed");
        } catch (IllegalArgumentException ex) {
          // NOTE: Intentionally empty
        } catch (Exception ex) {
          Assert.fail(ex.toString());
          throw new IllegalStateException("", ex);
        }
        strret = com.upokecenter.util.DataUtilities.GetUtf8String(seq, 0, seq.length, true);
        if (!(strret.length() > 0)) {
 Assert.fail();
 }
        Assert.assertEquals('\ufffd', strret.charAt(0));
      }
    }

    public static void DoTestReadUtf8(
      byte[] bytes,
      int expectedRet,
      String expectedString,
      int noReplaceRet,
      String noReplaceString) {
      if (bytes == null) {
        throw new NullPointerException("bytes");
      }
      DoTestReadUtf8(
        bytes,
        bytes.length,
        expectedRet,
        expectedString,
        noReplaceRet,
        noReplaceString);
    }

    public static void DoTestReadUtf8(
      byte[] bytes,
      int length,
      int expectedRet,
      String expectedString,
      int noReplaceRet,
      String noReplaceString) {
      try {
        StringBuilder builder = new StringBuilder();
        int ret = 0;
        {
          java.io.ByteArrayInputStream ms = null;
try {
ms = new java.io.ByteArrayInputStream(bytes);

          ret = com.upokecenter.util.DataUtilities.ReadUtf8(ms, length, builder, true);
          Assert.assertEquals(expectedRet, ret);
          if (expectedRet == 0) {
            Assert.assertEquals(expectedString, builder.toString());
          }
          ms.reset();
          builder.delete(0, builder.length());
          ret = com.upokecenter.util.DataUtilities.ReadUtf8(ms, length, builder, false);
          Assert.assertEquals(noReplaceRet, ret);
          if (noReplaceRet == 0) {
            Assert.assertEquals(noReplaceString, builder.toString());
          }
}
finally {
try { if (ms != null) { ms.close(); } } catch (java.io.IOException ex) {}
}
}
        if (bytes == null) {
          throw new NullPointerException("bytes");
        }
        if (bytes.length >= length) {
          builder.delete(0, builder.length());
          ret = com.upokecenter.util.DataUtilities.ReadUtf8FromBytes(
              bytes,
              0,
              length,
              builder,
              true);
          Assert.assertEquals(expectedRet, ret);
          if (expectedRet == 0) {
            Assert.assertEquals(expectedString, builder.toString());
          }
          builder.delete(0, builder.length());
          ret = com.upokecenter.util.DataUtilities.ReadUtf8FromBytes(
              bytes,
              0,
              length,
              builder,
              false);
          Assert.assertEquals(noReplaceRet, ret);
          if (noReplaceRet == 0) {
            Assert.assertEquals(noReplaceString, builder.toString());
          }
        }
      } catch (IOException ex) {
        throw new IllegalStateException("", ex);
      }
    }
    @Test
    public void TestReadUtf8() {
      try {
        com.upokecenter.util.DataUtilities.ReadUtf8(null, 1, null, true);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      {
        {
          java.io.ByteArrayInputStream ms = null;
try {
ms = new java.io.ByteArrayInputStream(new byte[] { 0 });

          try {
            com.upokecenter.util.DataUtilities.ReadUtf8(ms, 1, null, true);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (ms != null) { ms.close(); } } catch (java.io.IOException ex) {}
}
}
      }
      {
        {
          java.io.ByteArrayInputStream ms = null;
try {
ms = new java.io.ByteArrayInputStream(new byte[] { 0 });

          try {
            com.upokecenter.util.DataUtilities.ReadUtf8(ms, 1, null, false);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (ms != null) { ms.close(); } } catch (java.io.IOException ex) {}
}
}
      }
      DoTestReadUtf8(
        new byte[] { 0x21, 0x21, 0x21 },
        0,
        "!!!",
        0,
        "!!!");
      DoTestReadUtf8(
        new byte[] { 0x20, (byte)0xc2, (byte)0x80 },
        0,
        " \u0080",
        0,
        " \u0080");
      DoTestReadUtf8(
        new byte[] { 0x20, (byte)0xc2, (byte)0x80, 0x20 },
        0,
        " \u0080 ",
        0,
        " \u0080 ");
      DoTestReadUtf8(
        new byte[] { 0x20, (byte)0xc2, (byte)0x80, (byte)0xc2 },
        0,
        " \u0080\ufffd",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { 0x20, (byte)0xc2, 0x21, 0x21 },
        0,
        " \ufffd!!",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { 0x20, (byte)0xc2, (byte)0xff, 0x20 },
        0,
        " \ufffd\ufffd ",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { 0x20, (byte)0xe0, (byte)0xa0, (byte)0x80 },
        0,
        " \u0800",
        0,
        " \u0800");
      DoTestReadUtf8(
        new byte[] { 0x20, (byte)0xe0, (byte)0xa0, (byte)0x80, 0x20 },
        0,
        " \u0800 ",
        0,
        "\u0020\u0800 ");
      DoTestReadUtf8(
        new byte[] { 0x20, (byte)0xf0, (byte)0x90, (byte)0x80, (byte)0x80 },
        0,
        " \ud800\udc00",
        0,
        " \ud800\udc00");
      DoTestReadUtf8(
        new byte[] { 0x20, (byte)0xf0, (byte)0x90, (byte)0x80, (byte)0x80 },
        3,
        0,
        " \ufffd",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { 0x20, (byte)0xf0, (byte)0x90 },
        5,
        -2,
        null,
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { 0x20, 0x20, 0x20 },
        5,
        -2,
        null,
        -2,
        null);
      DoTestReadUtf8(
        new byte[] { 0x20, (byte)0xf0, (byte)0x90, (byte)0x80, (byte)0x80, 0x20 },
        0,
        " \ud800\udc00 ",
        0,
        " \ud800\udc00 ");
      DoTestReadUtf8(
        new byte[] { 0x20, (byte)0xf0, (byte)0x90, (byte)0x80, 0x20 },
        0,
        " \ufffd ",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { 0x20, (byte)0xf0, (byte)0x90, 0x20 },
        0,
        " \ufffd ",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { 0x20, (byte)0xf0, (byte)0x90, (byte)0x80, (byte)0xff },
        0,
        " \ufffd\ufffd",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { 0x20, (byte)0xf0, (byte)0x90, (byte)0xff },
        0,
        " \ufffd\ufffd",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { 0x20, (byte)0xe0, (byte)0xa0, 0x20 },
        0,
        " \ufffd ",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { 0x20, (byte)0xe0, 0x20 },
        0,
        " \ufffd ",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { 0x20, (byte)0xe0, (byte)0xa0, (byte)0xff },
        0,
        " \ufffd\ufffd",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { 0x20, (byte)0xe0, (byte)0xff },
        0,
        " \ufffd\ufffd",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { (byte)0xe0, (byte)0x9e },
        0,
        "\ufffd\ufffd",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { (byte)0xe0, (byte)0x9e, (byte)0x80 },
        0,
        "\ufffd\ufffd\ufffd",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { (byte)0xe0, (byte)0x9e, (byte)0x9e },
        0,
        "\ufffd\ufffd\ufffd",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { (byte)0xed, (byte)0xa1 },
        0,
        "\ufffd\ufffd",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { (byte)0xed, (byte)0xa1, (byte)0x80 },
        0,
        "\ufffd\ufffd\ufffd",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { (byte)0xed, (byte)0xa1, (byte)0x9e },
        0,
        "\ufffd\ufffd\ufffd",
        -1,
        null);

      DoTestReadUtf8(
        new byte[] { (byte)0xf0, (byte)0x8e },
        0,
        "\ufffd\ufffd",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { (byte)0xf0, (byte)0x8e, (byte)0x80 },
        0,
        "\ufffd\ufffd\ufffd",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { (byte)0xf0, (byte)0x8e, (byte)0x80, (byte)0x80 },
        0,
        "\ufffd\ufffd\ufffd\ufffd",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { (byte)0xf0, (byte)0x8e, (byte)0x85 },
        0,
        "\ufffd\ufffd\ufffd",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { (byte)0xf0, (byte)0x8e, (byte)0x85, (byte)0x80 },
        0,
        "\ufffd\ufffd\ufffd\ufffd",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { (byte)0xf4, (byte)0x93 },
        0,
        "\ufffd\ufffd",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { (byte)0xf4, (byte)0x93, (byte)0x80 },
        0,
        "\ufffd\ufffd\ufffd",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { (byte)0xf4, (byte)0x93, (byte)0x80, (byte)0x80 },
        0,
        "\ufffd\ufffd\ufffd\ufffd",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { (byte)0xf4, (byte)0x93, (byte)0x85 },
        0,
        "\ufffd\ufffd\ufffd",
        -1,
        null);
      DoTestReadUtf8(
        new byte[] { (byte)0xf4, (byte)0x93, (byte)0x85, (byte)0x80 },
        0,
        "\ufffd\ufffd\ufffd\ufffd",
        -1,
        null);
    }
    @Test
    public void TestReadUtf8FromBytes() {
      StringBuilder builder = new StringBuilder();
      try {
        com.upokecenter.util.DataUtilities.WriteUtf8("x", 0, 1, null, true);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.ReadUtf8FromBytes(null, 0, 1, new StringBuilder(), true);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.ReadUtf8FromBytes(
          new byte[] { 0 },
          -1,
          1,
          new StringBuilder(),
          true);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.ReadUtf8FromBytes(
          new byte[] { 0 },
          2,
          1,
          new StringBuilder(),
          true);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.ReadUtf8FromBytes(
          new byte[] { 0 },
          0,
          -1,
          new StringBuilder(),
          true);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.ReadUtf8FromBytes(
          new byte[] { 0 },
          0,
          2,
          new StringBuilder(),
          true);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.ReadUtf8FromBytes(
          new byte[] { 0 },
          1,
          1,
          new StringBuilder(),
          true);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.ReadUtf8FromBytes(new byte[] { 0 }, 0, 1, null, false);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      builder = new StringBuilder();
      {
        long numberTemp = com.upokecenter.util.DataUtilities.ReadUtf8FromBytes(
            new byte[] { (byte)0xf0, (byte)0x90, (byte)0x80, (byte)0x80 },
            0,
            4,
            builder,
            false);
        Assert.assertEquals(0, numberTemp);
      }
      {
        String stringTemp = builder.toString();
        Assert.assertEquals(
          "\ud800\udc00",
          stringTemp);
      }
      for (byte[] seq : GenerateIllegalUtf8Sequences()) {
        {
          long numberTemp = com.upokecenter.util.DataUtilities.ReadUtf8FromBytes(
              seq,
              0,
              seq.length,
              builder,
              false);
          Assert.assertEquals(-1, numberTemp);
        }
      }
    }
    @Test
    public void TestReadUtf8ToString() {
      try {
        com.upokecenter.util.DataUtilities.ReadUtf8ToString(null);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        com.upokecenter.util.DataUtilities.ReadUtf8ToString(null, 1, true);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      List<byte[]> illegalSeqs = GenerateIllegalUtf8Sequences();
      for (byte[] seq : illegalSeqs) {
        {
          java.io.ByteArrayInputStream ms = null;
try {
ms = new java.io.ByteArrayInputStream(seq);

          try {
            com.upokecenter.util.DataUtilities.ReadUtf8ToString(ms, -1, false);
            Assert.fail("Should have failed");
          } catch (IOException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (ms != null) { ms.close(); } } catch (java.io.IOException ex) {}
}
}
        {
          java.io.ByteArrayInputStream ms2 = null;
try {
ms2 = new java.io.ByteArrayInputStream(seq);

          String strret = null;
          try {
            strret = com.upokecenter.util.DataUtilities.ReadUtf8ToString(ms2, -1, true);
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          if (!(strret.length() > 0)) {
 Assert.fail();
 }
          Assert.assertEquals('\ufffd', strret.charAt(0));
}
finally {
try { if (ms2 != null) { ms2.close(); } } catch (java.io.IOException ex) {}
}
}
      }
    }
    @Test
    public void TestToLowerCaseAscii() {
      if (com.upokecenter.util.DataUtilities.ToLowerCaseAscii(null) != null) {
        Assert.fail();
      }
      {
        String stringTemp = com.upokecenter.util.DataUtilities.ToLowerCaseAscii("abc012-=?");
        Assert.assertEquals(
          "abc012-=?",
          stringTemp);
      }
      {
        String stringTemp = com.upokecenter.util.DataUtilities.ToLowerCaseAscii("ABC012-=?");
        Assert.assertEquals(
          "abc012-=?",
          stringTemp);
      }
    }
    @Test
    public void TestToUpperCaseAscii() {
      if (com.upokecenter.util.DataUtilities.ToUpperCaseAscii(null) != null) {
        Assert.fail();
      }
      {
        String stringTemp = com.upokecenter.util.DataUtilities.ToUpperCaseAscii("abc012-=?");
        Assert.assertEquals(
          "ABC012-=?",
          stringTemp);
      }
      {
        String stringTemp = com.upokecenter.util.DataUtilities.ToUpperCaseAscii("ABC012-=?");
        Assert.assertEquals(
          "ABC012-=?",
          stringTemp);
      }
    }
    @Test
    public void TestWriteUtf8() {
      try {
        try {
          com.upokecenter.util.DataUtilities.WriteUtf8(null, 0, 1, null, false);
          Assert.fail("Should have failed");
        } catch (NullPointerException ex) {
          // NOTE: Intentionally empty
        } catch (Exception ex) {
          Assert.fail(ex.toString());
          throw new IllegalStateException("", ex);
        }
        try {
          com.upokecenter.util.DataUtilities.WriteUtf8("xyz", 0, 1, null, false);
          Assert.fail("Should have failed");
        } catch (NullPointerException ex) {
          // NOTE: Intentionally empty
        } catch (Exception ex) {
          Assert.fail(ex.toString());
          throw new IllegalStateException("", ex);
        }
        try {
          com.upokecenter.util.DataUtilities.WriteUtf8(null, null, false);
          Assert.fail("Should have failed");
        } catch (NullPointerException ex) {
          // NOTE: Intentionally empty
        } catch (Exception ex) {
          Assert.fail(ex.toString());
          throw new IllegalStateException("", ex);
        }
        try {
          com.upokecenter.util.DataUtilities.WriteUtf8("xyz", null, false);
          Assert.fail("Should have failed");
        } catch (NullPointerException ex) {
          // NOTE: Intentionally empty
        } catch (Exception ex) {
          Assert.fail(ex.toString());
          throw new IllegalStateException("", ex);
        }
        {
          {
            java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

            try {
              com.upokecenter.util.DataUtilities.WriteUtf8("x", null, true);
              Assert.fail("Should have failed");
            } catch (NullPointerException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            try {
              com.upokecenter.util.DataUtilities.WriteUtf8("x", 0, 1, null, true);
              Assert.fail("Should have failed");
            } catch (NullPointerException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            try {
              com.upokecenter.util.DataUtilities.WriteUtf8("x", 0, 1, null, true, true);
              Assert.fail("Should have failed");
            } catch (NullPointerException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            try {
              com.upokecenter.util.DataUtilities.WriteUtf8(null, 0, 1, ms, true);
              Assert.fail("Should have failed");
            } catch (NullPointerException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            try {
              com.upokecenter.util.DataUtilities.WriteUtf8("x", -1, 1, ms, true);
              Assert.fail("Should have failed");
            } catch (IllegalArgumentException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            try {
              com.upokecenter.util.DataUtilities.WriteUtf8("x", 2, 1, ms, true);
              Assert.fail("Should have failed");
            } catch (IllegalArgumentException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            try {
              com.upokecenter.util.DataUtilities.WriteUtf8("x", 0, -1, ms, true);
              Assert.fail("Should have failed");
            } catch (IllegalArgumentException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            try {
              com.upokecenter.util.DataUtilities.WriteUtf8("x", 0, 2, ms, true);
              Assert.fail("Should have failed");
            } catch (IllegalArgumentException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            try {
              com.upokecenter.util.DataUtilities.WriteUtf8("x", 1, 1, ms, true);
              Assert.fail("Should have failed");
            } catch (IllegalArgumentException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            try {
              com.upokecenter.util.DataUtilities.WriteUtf8(null, 0, 1, ms, true, true);
              Assert.fail("Should have failed");
            } catch (NullPointerException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            try {
              com.upokecenter.util.DataUtilities.WriteUtf8("x", -1, 1, ms, true, true);
              Assert.fail("Should have failed");
            } catch (IllegalArgumentException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            try {
              com.upokecenter.util.DataUtilities.WriteUtf8("x", 2, 1, ms, true, true);
              Assert.fail("Should have failed");
            } catch (IllegalArgumentException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            try {
              com.upokecenter.util.DataUtilities.WriteUtf8("x", 0, -1, ms, true, true);
              Assert.fail("Should have failed");
            } catch (IllegalArgumentException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            try {
              com.upokecenter.util.DataUtilities.WriteUtf8("x", 0, 2, ms, true, true);
              Assert.fail("Should have failed");
            } catch (IllegalArgumentException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            try {
              com.upokecenter.util.DataUtilities.WriteUtf8("x", 1, 1, ms, true, true);
              Assert.fail("Should have failed");
            } catch (IllegalArgumentException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            try {
              com.upokecenter.util.DataUtilities.WriteUtf8(null, null, false);
              Assert.fail("Should have failed");
            } catch (NullPointerException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            try {
              com.upokecenter.util.DataUtilities.WriteUtf8(null, ms, false);
              Assert.fail("Should have failed");
            } catch (NullPointerException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
}
finally {
try { if (ms != null) { ms.close(); } } catch (java.io.IOException ex) {}
}
}
        }
        {
          {
            {
              java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

              com.upokecenter.util.DataUtilities.WriteUtf8("0\r1", 0, 3, ms, true, true);
              TestCommon.AssertByteArraysEqual(
                new byte[] { 0x30, 0x0d, 0x0a, 0x31 },
                ms.toByteArray());
}
finally {
try { if (ms != null) { ms.close(); } } catch (java.io.IOException ex) {}
}
}
          }
          {
            {
              java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

              com.upokecenter.util.DataUtilities.WriteUtf8("0\n1", 0, 3, ms, true, true);
              TestCommon.AssertByteArraysEqual(
                new byte[] { 0x30, 0x0d, 0x0a, 0x31 },
                ms.toByteArray());
}
finally {
try { if (ms != null) { ms.close(); } } catch (java.io.IOException ex) {}
}
}
          }
          {
            {
              java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

              com.upokecenter.util.DataUtilities.WriteUtf8("0\r\n1", 0, 4, ms, true, true);
              TestCommon.AssertByteArraysEqual(
                new byte[] { 0x30, 0x0d, 0x0a, 0x31 },
                ms.toByteArray());
}
finally {
try { if (ms != null) { ms.close(); } } catch (java.io.IOException ex) {}
}
}
          }
          {
            {
              java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

              com.upokecenter.util.DataUtilities.WriteUtf8("0\r\r1", 0, 4, ms, true, true);
              TestCommon.AssertByteArraysEqual(
                new byte[] { 0x30, 0x0d, 0x0a, 0x0d, 0x0a, 0x31 },
                ms.toByteArray());
}
finally {
try { if (ms != null) { ms.close(); } } catch (java.io.IOException ex) {}
}
}
          }
          {
            {
              java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

              com.upokecenter.util.DataUtilities.WriteUtf8("0\n\r1", 0, 4, ms, true, true);
              TestCommon.AssertByteArraysEqual(
                new byte[] { 0x30, 0x0d, 0x0a, 0x0d, 0x0a, 0x31 },
                ms.toByteArray());
}
finally {
try { if (ms != null) { ms.close(); } } catch (java.io.IOException ex) {}
}
}
          }
          {
            {
              java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

              com.upokecenter.util.DataUtilities.WriteUtf8("0\r\r\n1", 0, 5, ms, true, true);
              TestCommon.AssertByteArraysEqual(
                new byte[] { 0x30, 0x0d, 0x0a, 0x0d, 0x0a, 0x31 },
                ms.toByteArray());
}
finally {
try { if (ms != null) { ms.close(); } } catch (java.io.IOException ex) {}
}
}
          }
          {
            {
              java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

              com.upokecenter.util.DataUtilities.WriteUtf8("0\n\r\n1", 0, 5, ms, true, true);
              TestCommon.AssertByteArraysEqual(
                new byte[] { 0x30, 0x0d, 0x0a, 0x0d, 0x0a, 0x31 },
                ms.toByteArray());
}
finally {
try { if (ms != null) { ms.close(); } } catch (java.io.IOException ex) {}
}
}
          }
          {
            {
              java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

              com.upokecenter.util.DataUtilities.WriteUtf8("0\n\n\r1", 0, 5, ms, true, true);
              TestCommon.AssertByteArraysEqual(
                new byte[] { 0x30, 0x0d, 0x0a, 0x0d, 0x0a, 0x0d, 0x0a, 0x31 },
                ms.toByteArray());
}
finally {
try { if (ms != null) { ms.close(); } } catch (java.io.IOException ex) {}
}
}
          }
          {
            {
              java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

              com.upokecenter.util.DataUtilities.WriteUtf8("0\r\r\r1", 0, 5, ms, true, true);
              TestCommon.AssertByteArraysEqual(
                new byte[] { 0x30, 0x0d, 0x0a, 0x0d, 0x0a, 0x0d, 0x0a, 0x31 },
                ms.toByteArray());
}
finally {
try { if (ms != null) { ms.close(); } } catch (java.io.IOException ex) {}
}
}
          }
        }
      } catch (Exception ex) {
        throw new IllegalStateException(ex.getMessage(), ex);
      }
    }
  }
