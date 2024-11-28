package wlei.candy.web;

import wlei.candy.share.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

/**
 * ELK的日志
 * <p>
 * Created by helei on 2023/10/17
 */
public class JSONLog extends ThreadLog {
  private static final Logger LOGGER = LoggerFactory.getLogger(JSONLog.class);
  private LogDir logDir;
  private String message;

  public JSONLog(LogDir logDir) {
    this.logDir = logDir;
  }

  @Override
  public String timestamp() {
    return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }

  @Override
  public String format() {
    return JsonUtil.writeValueNonNull(this);
  }

  @Override
  protected Consumer<String> logger() {
    return LOGGER::info;
  }

  public LogDir getDirection() {
    return logDir;
  }

  public JSONLog setDirection(LogDir logDir) {
    this.logDir = logDir;
    return this;
  }

  public String getMessage() {
    return message;
  }

  public JSONLog setMessage(String message) {
    this.message = message;
    return this;
  }
}
