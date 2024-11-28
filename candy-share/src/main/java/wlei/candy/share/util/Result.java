package wlei.candy.share.util;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 主要用于控制层返回简单的结果
 * 为空的字段就不要序列化到json中
 *
 * @author HeLei
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Result {
  /**
   * ID
   */
  private Long id;
  /**
   * 是否存在
   */
  private Boolean exist;
  /**
   * 是否成功
   */
  private Boolean success;
  /**
   * 文本信息
   */
  private String text;

  public static Result ofId(long id) {
    Result result = new Result();
    result.id = id;
    return result;
  }

  public static Result ofExist(boolean exist) {
    Result result = new Result();
    result.exist = exist;
    return result;
  }

  public static Result withSuccess() {
    Result result = new Result();
    result.success = true;
    return result;
  }

  public static Result withFail() {
    Result result = new Result();
    result.success = false;
    return result;
  }

  public static Result ofText(String text) {
    Result result = new Result();
    result.text = text;
    return result;
  }

  public Long getId() {
    return id;
  }

  public Result setId(Long id) {
    this.id = id;
    return this;
  }

  public Boolean getExist() {
    return exist;
  }

  public Result setExist(Boolean exist) {
    this.exist = exist;
    return this;
  }

  public Boolean getSuccess() {
    return success;
  }

  public Result setSuccess(Boolean success) {
    this.success = success;
    return this;
  }

  public String getText() {
    return text;
  }

  public Result setText(String text) {
    this.text = text;
    return this;
  }
}
