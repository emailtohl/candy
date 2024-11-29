package wlei.candy.jpa;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * 基于JavaBean的ORM映射方式
 * <p>
 * Author: HeLei
 * Date: 2024/11/28
 */
@MappedSuperclass
public class BeanEntity<E extends GenericEntity<Long, E>> extends GenericEntity<Long, E> {
  private Long id;

  public BeanEntity() {
  }

  public BeanEntity(E src) {
    super(src);
  }

  // 注意：配置上“hibernate.id.db_structure_naming_strategy=legacy”，则ID可以使用hibernate_sequence提供的序列
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Override
  public Long getId() {
    return id;
  }

  @SuppressWarnings("unchecked")
  @Override
  public E setId(Long id) {
    this.id = id;
    return (E) this;
  }
}
