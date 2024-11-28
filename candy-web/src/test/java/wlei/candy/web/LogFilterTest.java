package wlei.candy.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by helei on 2023/7/23
 */
class LogFilterTest {
  private MockHttpServletRequest mockReq;
  private MockHttpServletResponse mockResp;
  private MockFilterChain chain;
  private final Supplier<String> tidGen = () -> UUID.randomUUID().toString();
  private final Predicate<HttpServletRequest> doNoThings = (HttpServletRequest req) -> req.getServletPath().startsWith("/static");
  private final BiConsumer<HttpServletRequest, String> printIn = ((req, tid) -> new JSONLog(LogDir.IN_REQUEST).setPath(req.getServletPath()).setMethod(req.getMethod()).setClientIP(req.getRemoteAddr()).setSessionId(req.getRequestedSessionId()).setUsername("foo").setTid(tid).print());

  @BeforeEach
  void setUp() throws UnsupportedEncodingException {
    mockReq = new MockHttpServletRequest();
    mockReq.setRemoteAddr("10.1.2.3");
    mockReq.addHeader("Content-Type", "application/json;charset=utf-8");
    mockReq.addHeader("Authorization", "Bearer jwt");
    mockResp = new MockHttpServletResponse();
    mockResp.setCharacterEncoding(StandardCharsets.UTF_8.name());
    mockResp.setContentType("application/json");
    mockResp.getWriter().write("{\"hello\":\"foo\"}");
    chain = new MockFilterChain(new AServlet());
  }

  @Test
  void normal() throws ServletException, IOException {
    LogPrinter logPrinter = new LogPrinter() {
      @Override
      public boolean isReadRequestBody(HttpServletRequest req) {
        return true;
      }

      @Override
      public boolean isReadResponseBody(HttpServletRequest req, HttpServletResponse resp) {
        return true;
      }

      @Override
      public void printIn(HttpServletRequest req, String tid) {
        printIn.accept(req, tid);
      }

      @Override
      public void printOut(HttpServletResponse resp, String error, String tid) {
        new JSONRespLog(LogDir.IN_RESPONSE, StringUtils.hasText(error) ? "-1" : "0").print();
      }

      @Override
      public void clear() {
        ThreadLog.clearThreadLocal();
      }
    };
    LogFilter logFilter = new LogFilter().setLogPrinter(logPrinter).setTidGen(tidGen).setDoNoThings(doNoThings);
    mockReq.setContent("{\"username\":\"foo\"}".getBytes(StandardCharsets.UTF_8));
    logFilter.doFilter(mockReq, mockResp, chain);
  }

  @Test
  void doNothings() throws ServletException, IOException {
    LogFilter logFilter = new LogFilter().setTidGen(tidGen).setDoNoThings(doNoThings);
    mockReq.setServletPath("/static/index.html");
    logFilter.doFilter(mockReq, mockResp, new MockFilterChain());
  }

  @Test
  void filterError() {
    LogPrinter logPrinter = new LogPrinter() {
      @Override
      public boolean isReadRequestBody(HttpServletRequest req) {
        return true;
      }

      @Override
      public boolean isReadResponseBody(HttpServletRequest req, HttpServletResponse resp) {
        return true;
      }

      @Override
      public void printIn(HttpServletRequest req, String tid) {
        printIn.accept(req, tid);
      }

      @Override
      public void printOut(HttpServletResponse resp, String error, String tid) {
        new JSONRespLog(LogDir.IN_RESPONSE, StringUtils.hasText(error) ? "-1" : "0").print();
      }

      @Override
      public void clear() {
        ThreadLog.clearThreadLocal();
      }
    };
    LogFilter logFilter = new LogFilter().setLogPrinter(logPrinter).setTidGen(tidGen).setDoNoThings(doNoThings);
    mockReq.setContent("{\"name\":\"foo\"}".getBytes(StandardCharsets.UTF_8));
    assertThrows(IllegalArgumentException.class, () -> logFilter.doFilter(mockReq, mockResp, chain));
  }

  @Test
  void logPrinterError() throws ServletException, IOException {
    LogPrinter logPrinter = new LogPrinter() {
      @Override
      public boolean isReadRequestBody(HttpServletRequest req) {
        throw new IllegalStateException("isReadRequestBody error");
      }

      @Override
      public boolean isReadResponseBody(HttpServletRequest req, HttpServletResponse resp) {
        throw new IllegalStateException("isReadResponseBody error");
      }

      @Override
      public void printIn(HttpServletRequest req, String tid) {
        throw new IllegalStateException("printIn error");
      }

      @Override
      public void printOut(HttpServletResponse resp, String error, String tid) {
        throw new IllegalStateException("printOut error");
      }

      @Override
      public void clear() {
        throw new IllegalStateException("clear error");
      }
    };
    LogFilter logFilter = new LogFilter().setLogPrinter(logPrinter).setTidGen(tidGen).setDoNoThings(doNoThings);
    mockReq.setContent("{\"username\":\"foo\"}".getBytes(StandardCharsets.UTF_8));
    logFilter.doFilter(mockReq, mockResp, chain);
  }

  @Test
  void notHttp() throws ServletException, IOException {
    ServletRequest servletRequest = Mockito.mock(ServletRequest.class);
    ServletResponse servletResponse = Mockito.mock(ServletResponse.class);
    FilterChain filterChain = Mockito.mock(FilterChain.class);
    new LogFilter().doFilter(servletRequest, servletResponse, filterChain);
  }

}