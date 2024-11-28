package wlei.candy.web;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CacheResponseWrapperTest {

  @Test
  void test() throws IOException, ServletException {
    Filter filter = new Filter() {
      @Override
      public void init(FilterConfig filterConfig) {
      }

      @Override
      public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
          throws IOException, ServletException {
        CacheResponseWrapper resp = new CacheResponseWrapper((HttpServletResponse) response);
        chain.doFilter(request, resp);
        String charset = resp.getCharacterEncoding();
        String respStr = new String(resp.getContent(), charset);
        assertEquals("hello bar", respStr);
      }

      @Override
      public void destroy() {
      }
    };
    MockFilterChain chain = new MockFilterChain(new ServletForTest());
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/hello");
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.addParameter("name", "bar");
    filter.doFilter(request, response, chain);

    Assertions.assertEquals("hello bar", response.getContentAsString());
  }

  private static class ServletForTest extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      resp.setCharacterEncoding("UTF-8");
      String name = req.getParameter("name");
      String content = "hello " + name;
//		resp.getOutputStream().print(content);
      PrintWriter w = resp.getWriter();
      w.print(content);
    }
  }
}