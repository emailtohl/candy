package wlei.candy.jpa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 通用实体类
 * Author: HeLei
 * Date: 2024/11/25
 *
 * @param <I> ID类型
 * @param <E> 实体类型
 */
// JSON序列化时忽略JPA/Hibernate懒加载属性
@SuppressWarnings("unchecked")
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
@MappedSuperclass
public abstract class GenericEntity<I extends Serializable, E extends GenericEntity<I, E>> implements Serializable, Cloneable {
  public static final String PROP_ID = "id";
  public static final String PROP_CREATE_TIME = "createTime";

  private LocalDateTime createTime;

  public GenericEntity() {
  }

  public GenericEntity(E src) {
    setId(src.getId());
    setCreateTime(src.getCreateTime());
  }

  /**
   * id，由子类决定生成方式
   *
   * @return id
   */
  public abstract I getId();

  public abstract E setId(I id);

  @Column(nullable = false, updatable = false)
  public LocalDateTime getCreateTime() {
    return createTime;
  }

  public E setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return (E) this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    E that = (E) o;
    return getId() == that.getId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  @Override
  public E clone() {
    try {
      return (E) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError(e);
    }
  }

  /**
   * 保存前处理
   */
  @PrePersist
  void prePersist() {
    if (getCreateTime() == null) {
      setCreateTime(LocalDateTime.now());
    }
  }
}
