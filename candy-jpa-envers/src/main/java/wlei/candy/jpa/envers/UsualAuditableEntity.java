package wlei.candy.jpa.envers;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.util.StringUtils;
import wlei.candy.jpa.UsualEntity;
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
  // 创建者的信息不会变，且保存的时候与updateBy相同，所以不需要被审计
  @NotAudited
  @Column(nullable = false, updatable = false)
  private String createBy;

  @Column(nullable = false)
  private LocalDateTime updateTime;

  @Column(nullable = false)
  private String updateBy;

  public UsualAuditableEntity() {
  }

  public UsualAuditableEntity(A src) {
    super(src);
    this.createBy = src.getCreateBy();
    this.updateTime = src.getUpdateTime();
    this.updateBy = src.getUpdateBy();
  }

  public A copyBasicFrom(UsualAuditableEntity<? extends UsualAuditableEntity<?>> src) {
    super.copyBasicFrom(src);
    return this.setCreateBy(src.getCreateBy())
        .setUpdateTime(src.getUpdateTime())
        .setUpdateBy(src.getUpdateBy());

  }

  /**
   * 获取实体对象基本属性的名字，如id，主要应用场景是BeanUtils.copyProperties时忽略属性之用
   *
   * @param properties 属性的名字
   * @return BaseEntity的属性名 string [ ]
   */
  public String[] includeBasePropertyNames(String... properties) {
    final short length = 6;
    String[] arr = new String[length + properties.length];
    arr[0] = PROP_ID;
    arr[1] = PROP_CREATE_TIME;
    arr[2] = PROP_MOD_VER;
    arr[3] = PROP_CREATE_BY;
    arr[4] = PROP_UPDATE_TIME;
    arr[5] = PROP_UPDATE_BY;
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
  public LocalDateTime getUpdateTime() {
    return updateTime;
  }

  @Override
  public A setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
    return (A) this;
  }

  @Override
  public String getUpdateBy() {
    return updateBy;
  }

  @Override
  public A setUpdateBy(String updateBy) {
    this.updateBy = updateBy;
    return (A) this;
  }

  /**
   * 保存前处理
   */
  @PrePersist
  void beforeCreate() {
    if (getUpdateTime() == null) {
      if (getCreateTime() != null) {
        setUpdateTime(getCreateTime());
      } else {
        setUpdateTime(LocalDateTime.now());
      }
    }
    if (!StringUtils.hasText(getCreateBy())) {
      setCreateBy(CurrentUserInfoFactory.get().username());
    }
    if (!StringUtils.hasText(getUpdateBy())) {
      setUpdateBy(getCreateBy());
    }
  }

  /**
   * 更新前处理
   */
  @PreUpdate
  void beforeUpdate() {
    if (getUpdateTime() == null) {
      setUpdateTime(LocalDateTime.now());
    }
    if (!StringUtils.hasText(getUpdateBy())) {
      setUpdateBy(CurrentUserInfoFactory.get().username());
    }
  }
}
