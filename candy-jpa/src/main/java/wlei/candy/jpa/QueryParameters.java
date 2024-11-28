package wlei.candy.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.function.BiFunction;

/**
 * 查询参数，key是属性名，value是查询的值，如果value为空，则忽略
 *
 * @author HeLei
 */
public class QueryParameters extends HashMap<String, Object> implements Cloneable {
  private BiFunction<CriteriaBuilder, ? extends Root<?>, List<Predicate>> supplement;

  public QueryParameters() {
  }

  public QueryParameters(Map<String, ?> m) {
    putAll(m);
  }

  /**
   * 添加查询参数
   *
   * @param propertyName 实体的属性名
   * @param value        要查询的值，可以是字符串、枚举、日期等值类型或值类型的集合
   * @return 继续构造的对象
   */
  public QueryParameters add(String propertyName, Object value) {
    maybeValueType(propertyName, value);
    maybeCollection(propertyName, value);
    maybeArray(propertyName, value);
    return this;
  }

  private void maybeValueType(String propertyName, Object value) {
    if (isValueType(value)) {
      if (value instanceof String) {
        String str = (String) value;
        if (StringUtils.hasText(str)) {
          put(propertyName, str.trim());
        }
      } else {
        put(propertyName, value);
      }
    }
  }

  private void maybeCollection(String propertyName, Object value) {
    if (value instanceof Collection<?>) {
      Collection<?> c = (Collection<?>) value;
      if (isAllValueType(c)) {
        put(propertyName, c);
      }
    }
  }

  private void maybeArray(String propertyName, Object value) {
    if (value == null) {
      return;
    }
    Class<?> clz = value.getClass();
    if (!clz.isArray() || Array.getLength(value) == 0) {
      return;
    }
    if (clz.getComponentType().isPrimitive() || isAllValueType((Object[]) value)) {
      Set<Object> in = new HashSet<>();
      for (int i = 0; i < Array.getLength(value); i++) {
        in.add(Array.get(value, i));
      }
      put(propertyName, in);
    }
  }

  private boolean isValueType(Object value) {
    return value instanceof Number
        || value instanceof String
        || value instanceof Enum
        || value instanceof Boolean
        || value instanceof Temporal
        || value instanceof Calendar
        || value instanceof Date;
  }

  private boolean isAllValueType(Collection<?> c) {
    if (c.isEmpty()) {
      return false;
    }
    for (Object o : c) {
      if (!isValueType(o)) {
        return false;
      }
    }
    return true;
  }

  private boolean isAllValueType(Object[] c) {
    boolean ok = true;
    for (Object o : c) {
      if (!isValueType(o) && !isPrimitive(o)) {
        ok = false;
        break;
      }
    }
    return ok;
  }

  private boolean isPrimitive(Object o) {
    return o != null && o.getClass().isPrimitive();
  }

  @Override
  public QueryParameters clone() {
    return (QueryParameters) super.clone();
  }

  /**
   * @param <I> ID类型
   * @param <E> 实体类
   * @return 补充的查询条件
   */
  @JsonIgnore
  @SuppressWarnings("unchecked")
  public <I extends Serializable, E extends GenericEntity<I, E>> BiFunction<CriteriaBuilder, Root<E>, List<Predicate>> getSupplement() {
    return (BiFunction<CriteriaBuilder, Root<E>, List<Predicate>>) supplement;
  }

  public <I extends Serializable, E extends GenericEntity<I, E>> QueryParameters setSupplement(BiFunction<CriteriaBuilder, Root<E>, List<Predicate>> supplement) {
    this.supplement = supplement;
    return this;
  }
}
