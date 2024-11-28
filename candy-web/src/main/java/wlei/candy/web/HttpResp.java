package wlei.candy.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.HttpCookie;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * 封装http的响应
 * <p>
 * Created by helei on 2023/9/13
 */
public class HttpResp {
  /**
   * 调用方传入的事务号
   */
  public static final String HEADER_INVOKER_TID = "I-Tid";
  public static final String HEADER_WWW_AUTHENTICATE = "WWW-Authenticate";
  private static final Logger LOGGER = LoggerFactory.getLogger(HttpResp.class);
  private static final ObjectMapper OM = new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
  /**
   * http的状态码
   */
  private int httpStatus;
  /**
   * 响应头的信息，只支持单key，不支持复数key
   */
  private Map<String, String> headers;
  /**
   * 响应体
   */
  private byte[] body;

  /**
   * 默认构造
   */
  public HttpResp() {
    // 状态码默认200
    this.httpStatus = 200;
    headers = new HashMap<>();
    body = new byte[]{};
  }

  /**
   * 入参构造
   *
   * @param httpStatus http的状态码
   * @param headers    响应的header
   * @param body       响应体
   */
  public HttpResp(int httpStatus, Map<String, String> headers, byte[] body) {
    this.httpStatus = httpStatus;
    this.headers = headers;
    this.body = body;
  }

  /**
   * 用解析json的方式获取响应的内容
   *
   * @param valueType 要解析的类型
   * @param <T>       类型参数
   * @return 结果
   * @throws IllegalStateException 如果json解析失败，会抛出此异常，可能是json格式不对，也可能是提供的参数类型不对
   */
  public <T> T parseJson(Class<T> valueType) throws IllegalStateException {
    try {
      byte[] noNullBody = (this.body == null || this.body.length == 0) ? "{}".getBytes(bodyCharset()) : this.body;
      return OM.readValue(noNullBody, valueType);
    } catch (Exception e) {
      String s = bodyAsString();
      String msg = String.format("parse json failed，body content is【%s】， cause【%s】", s, e.getMessage());
      LOGGER.debug(msg, e);
      throw new IllegalStateException(msg);
    }
  }

  /**
   * @return 获取cookie的kv结构
   */
  public Optional<String> cookieKv() {
    Optional<String> o = header("Set-Cookie");
    return o.flatMap(s -> HttpCookie.parse(s).stream()
        .filter(c -> c.getName().toLowerCase().contains("session"))
        .map(HttpCookie::toString)
        .map(String::trim)
        .findFirst());
  }

  /**
   * @return 将响应体按字符串形式返回，如果没有响应体，则返回空字符串
   */
  String bodyAsString() {
    String s;
    try {
      s = new String(this.body, bodyCharset());
    } catch (Exception e) {
      LOGGER.trace(e.getMessage());
      s = "";
    }
    return s;
  }

  /**
   * @return 根据响应头Content-Type获取响应体的编码格式，如果没有获取到相关头信息，则默认返回UTF-8
   */
  String bodyCharset() {
    Optional<String> o = header("Content-Type");
    if (!o.isPresent()) {
      return StandardCharsets.UTF_8.name();
    }
    String contentType = o.get();
    o = Arrays.stream(contentType.split(";"))
        .filter(kv -> kv.toLowerCase().contains("charset"))
        .findFirst();
    if (!o.isPresent()) {
      return StandardCharsets.UTF_8.name();
    }
    String kv = o.get();
    String[] pair = kv.split("=");
    if (pair.length != 2) {
      return StandardCharsets.UTF_8.name();
    }
    return StringUtils.hasText(pair[1]) ? pair[1] : StandardCharsets.UTF_8.name();
  }

  /**
   * @param headerName 响应头的名
   * @return 对应的头信息，不支持多个相同头信息的值
   */
  Optional<String> header(String headerName) {
    Optional<String> o = this.headers.keySet().stream()
        .filter(k -> k.equalsIgnoreCase(headerName))
        .findFirst();
    return o.map(this.headers::get);
  }

  /**
   * @return 解析错误，如果有响应体，则将响应体的错误解析出来，否则查找响应头中是否有WWW-Authenticate相关错误
   */
  SimpleResult parseError() {
    if (this.body != null && this.body.length > 0) {
      return errorFromBody();
    }
    return errorFromHeader();
  }

  /**
   * @return 从响应体解析错误
   */
  SimpleResult errorFromBody() {
    try {
      return parseJson(SimpleResult.class);
    } catch (IllegalStateException ignore) {
      String cause = String.format("parse json fail:【%s】", bodyAsString());
      return SimpleResult.fail(cause);
    }
  }

  /**
   * @return 从响应头的WWW-Authenticate中解析错误
   */
  SimpleResult errorFromHeader() {
    String tid = header(HEADER_INVOKER_TID).orElse("");
    String errorMessage = header(HEADER_WWW_AUTHENTICATE).orElse("");
    return SimpleResult.fail(String.format("【%s】%s", tid, errorMessage));
  }

  /**
   * @param wwwAuthenticate WWW-Authenticate中的信息
   * @return 是否无效令牌
   */
  boolean isInvalidToken(String wwwAuthenticate) {
    if (!StringUtils.hasText(wwwAuthenticate)) {
      return false;
    }
    String lowerCase = wwwAuthenticate.toLowerCase();
    return lowerCase.contains("attempting to decode the jwt") || lowerCase.startsWith("bearer");
  }

  /**
   * @param wwwAuthenticate WWW-Authenticate中的信息
   * @return 令牌是否已过期
   */
  boolean isTokenExpired(String wwwAuthenticate) {
    if (!StringUtils.hasText(wwwAuthenticate)) {
      return false;
    }
    String lowerCase = wwwAuthenticate.toLowerCase();
    return lowerCase.contains("jwt") && lowerCase.contains("expired");
  }

  /**
   * @return 是否有报错
   */
  public boolean hasError() {
    return httpStatus >= 300;
  }

  /**
   * @return 是否需要重新登录
   */
  public boolean requireReLogin() {
    SimpleResult r = parseError();
    return isTokenExpired(r.getErrorMessage()) || isInvalidToken(r.getErrorMessage());
  }

  /**
   * @return 克隆
   */
  @Override
  public HttpResp clone() {
    HttpResp copy = new HttpResp();
    copy.headers = new HashMap<>(this.headers);
    copy.body = this.body == null ? null : this.body.clone();
    return copy;
  }

  @Override
  public String toString() {
    return String.valueOf(httpStatus);
  }

  /**
   * @return http的状态码
   */
  public int getHttpStatus() {
    return httpStatus;
  }

  public HttpResp setHttpStatus(int httpStatus) {
    this.httpStatus = httpStatus;
    return this;
  }

  /**
   * @return http响应的header信息
   */
  public Map<String, String> getHeaders() {
    return headers;
  }

  public HttpResp setHeaders(Map<String, String> headers) {
    this.headers = headers;
    return this;
  }

  /**
   * @return http响应的body
   */
  public byte[] getBody() {
    return body;
  }

  public HttpResp setBody(byte[] body) {
    this.body = body;
    return this;
  }
}
