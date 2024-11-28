package wlei.candy.web;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 针对Http响应体的测试
 * <p>
 * Created by helei on 2023/9/13
 */
class HttpRespTest {
  /**
   * 无效 TOKEN
   */
  private static final String INVALID_SIGNATURE = "Bearer error=\"invalid_token\"error_description=\"An error occurred while attempting to decode the Jwt: Signed JWT rejected: Invalid signature\"error_uri=\"https://tools.ietf.org/html/rfc6750#section-3.1\"";
  /**
   * TOKEN 过期
   */
  private static final String TOKEN_EXPIRED = "Bearer error=\"invalid_token\"error_description=\"An error occurred while attempting to decode the Jwt: Jwt expired at 2023-08-23T02:38:38Z\"error_uri=\"https://tools.ietf.org/html/rfc6750#section-3.1\"";
  private static final String SET_COOKIE = "SESSION=NTA1MWIxMzUtMDU5Ny00NDcwLWEzNTQtNDIxNDkxOGU3ZmE5; Path=/kccv2; HttpOnly; SameSite=Lax";
  private static final String TID = UUID.randomUUID().toString();

  @Test
  void parseJson() {
    HttpResp r = new HttpResp();
    SimpleResult simpleResult = r.parseJson(SimpleResult.class);
    assertNotNull(simpleResult);
    r.setBody("{\"success\":true}".getBytes(StandardCharsets.UTF_8));
    simpleResult = r.parseJson(SimpleResult.class);
    assertTrue(simpleResult.getSuccess());

    r.setBody("xxx".getBytes(StandardCharsets.UTF_8));
    assertThrows(IllegalStateException.class, () -> r.parseJson(SimpleResult.class));

    assertTrue(r.setBody("{}".getBytes(StandardCharsets.UTF_8)).setHeaders(new HashMap<>()).getHeaders().isEmpty());
    r.setHttpStatus(200);
    assertEquals(200, r.getHttpStatus());
  }

  @Test
  void bodyCharset() {
    HttpResp r = new HttpResp();
    assertEquals(StandardCharsets.UTF_8.name(), r.bodyCharset());

    r = new HttpResp();
    r.getHeaders().put("Content-Type", "application/json;charset=UTF-8");
    assertEquals(StandardCharsets.UTF_8.name(), r.bodyCharset());

    r = new HttpResp();
    r.getHeaders().put("Content-Type", "application/json;");
    assertEquals(StandardCharsets.UTF_8.name(), r.bodyCharset());

    r = new HttpResp();
    r.getHeaders().put("Content-Type", "charset=UTF-8");
    assertEquals(StandardCharsets.UTF_8.name(), r.bodyCharset());

    r = new HttpResp();
    r.getHeaders().put("Content-Type", "application/json;charset=");
    assertEquals(StandardCharsets.UTF_8.name(), r.bodyCharset());

    r = new HttpResp();
    r.getHeaders().put("Content-Type", "");
    assertEquals(StandardCharsets.UTF_8.name(), r.bodyCharset());
  }

  @Test
  void bodyAsString() {
    HttpResp r = new HttpResp();
    r.getHeaders().put("Content-Type", "text/plain;charset=xxx");
    r.setBody(new byte[]{1, 2, 3});
    String s = r.bodyAsString();
    assertNotNull(s);
    System.out.println(r);
  }

  @Test
  void cookieKv() {
    HttpResp r = new HttpResp();
    r.getHeaders().put("Set-Cookie", SET_COOKIE);
    Optional<String> o = r.cookieKv();
    assertTrue(o.isPresent());
    assertEquals("SESSION=NTA1MWIxMzUtMDU5Ny00NDcwLWEzNTQtNDIxNDkxOGU3ZmE5", o.get());

    r = new HttpResp();
    r.getHeaders().put("Set-Cookie", "jsessionid=NTA1MWIxMzUtMDU5Ny00NDcwLWEzNTQtNDIxNDkxOGU3ZmE5; Path=/kccv2; HttpOnly; SameSite=Lax");
    o = r.cookieKv();
    assertTrue(o.isPresent());
    assertEquals("jsessionid=NTA1MWIxMzUtMDU5Ny00NDcwLWEzNTQtNDIxNDkxOGU3ZmE5", o.get());

  }

  @Test
  void getHeader() {
    HttpResp r = new HttpResp();
    r.getHeaders().put("Set-Cookie", SET_COOKIE);
    Optional<String> o = r.header("Set-Cookie");
    assertTrue(o.isPresent());
    o = r.header("set-cookie");
    assertTrue(o.isPresent());
    o = r.header("SET-COOKIE");
    assertTrue(o.isPresent());
    o = r.header("Content-Type");
    assertFalse(o.isPresent());
  }

