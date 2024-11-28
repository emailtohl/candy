package wlei.candy.share.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Java8 本地时间的一些常用功能
 */
public final class DateUtil {
  /**
   * The constant GMT_8.
   */
  public static final String GMT_8 = "GMT+8";
  /**
   * The constant TIME_PATTERN.
   */
  public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

  private static final String NUMBER_FORMAT = "yyyyMMddHHmmss";

  private DateUtil() {
  }

  /**
   * To date.
   *
   * @param localDate the local date
   * @return the date
   */
  public static Date toDate(LocalDate localDate) {
    ZoneId zoneId = ZoneId.systemDefault();
    ZonedDateTime zonedDateTime = localDate.atStartOfDay(zoneId);
    return Date.from(zonedDateTime.toInstant());
  }

  /**
   * To local date.
   *
   * @param date the date
   * @return the local date
   */
  public static LocalDate toLocalDate(Date date) {
    // java.sql.Date的toInstant方法不能使用
    Date _date;
    if (date instanceof java.sql.Date) {
      _date = new Date(date.getTime());
    } else {
      _date = date;
    }
    Instant instant = _date.toInstant();
    ZoneId zoneId = ZoneId.systemDefault();
    // atZone()方法返回在指定时区从此Instant生成的ZonedDateTime。
    return instant.atZone(zoneId).toLocalDate();
  }

  /**
   * To local date time local date time.
   *
   * @param date the date
   * @return the local date time
   */
  public static LocalDateTime toLocalDateTime(Date date) {
    // java.sql.Date的toInstant方法不能使用
    Date _date;
    if (date instanceof java.sql.Date) {
      _date = new Date(date.getTime());
    } else {
      _date = date;
    }
    Instant instant = _date.toInstant();
    ZoneId zone = ZoneId.systemDefault();
    return LocalDateTime.ofInstant(instant, zone);
  }

  /**
   * To date.
   *
   * @param localDateTime the local date time
   * @return the date
   */
  public static Date toDate(LocalDateTime localDateTime) {
    ZoneId zone = ZoneId.systemDefault();
    Instant instant = localDateTime.atZone(zone).toInstant();
    return Date.from(instant);
  }

  /**
   * @param d Date
   * @return 该天的最后时刻
   */
  public static Date last(Date d) {
    LocalDate ld = toLocalDate(d);
    LocalDateTime ldt = LocalDateTime.of(ld.getYear(), ld.getMonth(), ld.getDayOfMonth(), 23, 59, 59, 999999999);
    return toDate(ldt);
  }

  /**
   * 近似相等
   *
   * @param d1 参数1
   * @param d2 参数2
   * @return 近似相等的结果
   */
  public static boolean approximateEquals(Date d1, Date d2) {
    long d = d1.getTime() - d2.getTime();
    d = Math.abs(d);
    return d < 100;
  }

  /**
   * @param t LocalDateTime
   * @return 转成数字形式的时间
   */
  public static long numberTime(LocalDateTime t) {
    String s = t.format(DateTimeFormatter.ofPattern(NUMBER_FORMAT));
    return Long.parseLong(s);
  }

  /**
   * @param t Instant
   * @return 转成数字形式的时间
   */
  public static long numberTime(Instant t) {
    LocalDateTime d = t.atZone(ZoneId.systemDefault()).toLocalDateTime();
    return numberTime(d);
  }

  /**
   * @param d Date
   * @return 转成数字形式的时间
   */
  public static long numberTime(Date d) {
    return numberTime(d.toInstant());
  }

  /**
   * 将数字形式的时间转成LocalDateTime
   *
   * @param numberTime 数字形式的时间
   * @return LocalDateTime
   */
  public static LocalDateTime numberToLocalDateTime(long numberTime) {
    String t = String.valueOf(numberTime);
    if (!t.matches("\\d{4}[0-1][0-9][0-3][0-9][0-2][0-9][0-5][0-9][0-5][0-9]")) {
      throw new IllegalArgumentException(String.format("numberTime %d Formatting error, It should be %s", numberTime, NUMBER_FORMAT));
    }
    return LocalDateTime.parse(t, DateTimeFormatter.ofPattern(NUMBER_FORMAT));
  }
}
