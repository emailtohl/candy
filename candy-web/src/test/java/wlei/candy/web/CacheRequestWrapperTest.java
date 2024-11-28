package wlei.candy.web;

import jakarta.servlet.ServletInputStream;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by HeLei on 2021/4/9.
 */
class CacheRequestWrapperTest {

  @Test
  void test() throws IOException {
    byte[] body = "body world".getBytes(StandardCharsets.UTF_8);
    MockHttpServletRequest req = new MockHttpServletRequest();
    req.setContent(body);
    CacheRequestWrapper wrapper = new CacheRequestWrapper(req);
    // 第一次读取，关闭后，数据流重置
    AReadListener listener = new AReadListener();
    try (ServletInputStream in = wrapper.getInputStream()) {
      in.setReadListener(listener);
      byte[] bytes = IOUtils.toByteArray(in);
      assertArrayEquals(body, bytes);
      assertTrue(listener.isOnAllDataReadDone());
    }
    assertTrue(listener.isOnDataAvailableDone());
    // 第二次读取，数据流由于重置，仍然可以获取到
    listener = new AReadListener();
    try (ServletInputStream in = wrapper.getInputStream()) {
      in.setReadListener(listener);
      byte[] bytes = IOUtils.toByteArray(in);
      assertArrayEquals(body, bytes);
      assertTrue(listener.isOnAllDataReadDone());
    }
    assertTrue(listener.isOnDataAvailableDone());
  }

}