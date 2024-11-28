package wlei.candy.share.util;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by HeLei on 2021/8/26.
 */
class TripleTest {

  @Test
  void test() throws IOException, ClassNotFoundException {
    Triple<Long, String, Boolean> p = new Triple<>(1L, "hello world", true);
    System.out.println(p);
    String s = JsonUtil.writeValue(p);
    assertEquals("{\"left\":1,\"middle\":\"hello world\",\"right\":true}", s);

    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      ObjectOutputStream oos = new ObjectOutputStream(out);
      oos.writeObject(p);
      try (ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray())) {
        ObjectInputStream ois = new ObjectInputStream(in);
        Object o = ois.readObject();
        assertEquals(p, o);
      }
    }
    Set<Triple<Long, String, Boolean>> set = new HashSet<>();
    set.add(p);
    assertEquals(1, set.size());
    set.add(p);
    assertEquals(1, set.size());
  }
}