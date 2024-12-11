package wlei.candy.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;

import static wlei.candy.jpa.SoftDeletable.PROP_SOFT_DEL;

/**
 * 创建where谓词的构造器
 *
 * @param <I> ID类型
 * @param <E> 实体类
 * @author HeLei
 */
public class PredicateBuilder<I extends Serializable, E extends GenericEntity<I, E>> {
  final CriteriaBuilder criteriaBuilder;
  final Root<E> root;
  private boolean whereSoftDeleted = false;

  @SuppressWarnings("unchecked")
  public PredicateBuilder(CriteriaBuilder criteriaBuilder, Root<E> root) {
    this.criteriaBuilder = criteriaBuilder;
    this.root = (Root<E>) Proxy.newProxyInstance(
        Root.class.getClassLoader(),
        new Class[]{Root.class},
        new RootProxy(root)
    );
  }

  /**
   * 根据参数返回查询谓词
   * “.”作为路径分隔符，若字符串前后有“%”，则用like查询
   *
   * @param params 查询参数
   * @return 谓词集合
   */
  @SuppressWarnings("unchecked")
  public List<Predicate> getPredicates(QueryParameters params) {
    List<Predicate> predicates = new ArrayList<>();
    if (params == null) {
      return predicates;
    }
    for (Entry<String, ?> e : params.entrySet()) {
      Object value = e.getValue();
      if (value == null) {
        continue;
      }
      // 以“.”作为path的分隔
      Path<?> path = null;
      String attrPath = e.getKey();
      for (String attr : attrPath.split("\\.")) {
        if (path == null) {
          path = root.get(attr);
        } else {
          path = path.get(attr);
        }
      }
      // 如果查询值后缀有“%”，那么就用like查询
      if (value instanceof String && ((String) value).endsWith("%")) {
        predicates.add(criteriaBuilder.like(criteriaBuilder.lower((Path<String>) path), ((String) value).toLowerCase()));
      } else if (value instanceof Collection) {
        assert path != null;
        predicates.add(path.in((Collection<?>) value));
      } else {
        predicates.add(criteriaBuilder.equal(path, value));
      }
    }
    BiFunction<CriteriaBuilder, Root<E>, List<Predicate>> supplement = params.getSupplement();
    if (supplement != null) {
      predicates.addAll(supplement.apply(criteriaBuilder, root));
    }
    if (excludeSoftDeleted(params)) {
      predicates.add(criteriaBuilder.isFalse(root.get(PROP_SOFT_DEL)));
    }
    return predicates;
  }

  /**
   * 是否排除掉软删除的记录，如果传入的参数中有针对软删除的查询条件，那就不再做关于软删除的操作，否则就排除已被软删除的记录
   *
   * @param params 查询条件
   * @return 是否排除掉软删除的记录
   */
  private boolean excludeSoftDeleted(QueryParameters params) {
    if (params.containsKey(PROP_SOFT_DEL)) {
      return false;
    }
    if (whereSoftDeleted) {
      return false;
    }
    return root.getJavaType() != null && SoftDeletable.class.isAssignableFrom(root.getJavaType());
  }

  /**
   * 将查询参数组合成“并且”的谓词
   * 注意：查询参数不能为空
   *
   * @param parameters 查询参数
   * @return 并且连接的谓词
   */
  public Optional<Predicate> and(QueryParameters parameters) {
    return getPredicates(parameters).stream().reduce(criteriaBuilder::and);
  }

  private class RootProxy implements InvocationHandler {
    private final Root<E> target;

    public RootProxy(Root<E> target) {
      this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (!whereSoftDeleted && "get".equals(method.getName()) && args != null && args.length == 1 && PROP_SOFT_DEL.equals(args[0])) {
        whereSoftDeleted = true;
      }
      return method.invoke(target, args);
    }

  }
}