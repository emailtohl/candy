package wlei.candy.jpa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

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
public class UsualEntity<E extends UsualEntity<E>> extends GenericEntity<Long, E> implements SoftDeletable<Long, E> {
  // 注意：配置上“hibernate.id.db_structure_naming_strategy=legacy”，则ID可以使用hibernate_sequence提供的序列
  @Id
  @SequenceGenerator(name = "HIBERNATE_SEQUENCE", allocationSize = 10)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HIBERNATE_SEQUENCE")
  private Long id;

  @Basic
  private LocalDateTime deleteTime;

  public UsualEntity() {
  }

  public UsualEntity(E src) {
    super(src);
    this.setDeleteTime(src.getDeleteTime());
  }

  public E copyBasicFrom(UsualEntity<? extends UsualEntity<?>> src) {
    super.copyBasicFrom(src);
    this.setDeleteTime(src.getDeleteTime());
    return (E) this;
  }

  @Override
  public String[] includeBasicPropertyNames(String... properties) {
    String[] src = super.includeBasicPropertyNames(PROP_DELETE_TIME);
    return appendPropertyNames(src, properties);
  }

  protected String[] appendPropertyNames(String[] src, String... properties) {
    String[] result = new String[src.length + properties.length];
    System.arraycopy(src, 0, result, 0, src.length);
    System.arraycopy(properties, 0, result, src.length, properties.length);
    return result;
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

  @Override
  public LocalDateTime getDeleteTime() {
    return deleteTime;
  }

  @Override
  public E setDeleteTime(LocalDateTime deleteTime) {
    this.deleteTime = deleteTime;
    return (E) this;
  }
}
