package wlei.candy.web;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;

/**
 * 将Servlet的输出流进行GZIP压缩
 *
 * @author HeLei
 */
public class GzipFilter implements Filter {

  private static final String GZIP = "gzip";
  private final Logger logger = LoggerFactory.getLogger(GzipFilter.class);

  /**
   * 初始化方法
   *
   * @param filterConfig 过滤配置
   */
  @Override
  public void init(FilterConfig filterConfig) {
  }

  /**
   * 销毁方法
   */
  @Override
  public void destroy() {
  }

  /**
   * 过滤方法
   *
   * @param request  请求参数
   * @param response ServletResponse
   * @param chain    过滤链
   * @throws IOException      IO异常
   * @throws ServletException Servlet异常
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (!(request instanceof HttpServletRequest && response instanceof HttpServletResponse)) {
      chain.doFilter(request, response);
      return;
    }
    String accept = ((HttpServletRequest) request).getHeader("Accept-Encoding");
    if (accept != null && accept.contains(GZIP)) {
      ((HttpServletResponse) response).setHeader("Content-Encoding", GZIP);
      ResponseWrapper wrapper = new ResponseWrapper((HttpServletResponse) response);
      try {
        chain.doFilter(request, wrapper);
      } finally {
        try {
          wrapper.finish();
        } catch (Exception e) {
          logger.warn(e.getMessage(), e);
        }
      }
    } else {
      chain.doFilter(request, response);
    }
  }

  private static class ResponseWrapper extends HttpServletResponseWrapper {

    private static final String CONTENT_LENGTH = "Content-Length";
    /**
     * 给调用层的是包装后的压缩流
     */
    private GZIPServletOutputStream outputStream;
    /**
     * PrintWriter
     */
    private PrintWriter writer;

    /**
     * Instantiates a new Response wrapper.
     *
     * @param response the response
     */
    ResponseWrapper(HttpServletResponse response) {
      super(response);
    }

    /**
     * 只能提取一次
     *
     * @return 提取信息
     * @throws IOException IO异常
     */
    @Override
    public synchronized ServletOutputStream getOutputStream() throws IOException {
      if (writer != null) {
        throw new IllegalStateException("已经执行过 getWriter()");
      }
      if (outputStream == null) {
        outputStream = new GZIPServletOutputStream(super.getOutputStream());
      }
      return outputStream;
    }

    /**
     * 只能提取一次
     *
     * @return 提取信息
     * @throws IOException IO异常
     */
    @Override
    public synchronized PrintWriter getWriter() throws IOException {
      if (writer == null && outputStream != null) {
        throw new IllegalStateException("已经执行过 getOutputStream()");
      }
      if (writer == null) {
        outputStream = new GZIPServletOutputStream(super.getOutputStream());
        writer = new PrintWriter(new OutputStreamWriter(outputStream, getCharacterEncoding()));
      }
      return writer;
    }

    @Override
    public synchronized void flushBuffer() throws IOException {
      if (writer != null) {
        writer.flush();
      } else if (outputStream != null) {
        outputStream.flush();
      }
      super.flushBuffer();
    }

    @Override
    public void setContentLength(int length) {
    }

    @Override
    public void setContentLengthLong(long length) {
    }

    @Override
    public void setHeader(String name, String value) {
      if (!CONTENT_LENGTH.equalsIgnoreCase(name)) {
        super.setHeader(name, value);
      }
    }

    @Override
    public void addHeader(String name, String value) {
      if (!CONTENT_LENGTH.equalsIgnoreCase(name)) {
        super.setHeader(name, value);
      }
    }

    @Override
    public void setIntHeader(String name, int value) {
      if (!CONTENT_LENGTH.equalsIgnoreCase(name)) {
        super.setIntHeader(name, value);
      }
    }

    @Override
    public void addIntHeader(String name, int value) {
      if (!CONTENT_LENGTH.equalsIgnoreCase(name)) {
        super.setIntHeader(name, value);
      }
    }

    /**
     * Finish.
     *
     * @throws IOException the io exception
     */
    public synchronized void finish() throws IOException {
      if (writer != null) {
        writer.close();
      } else if (outputStream != null) {
        outputStream.finish();
      }
    }
  }

  /**
   * 包裹ServletOutputStream的GZIP压缩流 由于继承了ServletOutputStream，所以ServletOutputStream的行为将由持有的两个流实现
   */
  private static class GZIPServletOutputStream extends ServletOutputStream {

    /**
     * 持有原ServletOutputStream，isReady和setWriteListener需要使用
     */
    private final ServletOutputStream servletOutputStream;
    /**
     * 装饰ServletOutputStream的GZIP流，写入都用它来压缩
     */
    private final GZIPOutputStream gzipStream;

    /**
     * Instantiates a new Gzip servlet output stream.
     *
     * @param servletOutputStream the servlet output stream
     * @throws IOException the io exception
     */
    GZIPServletOutputStream(ServletOutputStream servletOutputStream) throws IOException {
      this.servletOutputStream = servletOutputStream;
      gzipStream = new GZIPOutputStream(servletOutputStream);
    }

    @Override
    public boolean isReady() {
      return servletOutputStream.isReady();
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
      servletOutputStream.setWriteListener(writeListener);
    }

    @Override
    public void write(int b) throws IOException {
      gzipStream.write(b);
    }

    @Override
    public void close() throws IOException {
      gzipStream.close();
    }

    @Override
    public void flush() throws IOException {
      gzipStream.flush();
    }

    /**
     * Finish.
     *
     * @throws IOException the io exception
     */
    public void finish() throws IOException {
      gzipStream.finish();
    }
  }
}
