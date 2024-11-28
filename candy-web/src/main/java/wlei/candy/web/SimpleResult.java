package wlei.candy.web;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by helei on 2023/9/13
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SimpleResult {
  /**
   * 配置ID
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
   * 当结果是失败时，存储失败原因
   */
  private String errorMessage;
  /**
   * 密码
   */
  private String encodePassword;
  /**
   * value
   */
  private String value;

  private SimpleResult() {
  }

  public SimpleResult(Long id) {
    this.id = id;
  }

  public SimpleResult(Boolean exist) {
    this.exist = exist;
  }

  public static SimpleResult fail() {
    SimpleResult result = new SimpleResult();
    result.setSuccess(false);
    return result;
  }

  public static SimpleResult fail(String cause) {
    return fail().setErrorMessage(cause);
  }

  public static SimpleResult success() {
    SimpleResult result = new SimpleResult();
    result.setSuccess(true);
    return result;
  }

  public static SimpleResult withValue(String value) {
    SimpleResult result = new SimpleResult();
    result.setValue(value);
    return result;
  }

  public Long getId() {
    return id;
  }

  public SimpleResult setId(Long id) {
    this.id = id;
    return this;
  }

  public Boolean getExist() {
    return exist;
  }

  public SimpleResult setExist(Boolean exist) {
    this.exist = exist;
    return this;
  }

  public Boolean getSuccess() {
    return success;
  }

  public SimpleResult setSuccess(Boolean success) {
    this.success = success;
    return this;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public SimpleResult setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  public String getEncodePassword() {
    return encodePassword;
  }

  public SimpleResult setEncodePassword(String encodePassword) {
    this.encodePassword = encodePassword;
    return this;
  }

  public String getValue() {
    return value;
  }

  public SimpleResult setValue(String value) {
    this.value = value;
    return this;
  }
}
