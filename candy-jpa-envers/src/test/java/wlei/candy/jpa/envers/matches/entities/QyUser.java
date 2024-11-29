package wlei.candy.jpa.envers.matches.matches.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Author: HeLei
 * Date: 2024/11/24
 */
@Audited
@Entity
public class QyUser extends UsualEntity<QyUser> {
  /**
   * 分隔符
   */
  public static final String DELIMITER = ",";
  /**
   * 分隔符正则表达式
   */
  public static final String DELIMITER_PATTERN = "^\\w+(,\\w+)*$";
  /**
   * 登录名，不能重复，可用微信账号或其他第三方授权
   */
  @Column(unique = true, nullable = false)
  private String phone;
  /**
   * 用户姓名，可重复
   */
  @Column(nullable = false)
  private String name;
  /**
   * 密码
   */
  @NotAudited
  @Column(nullable = false)
  private String password;
  /**
   * 权限集合
   */
  @Column
  private String authority;
  /**
   * 头像信息
   */
  @Column
  private String icon;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    QyUser qyUser = (QyUser) o;
    return Objects.equals(phone, qyUser.phone);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), phone);
  }

  public QyUser setAuthority(String authority) {
    if (StringUtils.hasText(authority) && !authority.matches(DELIMITER_PATTERN)) {
      throw new IllegalArgumentException(DELIMITER_PATTERN);
    }
    this.authority = authority;
    return this;
  }

  public String[] authorities() {
    if (!StringUtils.hasText(authority)) {
      return new String[]{};
    }
    return authority.split(DELIMITER);
  }

  public String getPhone() {
    return phone;
  }

  public QyUser setPhone(String phone) {
    this.phone = phone;
    return this;
  }

  public String getName() {
    return name;
  }

  public QyUser setName(String name) {
    this.name = name;
    return this;
  }

  public String getPassword() {
    return password;
  }

  public QyUser setPassword(String password) {
    this.password = password;
    return this;
  }

  public String getAuthority() {
    return authority;
  }

  public String getIcon() {
    return icon;
  }

  public QyUser setIcon(String icon) {
    this.icon = icon;
    return this;
  }
}
