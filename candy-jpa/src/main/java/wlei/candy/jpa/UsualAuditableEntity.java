package wlei.candy.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.util.StringUtils;
import wlei.candy.share.current.CurrentUserInfoFactory;

import java.time.LocalDateTime;

/**
 * 常用的可审计的实体类
 * <p>
 * Author: HeLei
 * Date: 2024/11/28
 *
 * @param <A> 继承本类的类型
 */
@SuppressWarnings("unchecked")
@Audited
@MappedSuperclass
public class UsualAuditableEntity<A extends UsualAuditableEntity<A>> extends UsualEntity<A> implements Auditability<Long, A> {
  @NotAudited
  @Column(nullable = false, updatable = false)
  private String createBy;

  @Column(nullable = false)
  private LocalDateTime modifyTime;

  @Column(nullable = false)
  private String modifyBy;

  public UsualAuditableEntity() {
  }

  public UsualAuditableEntity(A src) {
    super(src);
    this.createBy = src.getCreateBy();
    this.modifyTime = src.getModifyTime();
    this.modifyBy = src.getModifyBy();
  }

  /**
   * 获取实体对象基本属性的名字，如id，主要应用场景是BeanUtils.copyProperties时忽略属性之用
   *
   * @param properties 属性的名字
   * @return BaseEntity的属性名 string [ ]
   */
  public String[] includeBasePropertyNames(String... properties) {
    final short length = 5;
    String[] arr = new String[length + properties.length];
    arr[0] = PROP_ID;
    arr[1] = PROP_CREATE_TIME;
    arr[2] = PROP_CREATE_BY;
    arr[3] = PROP_MODIFY_TIME;
    arr[4] = PROP_MODIFY_BY;
    System.arraycopy(properties, 0, arr, length, properties.length);
    return arr;
  }

  @Override
  public String getCreateBy() {
    return createBy;
  }

  @Override
  public A setCreateBy(String createBy) {
    this.createBy = createBy;
    return (A) this;
  }

  @Override
  public LocalDateTime getModifyTime() {
    return modifyTime;
  }

  @Override
  public A setModifyTime(LocalDateTime modifyTime) {
    this.modifyTime = modifyTime;
    return (A) this;
  }

  @Override
  public String getModifyBy() {
    return modifyBy;
  }

  @Override
  public A setModifyBy(String modifyBy) {
    this.modifyBy = modifyBy;
    return (A) this;
  }

  /**
   * 保存前处理
   */
  @PrePersist
  void beforeCreate() {
    if (getModifyTime() == null) {
      if (getCreateTime() != null) {
        setModifyTime(getCreateTime());
      } else {
        setModifyTime(LocalDateTime.now());
      }
    }
    if (!StringUtils.hasText(getCreateBy())) {
      setCreateBy(CurrentUserInfoFactory.get().username());
    }
    if (!StringUtils.hasText(getModifyBy())) {
      setModifyBy(getCreateBy());
    }
  }

  /**
   * 更新前处理
   */
  @PreUpdate
  void beforeUpdate() {
    if (getModifyTime() == null) {
      setModifyTime(LocalDateTime.now());
    }
    if (!StringUtils.hasText(getModifyBy())) {
      setModifyBy(CurrentUserInfoFactory.get().username());
    }
  }
}
