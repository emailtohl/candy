package wlei.candy.share.util;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

  @Test
  void testDateLocalDate() {
    String format = "yyyy-MM-dd";
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    Date today = new Date();
    String s = sdf.format(today);
    LocalDate d = DateUtil.toLocalDate(today);
    assertEquals(s, d.format(DateTimeFormatter.ofPattern(format)));
    /*调试试用
    System.out.println(DateUtil.toDate(d));
    */
  }

  @Test
  void testLocalDateTime() {
    String format = "HH:mm:ss";
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    Date today = new Date();
    String s = sdf.format(today);
    LocalDateTime dt = DateUtil.toLocalDateTime(today);
    assertEquals(s, dt.format(DateTimeFormatter.ofPattern(format)));
    /*调试试用
    System.out.println(DateUtil.toDate(dt));
    */
  }

  /*
   *To date 测试方法
   */
  @Test
  void toDate_forDateTimetest() {
    Date today = new Date();
    LocalDateTime dt = DateUtil.toLocalDateTime(today);
    Date date = DateUtil.toDate(dt);
    assertEquals(today, date);
  }

  /*
   *To date 测试方法
   */
  @Test
  void toDate_forDatetest() {
    Date today = new Date();
    LocalDate dt = DateUtil.toLocalDate(today);
    Date date = DateUtil.toDate(dt);
    assertEquals(today.toString().substring(0, 10), date.toString().substring(0, 10));
  }

  @Test
  void last() {
    Date today = new Date();
    Date last = DateUtil.last(today);
    assertTrue(last.after(today));
  }

  @Test
  void approximateEquals() {
    Date d1 = new Date();
    Date d2 = new Date();
    assertTrue(DateUtil.approximateEquals(d1, d2));
    d2 = new Date(d1.getTime() + 50L);
    assertTrue(DateUtil.approximateEquals(d1, d2));
    d2 = new Date(d1.getTime() + 101L);
    assertFalse(DateUtil.approximateEquals(d1, d2));
  }

  @Test
  void testNumberTime1() {
    LocalDateTime d = LocalDateTime.of(2023, Month.JULY, 3, 9, 33, 50);
    long l = DateUtil.numberTime(d);
    assertEquals(20230703093350L, l);
  }

  @Test
  void testNumberTime2() {
    LocalDateTime d = LocalDateTime.of(2023, Month.JULY, 3, 9, 33, 50);
    ZoneId zone = ZoneId.systemDefault();
    Instant t = d.atZone(zone).toInstant();
    long l = DateUtil.numberTime(t);
    assertEquals(20230703093350L, l);
  }

  @Test
  void testNumberTime3() {
    LocalDateTime ldt = LocalDateTime.of(2023, Month.JULY, 3, 9, 33, 50);
    Date d = DateUtil.toDate(ldt);
    long l = DateUtil.numberTime(d);
    assertEquals(20230703093350L, l);
  }

  @Test
  void numberToLocalDateTime() {
    LocalDateTime ldt = LocalDateTime.of(2023, Month.JULY, 3, 9, 33, 50);
    long l = DateUtil.numberTime(ldt);
    LocalDateTime other = DateUtil.numberToLocalDateTime(l);
    assertEquals(l, DateUtil.numberTime(other));

    assertThrows(IllegalArgumentException.class, () -> DateUtil.numberToLocalDateTime(2023072117080011L));

    assertThrows(IllegalArgumentException.class, () -> DateUtil.numberToLocalDateTime(20230741170800L));
  }
}