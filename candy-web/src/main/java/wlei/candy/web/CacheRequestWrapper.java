package wlei.candy.web;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 包装HttpServletRequest，使其可以在过滤器层就读取数据流
 * <strong>读取流需要使用try-with-resource语法，或在读取完成后调用close()，这样才能重置流以被下一个过滤链重复使用</strong>
 * Created by HeLei on 2021/4/9.
 */
public class CacheRequestWrapper extends HttpServletRequestWrapper {

  private MemoryInputStreamWrapper inputStream;

  public CacheRequestWrapper(HttpServletRequest request) {
    super(request);
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    if (inputStream == null) {
      // 传入的时候类型就是HttpServletRequest，所以这里直接强转
      inputStream = new MemoryInputStreamWrapper((HttpServletRequest) getRequest());
    }
    return inputStream;
  }

  /**
   * 有的数据如认证后的权限，可能是在过滤器层上获取的，若要补到参数中就需要重置请求流
   *
   * @param contents 重写后的数据
   */
  public void setContents(byte[] contents) {
    inputStream.setContents(contents);
  }

  @Override
  public BufferedReader getReader() throws IOException {
    String encoding = getCharacterEncoding();
    encoding = StringUtils.hasText(encoding) ? encoding : StandardCharsets.UTF_8.name();
    try {
      return new BufferedReader(new InputStreamReader(getInputStream(), encoding));
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private static class MemoryInputStreamWrapper extends ServletInputStream {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryInputStreamWrapper.class);
    private ByteArrayInputStream memoryInputStream;
    private ReadListener listener;

    private MemoryInputStreamWrapper(HttpServletRequest request) throws IOException {
      try (ServletInputStream in = request.getInputStream()) {
        byte[] body = IOUtils.toByteArray(in);
        memoryInputStream = new ByteArrayInputStream(body);
      }
      onDataAvailable();
    }

    private void setContents(byte[] contents) {
      memoryInputStream = new ByteArrayInputStream(contents);
    }

    @Override
    public int read() {
      int data = memoryInputStream.read();
      onAllDataRead();
      return data;
    }

    /**
     * 当关闭时，内存流将重置，以便再次访问
     */
    @Override
    public void close() {
      memoryInputStream.reset();
      onDataAvailable();
    }

    @Override
    public boolean isFinished() {
      return memoryInputStream.available() == 0;
    }

    @Override
    public boolean isReady() {
      return true;
    }

    @Override
    public void setReadListener(ReadListener listener) {
      this.listener = listener;
    }

    private void onDataAvailable() {
      if (listener != null) {
        try {
          listener.onDataAvailable();
        } catch (IOException e) {
          LOGGER.error(e.getMessage(), e);
        }
      }
    }

    private void onAllDataRead() {
      if (listener != null && isFinished()) {
        try {
          listener.onAllDataRead();
        } catch (IOException e) {
          LOGGER.error(e.getMessage(), e);
        }
      }
    }
  }
}
