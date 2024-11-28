package wlei.candy.web;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 用于记录输入输出的日志
 * <p>
 * Created by HeLei on 2023/7/23
 */
public class LogFilter implements Filter {
  private static final Logger LOGGER = LoggerFactory.getLogger(LogFilter.class);
  private static final String TID = "tid";
  private LogPrinter logPrinter;
  private Supplier<String> tidGen;

  private Predicate<HttpServletRequest> doNoThings;

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    if (!(servletRequest instanceof HttpServletRequest) || !(servletResponse instanceof HttpServletResponse)) {
      String tid = Optional.ofNullable(tidGen).map(Supplier::get).orElse("");
      MDC.put(TID, tid);
      try {
        filterChain.doFilter(servletRequest, servletResponse);
      } finally {
        MDC.remove(TID);
      }
      return;
    }
    HttpServletRequest req = (HttpServletRequest) servletRequest;
    HttpServletResponse resp = (HttpServletResponse) servletResponse;
    String tid = getTid(req);
    MDC.put(TID, tid);
    resp.addHeader(TID, tid);
    if (doNoThings != null && doNoThings.test(req)) {
      try {
        filterChain.doFilter(req, resp);
      } finally {
        MDC.remove(TID);
      }
      return;
    }
    if (isReadRequestBody(req)) {
      req = new CacheRequestWrapper(req);
    }
    if (isReadResponseBody(req, resp)) {
      resp = new CacheResponseWrapper(resp);
    }
    logRequest(req);
    String error = null;
    try {
      filterChain.doFilter(req, resp);
    } catch (Exception e) {
      error = e.getMessage();
      LOGGER.warn(error);
      throw e;
    } finally {
      logResponse(resp, error);
      MDC.remove(TID);
      clear();
    }
  }

  private boolean isReadRequestBody(HttpServletRequest req) {
    try {
      return Optional.ofNullable(logPrinter).map(p -> p.isReadRequestBody(req)).orElse(false);
    } catch (Exception e) {
      LOGGER.warn(e.getMessage());
      return false;
    }
  }

  private boolean isReadResponseBody(HttpServletRequest req, HttpServletResponse resp) {
    try {
      return Optional.ofNullable(logPrinter).map(p -> p.isReadResponseBody(req, resp)).orElse(false);
    } catch (Exception e) {
      LOGGER.warn(e.getMessage());
      return false;
    }
  }

  private String getTid(HttpServletRequest req) {
    String tid = req.getHeader(HttpResp.HEADER_INVOKER_TID);
    if (!StringUtils.hasText(tid)) {
      tid = Optional.ofNullable(tidGen).map(Supplier::get).orElse("");
    }
    return tid;
  }

  private void logRequest(HttpServletRequest req) {
    try {
      Optional.ofNullable(logPrinter).ifPresent(p -> p.printIn(req, MDC.get(TID)));
    } catch (Exception e) {
      LOGGER.warn(e.getMessage());
    }
  }

  private void logResponse(HttpServletResponse resp, String error) {
    try {
      Optional.ofNullable(logPrinter).ifPresent(p -> p.printOut(resp, error, MDC.get(TID)));
    } catch (Exception e) {
      LOGGER.warn(e.getMessage());
    }
  }

  private void clear() {
    try {
      Optional.ofNullable(logPrinter).ifPresent(LogPrinter::clear);
    } catch (Exception e) {
      LOGGER.warn(e.getMessage());
    }
  }

  public LogFilter setLogPrinter(LogPrinter logPrinter) {
    this.logPrinter = logPrinter;
    return this;
  }

  public LogFilter setTidGen(Supplier<String> tidGen) {
    this.tidGen = tidGen;
    return this;
  }

  public LogFilter setDoNoThings(Predicate<HttpServletRequest> doNoThings) {
    this.doNoThings = doNoThings;
    return this;
  }
}
