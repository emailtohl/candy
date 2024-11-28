package wlei.candy.web;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 跨域访问过滤器 Servlet Filter implementation class CorsFilter
 *
 * @author HeLei
 */
public class CorsFilter implements Filter {
  final String SameSitePattern = "SameSite=((Lax)|(Strict)|(None))+";
  final Set<String> accessControlAllowOrigins = new HashSet<>();
  private volatile String accessControlAllowHeaders = "origin, x-requested-with, Upgrade-Insecure-Requests, Content-Type, Accept, Accept-Language, Content-Language, Access-Control-Allow-Headers, Authorization, XSRF-TOKEN, X-CSRF-TOKEN, X-Requested-With, X-Auth-Token, X-Xsrf-Token, Session-Id";
  private volatile boolean isSameSiteAndSecure = false;

  /**
   * 自定义可以访问的头信息
   *
   * @param accessControlAllowHeaders 允许通过的头信息
   */
  public void setAccessControlAllowHeaders(String accessControlAllowHeaders) {
    this.accessControlAllowHeaders = accessControlAllowHeaders;
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    final HttpServletResponse response = (HttpServletResponse) res;
    final HttpServletRequest request = isSameSiteAndSecure ? new SameSiteNoneWrapper((HttpServletRequest) req) : (HttpServletRequest) req;
    String originHeader = request.getHeader("Origin");
    originHeader = removeSeparatorOfTail(originHeader == null ? "" : originHeader);
    if (accessControlAllowOrigins.contains(originHeader) || accessControlAllowOrigins.contains("*")) {
      response.setHeader("Access-Control-Allow-Origin", originHeader);
      response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
      response.setHeader("Access-Control-Allow-Headers", accessControlAllowHeaders);
      response.setHeader("Access-Control-Max-Age", "3600");
      response.setHeader("Access-Control-Allow-Credentials", "true");
    }
    if ("OPTIONS".equalsIgnoreCase(((HttpServletRequest) req).getMethod())) {
      response.setStatus(HttpServletResponse.SC_OK);
    } else {
      chain.doFilter(request, response);
    }
  }

  @Override
  public void init(FilterConfig config) {
    String accessControlAllowOrigin = config.getInitParameter("Access-Control-Allow-Origin");
    if (StringUtils.hasText(accessControlAllowOrigin)) {
      accessControlAllowOrigins.addAll(Arrays.stream(accessControlAllowOrigin.split(","))
          .map(String::trim).map(this::removeSeparatorOfTail).collect(Collectors.toSet()));
    }
    String b = config.getInitParameter("isSameSiteAndSecure");
    if ("true".equalsIgnoreCase(b)) {
      this.isSameSiteAndSecure = true;
    }
  }

  @Override
  public void destroy() {
  }

  /**
   * 将url中的尾部的分隔符移除
   *
   * @param url 地址
   * @return 移除后的地址
   */
  String removeSeparatorOfTail(String url) {
    if (url.endsWith("/")) {
      return url.substring(0, url.length() - 1);
    }
    return url;
  }

  /**
   * 将结果替换成SameSite=None
   * <p>
   * 开启SameSite=None则一定要开启Secure，而Secure则需要在HTTPS环境下运行
   *
   * @param value 结果
   * @return 含SameSite=None的结果
   */
  String replaceSameSiteNone(String value) {
    if (value == null) {
      return "";
    }
    String result;
    if (value.contains("SameSite")) {
      result = value.replaceAll(SameSitePattern, "SameSite=None");
    } else {
      int i = value.indexOf(";");
      if (i > 0) {
        String left = value.substring(0, i);
        String right = value.substring(i);
        String middle = value.contains("Secure") ? "; SameSite=None" : "; SameSite=None; Secure";
        result = left + middle + right;
      } else {
        result = value + "; SameSite=None; Secure";
      }
    }
    return result;
  }

  /**
   * 以逗号为分隔
   *
   * @param accessControlAllowOrigin 允许通过的服务器站点
   */
  public void setAccessControlAllowOrigin(String accessControlAllowOrigin) {
    if (StringUtils.hasText(accessControlAllowOrigin)) {
      accessControlAllowOrigins.addAll(Arrays.stream(accessControlAllowOrigin.split(","))
          .map(String::trim).map(this::removeSeparatorOfTail).collect(Collectors.toSet()));
    }
  }

  /**
   * HTTPS环境下，Cookie的属性开启SameSite=None以及Secure
   *
   * @param sameSiteAndSecure 是或否
   */
  public void setSameSiteAndSecure(boolean sameSiteAndSecure) {
    isSameSiteAndSecure = sameSiteAndSecure;
  }

  class SameSiteNoneWrapper extends HttpServletRequestWrapper {

    public SameSiteNoneWrapper(HttpServletRequest request) {
      super(request);
    }

    @Override
    public HttpSession getSession(boolean create) {
      HttpSession session = super.getSession(create);
      ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if (ra != null) {
        overwriteSetCookie(ra.getResponse());
      }
      return session;
    }

    @Override
    public String changeSessionId() {
      String newSessionId = super.changeSessionId();
      ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if (ra != null) {
        overwriteSetCookie(ra.getResponse());
      }
      return newSessionId;
    }

    private void overwriteSetCookie(HttpServletResponse response) {
      if (response != null) {
        Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
        boolean firstHeader = true;
        for (String header : headers) { // there can be multiple Set-Cookie attributes
          if (firstHeader) {
            response.setHeader(HttpHeaders.SET_COOKIE, replaceSameSiteNone(header)); // set
            firstHeader = false;
            continue;
          }
          response.addHeader(HttpHeaders.SET_COOKIE, replaceSameSiteNone(header)); // add
        }
      }
    }
  }
}