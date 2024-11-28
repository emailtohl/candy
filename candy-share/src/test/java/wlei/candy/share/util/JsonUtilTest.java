package wlei.candy.share.util;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonUtilTest {

  @Test
  void normalMask() {
    String json = "{\"username\": \"john_doe\", \"password\": \"sensitive_password\", \"details\": {\"credit_card\": \"1234-5678-9012-3456\",\"specs\":[1,2,3]}}";
    String mask = JsonUtil.mask(json, this::mask, "password", "credit_card");
    HashMap<?, ?> m = JsonUtil.readValue(mask, HashMap.class);
    assertTrue(m.containsKey("password"));
    assertTrue(m.containsKey("details"));
    assertTrue(((Map<?, ?>) m.get("details")).containsKey("credit_card"));
    assertNotEquals("sensitive_password", m.get("password"));
    assertNotEquals("1234-5678-9012-3456", ((Map<?, ?>) m.get("details")).get("credit_card"));
  }

  @Test
  void noArgsMask() {
    String json = "{\"username\": \"john_doe\", \"password\": \"sensitive_password\", \"details\": {\"credit_card\": \"1234-5678-9012-3456\",\"specs\":[1,2,3]}}";
    String mask = JsonUtil.mask(json, this::mask);
    HashMap<?, ?> m = JsonUtil.readValue(mask, HashMap.class);
    assertEquals("sensitive_password", m.get("password"));
    assertEquals("1234-5678-9012-3456", ((Map<?, ?>) m.get("details")).get("credit_card"));
  }

  @Test
  void notStringMask() {
    String json = "{\"hello\":{\"world\":1},\"username\": \"john_doe\", \"password\": \"sensitive_password\", \"details\": {\"credit_card\": \"1234-5678-9012-3456\",\"specs\":[1,2,3]}}";
    String mask = JsonUtil.mask(json, this::mask, "hello", "world");
    HashMap<?, ?> m = JsonUtil.readValue(mask, HashMap.class);
    assertEquals(1, ((Map<?, ?>) m.get("hello")).get("world"));
  }

  @Test
  void exceptionMask() {
    String mask = JsonUtil.mask(null, this::mask, "hello", "world");
    assertNull(mask);
    mask = JsonUtil.mask("null", this::mask, "hello", "world");
    assertEquals("null", mask);
  }

  private String mask(String s) {
    return "****";
  }

}