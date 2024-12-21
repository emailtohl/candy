package wlei.candy.share.util;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Author: HeLei
 * Date: 2024/12/21
 */
class LongIdTest {

  @Test
  void getUniqueId() {
    String str = "Hello World\nHello World\nHello World\nHello World\nHello World\nHello World\nHello World\nHello World\nHello World\nHello World\nHello World\nHello World\n";
    Set<Long> s = new HashSet<>();
    for (int i = 0; i < 100; i++) {
      s.add(LongId.getUniqueId(str));
    }
    assertEquals(1, s.size());
  }
}