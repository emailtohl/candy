package wlei.candy.web;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Consumer;

/**
 * 线程上下文上的日志
 * <p>
 * Created by helei on 2023/10/26
 */
public abstract class ThreadLog implements Serializable {
  private static final ThreadLocal<ThreadLog> THREAD_LOCAL = new ThreadLocal<>();
  /**
   * 服务器ip
   */
  private static String SERVER_IP;

  static {
    try {
      SERVER_IP = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException ignore) {
    }
  }

  /**
   * 线程的开始时间，提供性能计算之用
   */
  private final long start;
  /**
   * 前端发起的事务号，没有的话则创建一个
   */
  private String tid;
  /**
   * 如果存在session则记录id
   */
  private String sessionId;
  /**
   * kcmc登录的用户名
   */
  private String username;
  /**
   * 客户端的ip
   */
  private String clientIP;
  /**
   * HTTP访问方法
   */
  private String method;
  /**
   * HTTP访问路径
   */
  private String path;
  /**
   * 实例化时的调用者，不能提供set方法
   */
  private String caller;
  /**
   * 实例化时调用者的调用行，不能提供set方法
   */
  private int line;
  /**
   * 调用者的所处的栈位置，需要根据调用的层数来调节
   */
  private int stackOffset = 0;

  public ThreadLog() {
    ThreadLog base = THREAD_LOCAL.get();
    if (base != null) {
      this.start = base.start;
      this.setTid(base.getTid())
          .setSessionId(base.getSessionId())
          .setUsername(base.getUsername())
          .setClientIP(base.getClientIP())
          .setMethod(base.getMethod())
          .setPath(base.getPath());
    } else {
      start = System.currentTimeMillis();
      THREAD_LOCAL.set(this);
    }
  }

  /**
   * 清理线程上下文的内容
   */
  public static void clearThreadLocal() {
    THREAD_LOCAL.remove();
  }

  /**
   * 使用各自定义的日志格式打印
   * 此方法涉及调用栈信息，不能不被覆盖
   */
  public final void print() {
    recogCaller();
    String msg = format();
    Consumer<String> l = logger();
    l.accept(msg);
  }

  /**
   * 获取调用者信息，私有方法，仅供print()使用
   */
  private void recogCaller() {
    try {
      StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
      // index 0: getStackTrace，index 1：本方法，index 2：本类的print方法，index 3：即调用点
      StackTraceElement stack = stacks[3 + stackOffset];
      String callerName = stack.getClassName();
      int index = callerName.lastIndexOf(".");
      this.caller = callerName.substring(index + 1);
      this.line = stack.getLineNumber();
    } catch (Exception ignore) {
    }
  }

  /**
   * @return 与开始时间的差值
   */
  public long durationStart() {
    return System.currentTimeMillis() - start;
  }

  /**
   * @return 打印时的时间戳，按具体日志要求格式来
   */
  protected abstract String timestamp();

  /**
   * @return 将结构化的数据格式化为日志内容
   */
  protected abstract String format();

  /**
   * @return 由扩展类给出打印的Logger
   */
  protected abstract Consumer<String> logger();

  /**
   * @param stackOffset 调用者所在的栈序号
   * @return this
   */
  public ThreadLog setStackOffset(int stackOffset) {
    this.stackOffset = stackOffset;
    return this;
  }

  @Override
  public String toString() {
    return format();
  }

  public String getCaller() {
    return caller;
  }

  public int getLine() {
    return line;
  }

  public String getTid() {
    return tid;
  }

  public ThreadLog setTid(String tid) {
    this.tid = tid;
    return this;
  }

  public String getSessionId() {
    return sessionId;
  }

  public ThreadLog setSessionId(String sessionId) {
    this.sessionId = sessionId;
    return this;
  }

  public String getUsername() {
    return username;
  }

  public ThreadLog setUsername(String username) {
    this.username = username;
    return this;
  }

  public String getClientIP() {
    return clientIP;
  }

  public ThreadLog setClientIP(String clientIP) {
    this.clientIP = clientIP;
    return this;
  }

  public String getServerIP() {
    return SERVER_IP;
  }

  public String getMethod() {
    return method;
  }

  public ThreadLog setMethod(String method) {
    this.method = method;
    return this;
  }

  public String getPath() {
    return path;
  }

  public ThreadLog setPath(String path) {
    this.path = path;
    return this;
  }

}
