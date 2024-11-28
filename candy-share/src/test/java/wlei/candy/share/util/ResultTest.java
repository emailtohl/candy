package wlei.candy.share.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by HeLei on 2021/8/26.
 */
class ResultTest {

  @Test
  void ofId() {
    Result r = Result.ofId(1L);
    assertEquals(1L, r.getId());
    String s = JsonUtil.writeValue(r);
    assertEquals("{\"id\":1}", s);
    r.setId(2L);
    s = JsonUtil.writeValue(r);
    assertEquals("{\"id\":2}", s);
  }

  @Test
  void ofExist() {
    Result r = Result.ofExist(true);
    assertTrue(r.getExist());
    String s = JsonUtil.writeValue(r);
    assertEquals("{\"exist\":true}", s);
    r.setExist(false);
    s = JsonUtil.writeValue(r);
    assertEquals("{\"exist\":false}", s);
  }

  @Test
  void withSuccess() {
    Result r = Result.withSuccess();
    assertTrue(r.getSuccess());
    String s = JsonUtil.writeValue(r);
    assertEquals("{\"success\":true}", s);
    r.setSuccess(false);
    s = JsonUtil.writeValue(r);
    assertEquals("{\"success\":false}", s);
  }

  @Test
  void withFail() {
    Result r = Result.withFail();
    assertFalse(r.getSuccess());
    String s = JsonUtil.writeValue(r);
    assertEquals("{\"success\":false}", s);
  }

  @Test
  void ofText() {
    Result r = Result.ofText("hello");
    String s = JsonUtil.writeValue(r);
    assertEquals("{\"text\":\"hello\"}", s);
    r.setText("world");
    s = JsonUtil.writeValue(r);
    assertEquals("{\"text\":\"world\"}", s);
  }
}