package wlei.candy.share.util;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by helei on 2022/6/8
 */
class XmlEleTest {

  @Test
  void from() throws IOException {
    ClassPathResource r = new ClassPathResource("logback-test.xml");
    String s = IOUtils.toString(r.getURL(), StandardCharsets.UTF_8);
    System.out.println(s);
    XmlEle x1 = XmlEle.from(s);
    s = x1.toString();
    System.out.println(s);
    XmlEle x2 = XmlEle.from(s);
    assertEquals(x1.hashCode(), x2.hashCode());
    assertEquals(x1, x2);
  }
}