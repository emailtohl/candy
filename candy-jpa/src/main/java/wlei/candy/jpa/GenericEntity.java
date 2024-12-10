package wlei.candy.jpa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

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
@EntityListeners(EntityStateListener.class)
@MappedSuperclass
public abstract class GenericEntity<I extends Serializable, E extends GenericEntity<I, E>> implements Serializable, Cloneable {
  public static final String PROP_ID = "id";
  public static final String PROP_CREATE_TIME = "createTime";
  public static final String PROP_MOD_VER = "modVer";

  private LocalDateTime createTime;
  private LocalDateTime deleteTime;


  /**
   * 修改的版本号，用于乐观锁
   */
  private int modVer;

  public GenericEntity() {
  }

  public GenericEntity(E src) {
    if (src == null) {
      return;
    }
    setId(src.getId());
    setCreateTime(src.getCreateTime());
    setModVer(src.getModVer());
  }

  protected E copyBasicFrom(GenericEntity<I, ? extends GenericEntity<I, ?>> src) {
    if (src == null) {
      return (E) this;
    }
    return this.setId(src.getId())
        .setCreateTime(src.getCreateTime())
        .setModVer(src.getModVer());
  }

  /**
   * id，由子类决定生成方式
   *
   * @return id
   */
  public abstract I getId();

  public abstract E setId(I id);

  @Access(AccessType.PROPERTY)
  @Column(nullable = false, updatable = false)
  public LocalDateTime getCreateTime() {
    return createTime;
  }

  public E setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return (E) this;
  }

  @Access(AccessType.PROPERTY)
  @Version
  @Column
  public int getModVer() {
    return modVer;
  }

  public E setModVer(int modVer) {
    this.modVer = modVer;
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
   * 获取实体对象基本属性的名字，如id，主要应用场景是BeanUtils.copyProperties时忽略属性之用
   *
   * @param properties 属性的名字
   * @return BaseEntity的属性名 string [ ]
   */
  public String[] includeBasicPropertyNames(String... properties) {
    final short length = 3;
    String[] result = new String[length + properties.length];
    result[0] = PROP_ID;
    result[1] = PROP_CREATE_TIME;
    result[2] = PROP_MOD_VER;
    System.arraycopy(properties, 0, result, length, properties.length);
    return result;
  }

  /**
   * 保存前处理
   * final修饰符，保证不被子类覆盖，否则确实创建时间
   */
  @PrePersist
  final void prePersist() {
    if (getCreateTime() == null) {
      setCreateTime(LocalDateTime.now());
    }
  }
}
