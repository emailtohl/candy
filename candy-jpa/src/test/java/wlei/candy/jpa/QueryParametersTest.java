package wlei.candy.jpa;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class QueryParametersTest {

  @Test
  void add() {
    QueryParameters p = new QueryParameters()
        .add("p1", 1)
        .add("p2", "")
        .add("p3", null)
        .add("p4", false)
        .add("p5", " hello ")
        .add("p6", LocalDateTime.now())
        .add("p7", new Object[]{})
        // 不能校验集合中的类型是否一致
        .add("p8", new Object[]{1, 2, null})
        .add("p9", new Object[]{1, 2})
        .add("p10", Arrays.asList(1, 2, null))
        .add("p11", Arrays.asList(1, 2))
        .add("p12", new Object[]{})
        .add("p13", new ArrayList<>())
        .add("p14", new char[]{'a', 'b'})
        .add("p15", new BigDecimal(12));

    assertTrue(p.containsKey("p1"));
    assertFalse(p.containsKey("p2"));
    assertFalse(p.containsKey("p3"));
    assertTrue(p.containsKey("p4"));
    assertTrue(p.containsKey("p5"));
    assertTrue(p.containsKey("p6"));
    assertFalse(p.containsKey("p7"));
    assertFalse(p.containsKey("p8"));
    assertTrue(p.containsKey("p9"));
    assertFalse(p.containsKey("p10"));
    assertTrue(p.containsKey("p11"));
    assertFalse(p.containsKey("p12"));
    assertFalse(p.containsKey("p13"));
    assertTrue(p.containsKey("p14"));
    assertTrue(p.containsKey("p15"));

    assertEquals("hello", p.get("p5"));

    p = new QueryParameters(p);
    assertEquals("hello", p.get("p5"));
  }
}