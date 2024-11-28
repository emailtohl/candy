package wlei.candy.jpa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.MappedSuperclass;

import java.io.Serializable;
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

  /**
   * id，由子类决定生成方式
   *
   * @return id
   */
  public abstract I getId();

  public abstract E setId(I id);

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

}
