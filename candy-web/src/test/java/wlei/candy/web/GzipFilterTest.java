package wlei.candy.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

class GzipFilterTest {

  @Test
  void doFilter() throws IOException, ServletException {
    dotCompress();
    compress();
  }

  private void compress() throws IOException, ServletException {
    GzipFilter gzipFilter = new GzipFilter();
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain(new HttpServlet() {
      @Override
      protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("hello", "world");
        resp.setIntHeader("foo", 200);
        resp.getWriter().println("hello world");
        resp.flushBuffer();
      }
    }, gzipFilter);

    request.addHeader("Accept-Encoding", "gzip");
    filterChain.doFilter(request, response);

    Assertions.assertEquals("gzip", response.getHeader("Content-Encoding"));

    // 再测试调用OutputStream
    filterChain = new MockFilterChain(new HttpServlet() {
      @Override
      protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.addHeader("hello", "world");
        resp.addIntHeader("foo", 200);
        resp.getOutputStream().isReady();
        resp.getOutputStream().flush();
      }
    }, gzipFilter);

    request.addHeader("Accept-Encoding", "gzip");
    filterChain.doFilter(request, response);
  }

  private void dotCompress() throws IOException, ServletException {
    GzipFilter gzipFilter = new GzipFilter();
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();
    gzipFilter.doFilter(request, response, filterChain);
  }
}