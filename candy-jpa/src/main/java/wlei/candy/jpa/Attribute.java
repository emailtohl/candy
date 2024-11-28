package wlei.candy.jpa;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 实体属性，统一JavaBean属性和类字段两种概念
 * <p>
 * Created by HeLei on 2021/12/25.
 */
public abstract class Attribute {
  protected static final Logger LOGGER = LoggerFactory.getLogger(Attribute.class);
  protected static final Pattern GENERIC_PATTERN = Pattern.compile("<(.+)>");

  /**
   * @return 属性名
   */
  public abstract String getName();

  /**
   * @return 属性类型
   */
  public abstract Class<?> getType();

  /**
   * @param instance 实例
   * @return 实例的值
   */
  public abstract Object getValue(Object instance);

  /**
   * 获取在此实例上的属性的值
   *
   * @param instance  实例
   * @param valueType 值的类
   * @param <T>       类型参数
   * @return 属性的值
   */
  public <T> T getValue(Object instance, Class<T> valueType) {
    Object value = getValue(instance);
    return valueType.cast(value);
  }

  /**
   * 获取该属性的泛型类
   *
   * @return 泛型类的实例
   */
  public Class<?>[] getGenericClass() {
    Class<?> targetClass = getTypeByAnnotation();
    if (!void.class.equals(targetClass)) {
      return new Class[]{targetClass};
    }
    List<Class<?>> ls = getTypeByGenericString();
    return ls.toArray(new Class<?>[0]);
  }

  private Class<?> getTypeByAnnotation() {
    Class<?> targetClass = void.class;
    // 以下三种注解不会同时存在，所以不考虑覆盖的场景
    ElementCollection elementCollection = getAnnotation(ElementCollection.class);
    if (elementCollection != null) {
      targetClass = elementCollection.targetClass();
    }
    OneToMany oneToMany = getAnnotation(OneToMany.class);
    if (oneToMany != null) {
      targetClass = oneToMany.targetEntity();
    }
    ManyToMany manyToMany = getAnnotation(ManyToMany.class);
    if (manyToMany != null) {
      targetClass = manyToMany.targetEntity();
    }
    return targetClass;
  }

  private List<Class<?>> getTypeByGenericString() {
    List<Class<?>> result = new ArrayList<>();
    String genericString = toGenericString();
    LOGGER.debug("genericString: {}", genericString);
    Matcher m = GENERIC_PATTERN.matcher(genericString);
    if (m.find()) {
      for (String className : m.group(1).split(",")) {
        try {
          result.add(Class.forName(className.trim()));
        } catch (ClassNotFoundException e) {
          LOGGER.warn(e.getMessage(), e);
        }
      }
    }
    return result;
  }

  /**
   * @return 字符串表示的类型
   */
  protected abstract String toGenericString();

  /**
   * 获取该属性上所有的注解
   *
   * @param annotationClass 注解的类实例
   * @param <A>             限定在注解类型
   * @return 如果不存在该注解，则返回null
   */
  protected abstract <A extends Annotation> A getAnnotation(Class<A> annotationClass);

  /**
   * 实现equals和hashCode保证在Hash容器中有正确的行为
   *
   * @param o 另一个属性对象
   * @return 相等性判断的结果
   */
  @Override
  public abstract boolean equals(Object o);

  @Override
  public abstract int hashCode();

  @Override
  public String toString() {
    return getName();
  }
}

class BeanProp extends Attribute {
  /**
   * 属性描述器
   */
  private final PropertyDescriptor propertyDescriptor;
  /**
   * 属性描述器中的Getter方法
   */
  private final Method readMethod;

  public BeanProp(PropertyDescriptor propertyDescriptor) {
    this.propertyDescriptor = propertyDescriptor;
    this.readMethod = propertyDescriptor.getReadMethod();
    if (this.readMethod == null) {
      throw new IllegalArgumentException("There's no read method in " + propertyDescriptor.getName());
    }
    this.readMethod.setAccessible(true);
  }

  @Override
  public String getName() {
    return propertyDescriptor.getName();
  }

  @Override
  public Class<?> getType() {
    return propertyDescriptor.getPropertyType();
  }

  @Override
  public Object getValue(Object instance) {
    Objects.requireNonNull(instance);
    try {
      return readMethod.invoke(instance);
    } catch (IllegalAccessException | InvocationTargetException e) {
      LOGGER.warn(e.getMessage(), e);
      throw new IllegalStateException(e);
    }
  }

  @Override
  public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
    return readMethod.getAnnotation(annotationClass);
  }

  @Override
  protected String toGenericString() {
    return readMethod.toGenericString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BeanProp beanProp = (BeanProp) o;
    return Objects.equals(propertyDescriptor, beanProp.propertyDescriptor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(propertyDescriptor);
  }
}

class BeanField extends Attribute {
  /**
   * 字段
   */
  private final Field field;

  public BeanField(Field field) {
    field.setAccessible(true);
    this.field = field;
  }

  @Override
  public String getName() {
    return field.getName();
  }

  @Override
  public Class<?> getType() {
    return field.getType();
  }

  @Override
  public Object getValue(Object instance) {
    Objects.requireNonNull(instance);
    try {
      return field.get(instance);
    } catch (IllegalAccessException e) {
      LOGGER.warn(e.getMessage(), e);
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
    return field.getAnnotation(annotationClass);
  }

  @Override
  protected String toGenericString() {
    return field.toGenericString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BeanField beanField = (BeanField) o;
    return Objects.equals(field, beanField.field);
  }

  @Override
  public int hashCode() {
    return Objects.hash(field);
  }
}