package wlei.candy.jpa;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 分析实体对象与HibernateSearch有关的注解
 * <p>
 * Created by HeLei on 2021/4/24
 */
class FieldsAnalyzer {
  private final Set<Class<?>> visited = new HashSet<>();
  private final Set<String> fields = new HashSet<>();

  private final Class<?> clazz;

  FieldsAnalyzer(Class<?> clazz) {
    this.clazz = clazz;
  }

  String[] parse() {
    exec(clazz, "");
    return fields.toArray(new String[0]);
  }

  void exec(Class<?> clazz, String parentPath) {
    visited.add(clazz);
    for (Attribute attribute : AttributeFactory.parse(clazz)) {
      String preStr = parentPath.isEmpty() ? "" : parentPath + '.';
      KeywordField keyField = attribute.getAnnotation(KeywordField.class);
      if (keyField != null) {
        String onField = preStr + (keyField.name().isEmpty() ? attribute.getName() : keyField.name());
        fields.add(onField);
        continue;
      }
      GenericField genericField = attribute.getAnnotation(GenericField.class);
      if (genericField != null) {
        String onField = preStr + (genericField.name().isEmpty() ? attribute.getName() : genericField.name());
        fields.add(onField);
        continue;
      }
      FullTextField fullTextField = attribute.getAnnotation(FullTextField.class);
      if (fullTextField != null) {
        String onField = preStr + (fullTextField.name().isEmpty() ? attribute.getName() : fullTextField.name());
        fields.add(onField);
        continue;
      }
      // @IndexedEmbedded指定在主业务实体的索引中包含关联业务实体的搜索内容，可以通过搜索关联业务实体的内容得到主业务实体的查询结果。
      // @ContainedIn指定更新关联实体时同时更新主业务实体中索引的内容，如果不指定@ContainedIn会导致关联实体内容修改后得到错误的搜索结果。
      // @IndexedEmbedded和@ContainedIn可以同时出现在一个属性上，意味着其关联的业务实体对应的属性上也应当同时出现这两个注解。
      IndexedEmbedded indexedEmbedded = attribute.getAnnotation(IndexedEmbedded.class);
      if (indexedEmbedded != null) {
        // IndexedEmbedded既可以注解在@ManyToOne这样的实体属性上，也可以注解在@OneToMany这样的集合属性上
        Class<?> embClazz = attribute.getType();
        if (Collection.class.isAssignableFrom(embClazz)) {// 如果是集合属性的情况
          embClazz = indexedEmbedded.targetType();
          if (void.class.equals(embClazz)) {// 如果没有指定目标类，那就分析该泛型类
            Class<?>[] classes = attribute.getGenericClass();
            if (classes.length != 1) {
              throw new IllegalStateException("The embedded class was not analyzed");
            }
            embClazz = classes[0];
          }
        }
        // 再次解析这个类时，就不再递归访问了
        if (visited.contains(clazz)) {
          exec(embClazz, parentPath.isEmpty() ? attribute.getName() : parentPath + '.' + attribute.getName());
        }
      }
    }
  }
}
