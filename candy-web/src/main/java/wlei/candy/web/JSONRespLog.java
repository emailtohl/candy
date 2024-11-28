package wlei.candy.web;

/**
 * 记录响应的ELK日志
 * <p>
 * Created by helei on 2023/10/24
 */
public class JSONRespLog extends JSONLog {
  /**
   * 错误的响应会有错误码
   */
  private String code;

  private String error;

  public JSONRespLog(LogDir logDir, String code) {
    super(logDir);
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public JSONRespLog setCode(String code) {
    this.code = code;
    return this;
  }

  public String getError() {
    return error;
  }

  public JSONRespLog setError(String error) {
    this.error = error;
    return this;
  }
}
