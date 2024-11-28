package wlei.candy.web;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by helei on 2023/10/17
 */
class ThreadLogTest {

  @Test
  void test() {
    JSONLog in = (JSONLog) new JSONLog(LogDir.IN_REQUEST)
        .setDirection(LogDir.IN_REQUEST)
        .setMessage("hello")
        .setSessionId(UUID.randomUUID().toString())
        .setMethod("POST")
        .setTid(UUID.randomUUID().toString())
        .setClientIP("10.1.2.3")
        .setPath("/say/hello")
        .setUsername("foo");
    in.print();

    assertEquals(ThreadLogTest.class.getSimpleName(), in.getCaller());
    assertTrue(in.getLine() > 0);
    assertNotNull(LocalDateTime.parse(in.timestamp(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    assertEquals(LogDir.IN_REQUEST, in.getDirection());

    new JSONLog(LogDir.INNER).print();
    new JSONLog(LogDir.OUT).print();
    new JSONLog(LogDir.OUT_REQUEST).print();
    new JSONRespLog(LogDir.OUT_RESPONSE, "0").print();

    JSONLog out = new JSONRespLog(LogDir.IN_RESPONSE, "0").setCode("0");
    out.print();
    assertTrue(out.toString().contains("foo"));

    assertTrue(out.durationStart() > 0);
    assertNotNull(out.getServerIP());

    JSONLog.clearThreadLocal();
    // 测试多次调用会不会报错
    JSONLog.clearThreadLocal();
  }

  @Test
  void invokerIndex() {
    LogWrapper.printIn("hello");
    LogWrapper.printOut("world");
    LogWrapper.printIn("foo");
    LogWrapper.printOut("bar");
  }

  private static class LogWrapper extends JSONLog {

    private LogWrapper(LogDir logDir) {
      super(logDir);
    }

    static void printIn(String msg) {
      new JSONLog(LogDir.IN_REQUEST)
          .setDirection(LogDir.IN_RESPONSE)
          .setMessage(msg)
          .setSessionId(UUID.randomUUID().toString())
          .setMethod("POST")
          .setTid(UUID.randomUUID().toString())
          .setClientIP("10.1.2.3")
          .setPath("/say/hello")
          .setUsername("foo")
          .setStackOffset(1)
          .print();
    }

    static void printOut(String msg) {
      new JSONLog(LogDir.IN_REQUEST)
          .setDirection(LogDir.IN_REQUEST)
          .setMessage(msg)
          .setSessionId(UUID.randomUUID().toString())
          .setMethod("POST")
          .setTid(UUID.randomUUID().toString())
          .setClientIP("10.1.2.3")
          .setPath("/say/hello")
          .setUsername("foo")
          .setStackOffset(1)
          .print();
    }
  }
}
