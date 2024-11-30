package wlei.candy.web;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.StringUtils;
import wlei.candy.share.util.JsonUtil;
import wlei.candy.share.util.Result;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 对整个过滤链的测试
 * <p>
 * Created by HeLei on 2022/6/18.
 */
class FilterLayerTest {
  MockHttpServletRequest mReq;
  CacheRequestWrapper cReq;
  MockHttpServletResponse mResp;
  MockFilterChain chain;
  AServlet aServlet;
  AFilter aFilter;
  AReadListener aReadListener;

  @BeforeEach
  void setUp() throws IOException {
    aReadListener = new AReadListener();
    mReq = new MockHttpServletRequest();
    mReq.setCharacterEncoding(StandardCharsets.UTF_8.name());
    mReq.setContent(JsonUtil.writeValue(new ReqParams("hello")).getBytes(StandardCharsets.UTF_8));
    cReq = new CacheRequestWrapper(mReq);
    cReq.getInputStream().setReadListener(aReadListener);
    mResp = new MockHttpServletResponse();
    aServlet = new AServlet();
    aFilter = new AFilter();
    chain = new MockFilterChain(aServlet, aFilter);
  }

  @Test
  void test() throws ServletException, IOException {
    aFilter.doFilter(cReq, mResp, chain);
    ReqParams params = aServlet.getReqParams();
    assertTrue(StringUtils.hasText(params.getIp()));
    assertEquals("foo", params.getUsername());
    assertTrue(params.getAuthorities().contains("CREATE"));
    assertTrue(params.getAuthorities().contains("UPDATE"));
    assertTrue(params.getAuthorities().contains("DELETE"));
    String json = mResp.getContentAsString();
    Result result = JsonUtil.readValue(json, Result.class);
    Assertions.assertEquals("foo", result.getText());
    assertTrue(aReadListener.isOnAllDataReadDone());
    assertTrue(aReadListener.isOnDataAvailableDone());
  }
}
