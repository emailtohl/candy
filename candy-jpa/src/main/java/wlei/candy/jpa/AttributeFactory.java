package wlei.candy.jpa;

import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * 创建实体属性模型的工厂类
 * <p>
 * Created by HeLei on 2021/12/25.
 */
public final class AttributeFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(AttributeFactory.class);

  /**
   * 分析实体类，得到属性模型
   *
   * @param entityClass 实体类
   * @return 属性模型集
   * @throws IllegalArgumentException 若不是实体类
   */
  public static Set<Attribute> parse(Class<?> entityClass) throws IllegalArgumentException {
    Class<?>[] bound = findEntityBound(entityClass);
    Class<?> topBound = bound[0], bottomBound = bound[1];
    AccessType accessType = getAccessType(entityClass);
    if (accessType == AccessType.FIELD) {
      return parseFields(topBound, bottomBound);
    } else {
      return parseProperties(topBound, bottomBound);
    }
  }

  static Set<Attribute> parseFields(Class<?> topBound, Class<?> bottomBound) {
    Set<Attribute> result = new HashSet<>();
    Class<?> clz = topBound;
    while (!clz.equals(bottomBound)) {
      Field[] fields = clz.getDeclaredFields();
      for (Field field : fields) {
        int modifiers = field.getModifiers();
        // isStrict 内部类连接外围类的引用
        if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || Modifier.isAbstract(modifiers)
            || Modifier.isNative(modifiers) || Modifier.isStrict(modifiers)
            || field.getAnnotation(Transient.class) != null) {
          continue;
        }
        Attribute attribute = new BeanField(field);
        // 扩展类的属性将覆盖基类的属性
        if (result.contains(attribute)) {
          LOGGER.debug("{} is exist ignore this", attribute.getName());
          continue;
        }
        result.add(attribute);
      }
      clz = clz.getSuperclass();
    }
    return result;
  }

  static Set<Attribute> parseProperties(Class<?> topBound, Class<?> bottomBound) {
    Set<Attribute> properties = new HashSet<>();
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(topBound, bottomBound);
      for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
        Method readMethod = pd.getReadMethod();
        if (readMethod == null || readMethod.getAnnotation(Transient.class) != null) {
          continue;
        }
        properties.add(new BeanProp(pd));
      }
    } catch (IntrospectionException e) {
      LOGGER.warn(e.getMessage(), e);
      throw new IllegalStateException(e);
    }
    return properties;
  }

  /**
   * 对注解了@Entity的类进行分析，确定其访问的规则，默认返回PROPERTY，这不仅是JPA默认规则，而且与EntityBase规则一致
   *
   * @param entityClass 注解了@Entity并且定义了@Id的类
   * @return 访问规则：FIELD还是PROPERTY
   * @throws IllegalArgumentException 不是定义的实体类
   */
  static AccessType getAccessType(Class<?> entityClass) throws IllegalArgumentException {
    Optional<AccessType> o = findAccessTypeByClass(entityClass);
    if (o.isPresent()) {
      return o.get();
    }
    LOGGER.debug("No @AccessType was found on the {}", entityClass.getSimpleName());
    o = findAccessTypeByField(entityClass);
    if (o.isPresent()) {
      return o.get();
    }
    LOGGER.debug("No @Id or @EmbeddedId was found on the JavaBean fields");
    o = findAccessTypeByProperty(entityClass);
    if (o.isPresent()) {
      return o.get();
    }
    LOGGER.debug("No @Id or @EmbeddedId was found on the JavaBean Property");
    String err = "No @Id or @EmbeddedId was found on the fields and property";
    LOGGER.debug(err);
    throw new IllegalArgumentException(err);
  }

  static Optional<AccessType> findAccessTypeByClass(Class<?> entityClass) {
    Class<?> clz = entityClass;
    while (clz != Object.class) {
      Access accessAnno = clz.getAnnotation(Access.class);
      if (accessAnno != null) {
        return Optional.of(accessAnno.value());
      }
      clz = clz.getSuperclass();
    }
    return Optional.empty();
  }

  static Optional<AccessType> findAccessTypeByField(Class<?> entityClass) {
    Class<?> clz = entityClass;
    while (clz != Object.class) {
      Field[] fields = clz.getDeclaredFields();
      for (Field field : fields) {
        if (field.getAnnotation(Id.class) != null || field.getAnnotation(EmbeddedId.class) != null) {
          return Optional.of(AccessType.FIELD);
        }
      }
      clz = clz.getSuperclass();
    }
    return Optional.empty();
  }

  static Optional<AccessType> findAccessTypeByProperty(Class<?> entityClass) {
    try {
      // stopClass是Object.class，表示不分析Object
      for (PropertyDescriptor pd : Introspector.getBeanInfo(entityClass, Object.class).getPropertyDescriptors()) {
        Method read = pd.getReadMethod();
        if (read != null && (read.getAnnotation(Id.class) != null || read.getAnnotation(EmbeddedId.class) != null)) {
          return Optional.of(AccessType.PROPERTY);
        }
      }
    } catch (IntrospectionException e) {
      LOGGER.warn(e.getMessage(), e);
      throw new IllegalStateException(e);
    }
    return Optional.empty();
  }

  /**
   * 查找实体的边界范围。因为继承关系，有两种情况需要考虑：
   * 1. 一个未标注@Entity的类，它继承于标注了@Entity的类。
   * 2. 标注了@Entity的类，但它的父类存在既不含@Entity也不含@MappedSuperclass的类
   * 所以对于那些不能映射到数据库表字段的属性，均需要排除。
   * <b>注意：遵循左闭右开原则，上界是闭区间，下界是开区间</b>
   *
   * @param clazz 被标注了Entity、Embeddable的实体类
   * @return 一个entityClass，stopClass 2个元素的数组，在继承上为上闭下开
   * @throws IllegalArgumentException 该类不是注解了@Entity的类
   */
  static Class<?>[] findEntityBound(Class<?> clazz) throws IllegalArgumentException {
    Class<?> clz = clazz;
    // 指定是数组列表，便于下标访问
    ArrayList<Class<?>> classes = new ArrayList<>();
    while (clz != Object.class) {
      classes.add(clz);
      clz = clz.getSuperclass();
    }
    // 寻找上届
    Class<?> topBound = findTopBound(classes);
    // 寻找下届
    Class<?> bottomBound = findBottomBound(classes);
    LOGGER.debug("entityClass is {} and stopClass is {}", topBound.getSimpleName(), bottomBound.getSimpleName());
    return new Class<?>[]{topBound, bottomBound};
  }

  private static Class<?> findTopBound(ArrayList<Class<?>> classes) throws IllegalArgumentException {
    assert !classes.isEmpty();
    // 寻找上界
    for (Class<?> clz : classes) {
      Embeddable embeddableAnn = clz.getAnnotation(Embeddable.class);
      Entity entityAnn = clz.getAnnotation(Entity.class);
      if (embeddableAnn != null || entityAnn != null) {
        return clz;
      }
    }
    throw new IllegalArgumentException(classes.get(0).getName() + " is not entity class");
  }

  private static Class<?> findBottomBound(ArrayList<Class<?>> classes) throws IllegalArgumentException {
    assert !classes.isEmpty();
    // 指定为ArrayList，对数组操作
    for (int i = classes.size() - 1; i >= 0; i--) {
      Class<?> clz = classes.get(i);
      MappedSuperclass mappedSuperclassAnno = clz.getAnnotation(MappedSuperclass.class);
      Entity entityAnn = clz.getAnnotation(Entity.class);
      Embeddable embeddableAnn = clz.getAnnotation(Embeddable.class);
      if (mappedSuperclassAnno != null || entityAnn != null || embeddableAnn != null) {
        return clz.getSuperclass();
      }
    }
    throw new IllegalArgumentException(classes.get(0).getName() + " is not entity class");
  }
}
