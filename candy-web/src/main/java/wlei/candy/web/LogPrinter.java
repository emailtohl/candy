package wlei.candy.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Created by helei on 2023/10/26
 */
public interface LogPrinter {
  /**
   * @param req 判断对象
   * @return 是否读取请求体
   */
  boolean isReadRequestBody(HttpServletRequest req);

  /**
   * @param req  判断对象
   * @param resp 判断对象
   * @return 是否读取响应体
   */
  boolean isReadResponseBody(HttpServletRequest req, HttpServletResponse resp);

  /**
   * 打印输入信息
   *
   * @param req 请求
   * @param tid 事务id
   */
  void printIn(HttpServletRequest req, String tid);

  /**
   * 打印输出信息
   *
   * @param resp  响应
   * @param error 没有异常信息则为null
   * @param tid   事务id
   */
  void printOut(HttpServletResponse resp, String error, String tid);

  /**
   * 清理
   */
  void clear();
}
