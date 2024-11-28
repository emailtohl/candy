package wlei.candy.web;

import java.util.HashSet;
import java.util.Set;

/**
 * 用于测试的数据对象
 * <p>
 * Created by HeLei on 2022/6/18.
 */
class ReqParams {
  private String ip;
  private String username;
  private Set<String> authorities = new HashSet<>();
  private String msg;

  public ReqParams() {
  }

  public ReqParams(String msg) {
    this.msg = msg;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Set<String> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(Set<String> authorities) {
    this.authorities = authorities;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}
