package wlei.candy.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 主键或联合主键对应的值
 *
 * @author HeLei
 */
public class KeyAttribute {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeyAttribute.class);
  /**
   * attrName id或联合主键的key的属性名
   */
  public final String attrName;
  /**
   * value 对应的值
   */
  public final Object value;

  /**
   * Instantiates a new unique attribute.
   *
   * @param name  the name
   * @param value the value
   */
  public KeyAttribute(String name, Object value) {
    this.attrName = name;
    this.value = value;
  }

  /**
   * 分析该实体，是否带有unique注解的字段，并用此字段作为唯一识别信息
   *
   * @param entity 实体
   * @return 分析结果
   */
  static <I extends Serializable, E extends GenericEntity<I, E>> KeyAttribute[] parse(E entity) {
    List<KeyAttribute> result = new ArrayList<>();
    for (Attribute attribute : AttributeFactory.parse(entity.getClass())) {
      Column column = attribute.getAnnotation(Column.class);
      if (column == null || !column.unique()) {
        continue;
      }
      Object value = attribute.getValue(entity);
      if (value == null) {
        continue;
      }
      result.add(new KeyAttribute(attribute.getName(), value));
    }
    if (result.isEmpty()) {
      String msg = String.format("There is no unique identifier for this class %s", entity.getClass().getSimpleName());
      LOGGER.error(msg);
      throw new IllegalArgumentException(msg);
    }
    return result.toArray(new KeyAttribute[0]);
  }

  /**
   * 根据属性名获取路径
   *
   * @param r   根路径
   * @param <I> ID类型
   * @param <E> 实体类
   * @param <X> 路径上的值类型
   * @return 属性的路径
   */
  public <I extends Serializable, E extends GenericEntity<I, E>, X> Path<X> getPath(Root<E> r) {
    Path<X> path = null;
    for (String a : attrName.split("\\.")) {
      if (path == null) {
        path = r.get(a);
      } else {
        path = path.get(a);
      }
    }
    if (path == null) {
      throw new IllegalStateException("attrName is empty");
    }
    return path;
  }
}
