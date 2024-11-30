package wlei.candy.jpa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

/**
 * 常用的一种实体定义类
 * 本类的id定义成Long，且基于属性映射的方式
 * <p>
 * Author: HeLei
 * Date: 2024/11/27
 *
 * @param <E> 实体类型
 */
@SuppressWarnings("unchecked")
// JSON序列化时忽略JPA/Hibernate懒加载属性
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
@MappedSuperclass
@Access(AccessType.FIELD) // 使用属性定义映射，可以被子类覆盖
public class UsualEntity<E extends UsualEntity<E>> extends GenericEntity<Long, E> {
  // 注意：配置上“hibernate.id.db_structure_naming_strategy=legacy”，则ID可以使用hibernate_sequence提供的序列
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  public UsualEntity() {
  }

  public UsualEntity(E src) {
    super(src);
  }

  public E copyBasicFrom(UsualEntity<? extends UsualEntity<?>> src) {
    return super.copyBasicFrom(src);
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public E setId(Long id) {
    this.id = id;
    return (E) this;
  }

}
