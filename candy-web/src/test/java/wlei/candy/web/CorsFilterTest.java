package wlei.candy.web;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CorsFilterTest {

  @Test
  void doFilter() throws IOException, ServletException {
    CorsFilter filter = new CorsFilter();
    filter.setAccessControlAllowOrigin("http://localhost, http://localhost:8080, http://localhost:4200");
    filter.setAccessControlAllowHeaders("Content-Type, Access-Control-Allow-Headers, Authorization, X-CSRF-TOKEN, X-Requested-With, X-Auth-Token, X-Xsrf-Token, XSRF-TOKEN");
    filter.setSameSiteAndSecure(true);
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();
    request.addHeader("Origin", "http://localhost");
    filter.doFilter(request, response, filterChain);
    String header = response.getHeader("Access-Control-Allow-Methods");
    assertEquals("GET, POST, PUT, DELETE, OPTIONS", header);
  }

  @Test
  void init() {
    MockFilterConfig config = new MockFilterConfig();
    config.addInitParameter("Access-Control-Allow-Origin", "*");
    CorsFilter filter = new CorsFilter();
    filter.init(config);
  }

  @Test
  void replaceSameSiteNone() {
    CorsFilter filter = new CorsFilter();
    filter.setSameSiteAndSecure(true);
    String t = "XSRF-TOKEN=b9e66ba4-97bd-42c2-9011-f7c82f6e229b; Path=/; SameSite=Lax; Secure";
    assertEquals("XSRF-TOKEN=b9e66ba4-97bd-42c2-9011-f7c82f6e229b; Path=/; SameSite=None; Secure",
        t.replaceAll(filter.SameSitePattern, "SameSite=None"));

    String s = filter.replaceSameSiteNone(t);
    assertEquals("XSRF-TOKEN=b9e66ba4-97bd-42c2-9011-f7c82f6e229b; Path=/; SameSite=None; Secure", s);

    t = "JSESSIONID=EA6818D5C304FFCE0071AB275263A306; Path=/; Secure; HttpOnly";
    s = filter.replaceSameSiteNone(t);
    assertEquals("JSESSIONID=EA6818D5C304FFCE0071AB275263A306; SameSite=None; Path=/; Secure; HttpOnly", s);
  }

  @Test
  void removeSeparatorOfTail() {
    CorsFilter filter = new CorsFilter();
    String url = "http://localhost:8080/";
    assertEquals("http://localhost:8080", filter.removeSeparatorOfTail(url));
    url = "http://localhost:8080";
    assertEquals("http://localhost:8080", filter.removeSeparatorOfTail(url));
  }
}