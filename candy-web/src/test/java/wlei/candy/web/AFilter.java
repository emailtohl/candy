package wlei.candy.web;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wlei.candy.share.util.JsonUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 用于测试的Filter
 * <p>
 * Created by HeLei on 2022/6/18.
 */
class AFilter implements Filter {
  private static final Logger LOGGER = LoggerFactory.getLogger(AFilter.class);

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    CacheRequestWrapper req = new CacheRequestWrapper((HttpServletRequest) request);
    CacheResponseWrapper resp = new CacheResponseWrapper((HttpServletResponse) response);
    ReqParams params = getReqParams(req);
    logIn(params);
    replaceContent(req, params);
    chain.doFilter(req, resp);
    logOut(resp);
  }

  private ReqParams getReqParams(CacheRequestWrapper req) throws IOException {
    try (ServletInputStream in = req.getInputStream()) {
      byte[] contents = IOUtils.toByteArray(in);
      String json = new String(contents, req.getCharacterEncoding());
      ReqParams params = JsonUtil.readValue(json, ReqParams.class);
      params.setIp(req.getRemoteAddr());
      // 模拟获取当前用户信息
      currentUserInfo(params);
      return params;
    }
  }

  /**
   * 模拟获取当前用户信息
   *
   * @param params 将获取的信息填入此参数对象中
   */
  private void currentUserInfo(ReqParams params) {
    params.setUsername("foo");
    params.getAuthorities().add("CREATE");
    params.getAuthorities().add("UPDATE");
    params.getAuthorities().add("DELETE");
  }

  private void replaceContent(CacheRequestWrapper req, ReqParams params) throws UnsupportedEncodingException {
    String json = JsonUtil.writeValue(params);
    req.setContents(json.getBytes(req.getCharacterEncoding()));
  }

  private void logIn(ReqParams params) {
    LOGGER.info(JsonUtil.writeValue(params));
  }

  private void logOut(CacheResponseWrapper resp) throws IOException {
    byte[] content = resp.getContent();
    LOGGER.info(new String(content, resp.getCharacterEncoding()));
  }
}
