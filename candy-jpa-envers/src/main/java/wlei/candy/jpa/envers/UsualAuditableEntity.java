package wlei.candy.jpa.envers;

import jakarta.persistence.*;
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

  // 是否禁用自动记录创建人、更新人、更新时间
  // 在手动记录创建人、更新人、更新时间时，让其为true
  @Transient
  private transient boolean closeBeforeUpdate;

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
  @Override
  public String[] includeBasicPropertyNames(String... properties) {
    String[] src = super.includeBasicPropertyNames(PROP_CREATE_BY, PROP_UPDATE_TIME, PROP_UPDATE_BY);
    return appendPropertyNames(src, properties);
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

  public final boolean isCloseBeforeUpdate() {
    return closeBeforeUpdate;
  }

  public final void setCloseBeforeUpdate(boolean closeBeforeUpdate) {
    this.closeBeforeUpdate = closeBeforeUpdate;
  }

  /**
   * 保存前，确保创建人、更新人、更新时间不能为空
   */
  @PrePersist
  final void beforeCreate() {
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
  final void beforeUpdate() {
    if (this.closeBeforeUpdate) {
      return;
    }
    setUpdateTime(LocalDateTime.now());
    setUpdateBy(CurrentUserInfoFactory.get().username());
  }
}
