package wlei.candy.web;

import wlei.candy.share.util.JsonUtil;
import wlei.candy.share.util.Result;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * 用于测试的Servlet
 * <p>
 * Created by HeLei on 2022/6/18.
 */
class AServlet extends HttpServlet {
  private ReqParams reqParams;

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String json = req.getReader().lines().collect(Collectors.joining("\n"));
    reqParams = JsonUtil.readValue(json, ReqParams.class);
    String result = JsonUtil.writeValue(Result.ofText(reqParams.getUsername()));
    resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
    resp.getWriter().println(result);
  }

  public ReqParams getReqParams() {
    return reqParams;
  }
}