  @Test
  void parseError() {
    HttpResp r = new HttpResp();
    r.setHttpStatus(500).getHeaders().put(HttpResp.HEADER_INVOKER_TID, TID);
    r.getHeaders().put(HttpResp.HEADER_WWW_AUTHENTICATE, "Bearer");

    SimpleResult sr = r.parseError();
    assertTrue(sr.getErrorMessage().contains("Bearer"));

    r.setBody("{\"success\":false,\"errorMessage\":\"auth\"}".getBytes(StandardCharsets.UTF_8));
    sr = r.parseError();
    assertTrue(sr.getErrorMessage().contains("auth"));
  }

  @Test
  void errorFromBody() {
    HttpResp r = new HttpResp();
    r.setHttpStatus(500);
    r.getHeaders().put(HttpResp.HEADER_INVOKER_TID, TID);
    r.setBody("{\"success\":false,\"errorMessage\":\"auth\"}".getBytes(StandardCharsets.UTF_8));
    SimpleResult sr = r.errorFromBody();
    assertEquals("auth", sr.getErrorMessage());

    r.setBody("xxx".getBytes(StandardCharsets.UTF_8));
    sr = r.errorFromBody();
    assertTrue(sr.getErrorMessage().contains("xxx"));
  }

  @Test
  void errorFromHeader() {
    HttpResp r = new HttpResp();
    r.setHttpStatus(401);
    r.getHeaders().put(HttpResp.HEADER_INVOKER_TID, TID);
    r.getHeaders().put(HttpResp.HEADER_WWW_AUTHENTICATE, "Bearer");
    SimpleResult sr = r.errorFromHeader();
    assertTrue(sr.getErrorMessage().contains("Bearer"));

    // ------------
    r = new HttpResp();
    r.setHttpStatus(401);
    r.getHeaders().put(HttpResp.HEADER_INVOKER_TID, TID);
    r.getHeaders().put(HttpResp.HEADER_WWW_AUTHENTICATE, TOKEN_EXPIRED);
    sr = r.errorFromHeader();
    assertTrue(sr.getErrorMessage().contains("invalid"));
  }

  @Test
  void isInvalidToken() {
    HttpResp r = new HttpResp();
    assertFalse(r.isInvalidToken(""));
    assertTrue(r.isInvalidToken("Bearer"));
    assertTrue(r.isInvalidToken(INVALID_SIGNATURE));
    assertTrue(r.isInvalidToken(TOKEN_EXPIRED));
  }

  @Test
  void isTokenExpired() {
    HttpResp r = new HttpResp();
    assertFalse(r.isTokenExpired(""));
    assertFalse(r.isTokenExpired("Bearer"));
    assertFalse(r.isTokenExpired(INVALID_SIGNATURE));
    assertTrue(r.isTokenExpired(TOKEN_EXPIRED));
  }

  @Test
  void getCode() {
    HttpResp r = new HttpResp();
    r.setHttpStatus(500);
    r.getHeaders().put(HttpResp.HEADER_INVOKER_TID, TID);
  }

  @Test
  void getErrorMessage() {
    HttpResp r = new HttpResp();
    r.setHttpStatus(500);
    r.getHeaders().put(HttpResp.HEADER_INVOKER_TID, TID);
  }

  @Test
  void hasError() {
    HttpResp r = new HttpResp();
    r.setHttpStatus(500);
    assertTrue(r.hasError());
  }

  @Test
  void requireReLogin() {
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpResp.HEADER_INVOKER_TID, TID);
    headers.put(HttpResp.HEADER_WWW_AUTHENTICATE, TOKEN_EXPIRED);
    HttpResp r = new HttpResp(401, headers, null);
    r.setHttpStatus(401);

    assertTrue(r.requireReLogin());
  }

  @Test
  void testClone() {
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpResp.HEADER_INVOKER_TID, TID);
    HttpResp r = new HttpResp(401, headers, new byte[]{1, 2, 3});

    HttpResp other = r.clone();
    other.getHeaders().put(HttpResp.HEADER_INVOKER_TID, "xxx");
    other.setBody(new byte[]{4, 5, 6});
    assertEquals(TID, r.header(HttpResp.HEADER_INVOKER_TID).orElseThrow(IllegalStateException::new));
    assertArrayEquals(new byte[]{1, 2, 3}, r.getBody());
  }
}