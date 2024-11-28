package wlei.candy.web;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 包装HttpServletResponse，以便于能从其提取出相应的内容
 *
 * @author HeLei
 */
public class CacheResponseWrapper extends HttpServletResponseWrapper {

  private OutputStreamWrapper streamWrapper;
  private PrintWriter writer;

  public CacheResponseWrapper(HttpServletResponse response) {
    super(response);
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    buildOnce();
    return streamWrapper;
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    buildOnce();
    return writer;
  }

  private synchronized void buildOnce() throws IOException {
    // 创建数据流的同时创建PrintWriter，可以通过PrintWriter来判断是否已创建了数据流
    if (writer == null) {
      // 输出流为自定义的，当此流的write方法被调用时，会将数据存储一份到内存中
      streamWrapper = new OutputStreamWrapper(super.getOutputStream());
      // PrintWriter使用自定义的数据流
      writer = new PrintWriter(streamWrapper);
    }
  }

  /**
   * @return 返回内存中的内容
   * @throws IOException 网络中断
   */
  public byte[] getContent() throws IOException {
    buildOnce();
    // writer有自己的缓存，获取内容前需先刷新
    writer.flush();
    return streamWrapper.getByteArrayOutputStream().toByteArray();
  }

  private static class OutputStreamWrapper extends ServletOutputStream {
    private final ServletOutputStream outputStream;
    private final ByteArrayOutputStream memoryStream = new ByteArrayOutputStream();

    OutputStreamWrapper(ServletOutputStream servletOutputStream) {
      this.outputStream = servletOutputStream;
    }

    private ByteArrayOutputStream getByteArrayOutputStream() {
      return memoryStream;
    }

    @Override
    public boolean isReady() {
      return outputStream.isReady();
    }

    @Override
    public void setWriteListener(WriteListener listener) {
      outputStream.setWriteListener(listener);
    }

    @Override
    public void write(int b) throws IOException {
      outputStream.write(b);
      memoryStream.write(b);
    }

    @Override
    public void flush() throws IOException {
      outputStream.flush();
    }

    @Override
    public void close() throws IOException {
      outputStream.close();
      memoryStream.close();
    }
  }

}
