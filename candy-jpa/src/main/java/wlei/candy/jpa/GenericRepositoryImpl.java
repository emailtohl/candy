package wlei.candy.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static wlei.candy.jpa.GenericEntity.PROP_ID;

/**
 * <p>数据仓库的基础类，提供常用增、删、改、查的功能</p>
 * <p>对于查询，提供count、query，可将map作为查询参数传入</p>
 * <p>查询提供连表查询</p>
 * <p>此外，提供历史修订版的查询功能</p>
 * Author: HeLei
 * Date: 2024/11/25
 *
 * @param <I> ID实体类型
 * @param <E> 具体实体类型
 */
public abstract class GenericRepositoryImpl<I extends Serializable, E extends GenericEntity<I, E>> implements GenericRepository<I, E> {
  /**
   * 日志
   */
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  /**
   * 实体的类型
   */
  protected final Class<E> entityClass;
  /**
   * 实体管理器
   */
  @PersistenceContext
  protected EntityManager entityManager;

  /**
   * 默认构造方法，可分析泛型确定实体类型
   */
  public GenericRepositoryImpl() {
    Class<E> tmp = null;
    Class<?> clz = this.getClass();
    while (clz != GenericRepositoryImpl.class) {
      Type genericSuperclass = clz.getGenericSuperclass();
      if (genericSuperclass instanceof ParameterizedType) {
        ParameterizedType parameterizedType = castParameterizedType(genericSuperclass);
        Type[] arguments = parameterizedType.getActualTypeArguments();
        for (Type t : arguments) {
          if (t instanceof Class && GenericEntity.class.isAssignableFrom((Class<?>) t)) {
            tmp = castClass(t);
          }
        }
      }
      clz = clz.getSuperclass();
    }
    if (tmp == null) {
      String cause = "The type of entity is unknown";
      logger.error(cause);
      throw new IllegalStateException(cause);
    }
    entityClass = tmp;
  }

  private ParameterizedType castParameterizedType(Type genericSuperclass) {
    return (ParameterizedType) genericSuperclass;
  }

  @SuppressWarnings("unchecked")
  private Class<E> castClass(Type t) {
    return (Class<E>) t;
  }

  /**
   * 构造方法，指定实体类型
   *
   * @param entityClass 实体的类型
   */
  public GenericRepositoryImpl(Class<E> entityClass) {
    this.entityClass = entityClass;
  }

  /**
   * 添加一个实体
   *
   * @param entity 添加一个实体
   * @return 持久化状态的实体对象
   */
  @Override
  public E add(E entity) {
    entityManager.persist(entity);
    return entity;
  }

  /**
   * 根据id进行查找
   *
   * @param id 根据该实体的id进行查询
   * @return 该id的实体
   */
  @Override
  public Optional<E> get(I id) {
    E e = entityManager.find(entityClass, id);
    return Optional.ofNullable(e);
  }

  /**
   * 更新指定的属性
   *
   * @param parameters       查询参数，注意不支持“.”分隔的路径查询
   * @param updateAttributes 更新的属性，注意不支持“.”分隔的级联更新
   * @return 更新行数
   */
  @Override
  public int update(QueryParameters parameters, KeyAttribute... updateAttributes) {
    if (updateAttributes.length == 0) {
      throw new IllegalArgumentException("updateAttributes is empty");
    }
    if (parameters == null || parameters.isEmpty()) {
      logger.warn("QueryParameters is empty when update");
      return 0;
    }
    CriteriaBuilder b = entityManager.getCriteriaBuilder();
    CriteriaUpdate<E> q = b.createCriteriaUpdate(entityClass);
    Root<E> r = q.from(entityClass);
    for (KeyAttribute keyAttribute : updateAttributes) {
      Path<Object> path = keyAttribute.getPath(r);
      q.set(path, keyAttribute.value);
    }
    new PredicateBuilder<>(b, r).and(parameters).ifPresent(q::where);
    return entityManager.createQuery(q).executeUpdate();
  }

  /**
   * 删除实体对象 这里的业务实体对象由于既没id也没有唯一性的约束，故只能传入持久化的实体对象进行删除
   *
   * @param id 实体的id
   */
  @Override
  public void delete(I id) {
    get(id).ifPresent(entityManager::remove);
  }

  /**
   * 根据查询参数进行删除
   *
   * @param parameters 查询参数
   * @return 删除的行数
   */
  @Override
  public int delete(QueryParameters parameters) {
    if (parameters == null || parameters.isEmpty()) {
      logger.warn("QueryParameters is empty when delete");
      return 0;
    }
    CriteriaBuilder b = entityManager.getCriteriaBuilder();
    CriteriaDelete<E> q = b.createCriteriaDelete(entityClass);
    Root<E> r = q.from(entityClass);
    new PredicateBuilder<>(b, r).and(parameters).ifPresent(q::where);
    return entityManager.createQuery(q).executeUpdate();
  }

  /**
   * 查询结果的总数
   *
   * @param supplier 调用方提供谓词构造器
   * @return 结果的总数
   */
  @Override
  public int count(PredicateSupplier<I, E> supplier) {
    Objects.requireNonNull(supplier);
    CriteriaBuilder b = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> q = b.createQuery(Long.class);
    Root<E> r = q.from(entityClass);
    q = q.select(b.count(r));
    supplier.get(b, r).ifPresent(q::where);
    Long count = entityManager.createQuery(q).getSingleResult();
    return count.intValue();
  }

  /**
   * 查询结果的总数
   *
   * @param parameters 查询参数
   * @return 结果的总数
   */
  @Override
  public int count(QueryParameters parameters) {
    return count(getPredicateSupplier(parameters));
  }

  /**
   * 分页查询
   *
   * @param supplier 调用方提供谓词构造器
   * @param pageable 分页对象
   * @return 分页查询结果
   */
  public Page<E> query(PredicateSupplier<I, E> supplier, Pageable pageable) {
    Objects.requireNonNull(supplier);
    Objects.requireNonNull(pageable);
    int count = count(supplier);
    if (count == 0) {
      return Page.empty(pageable);
    }
    CriteriaBuilder b = entityManager.getCriteriaBuilder();
    CriteriaQuery<E> q = b.createQuery(entityClass);
    Root<E> r = q.from(entityClass);
    supplier.get(b, r).ifPresent(q::where);
    q = q.select(r).orderBy(QueryUtils.toOrders(pageable.getSort(), r, b));
    List<E> ls = entityManager.createQuery(q).setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();
    return new PageImpl<>(ls, pageable, count);
  }

  /**
   * 分页查询
   *
   * @param parameters 查询参数
   * @param pageable   分页对象
   * @return 分页查询结果
   */
  @Override
  public Page<E> query(QueryParameters parameters, @Nullable Pageable pageable) {
    return query(getPredicateSupplier(parameters), pageable);
  }

  /**
   * 根据提供的参数进行动态查询
   *
   * @param supplier 调用方提供谓词构造器
   * @return 实体列表
   */
  @Override
  public List<E> query(PredicateSupplier<I, E> supplier) {
    CriteriaBuilder b = entityManager.getCriteriaBuilder();
    CriteriaQuery<E> q = b.createQuery(entityClass);
    Root<E> r = q.from(entityClass);
    q = q.select(r).orderBy(b.desc(r.get(PROP_ID)));
    supplier.get(b, r).ifPresent(q::where);
    return entityManager.createQuery(q).getResultList();
  }

  /**
   * 简单地根据提供的参数，在实体root这层进行动态查询
   *
   * @param parameters 动态参数
   * @return 实体列表
   */
  @Override
  public List<E> query(QueryParameters parameters) {
    return query(getPredicateSupplier(parameters));
  }

  /**
   * 将查询参数转成谓词提供器
   *
   * @param parameters 查询参数
   * @return 谓词提供器
   */
  protected PredicateSupplier<I, E> getPredicateSupplier(QueryParameters parameters) {
    return (b, r) -> new PredicateBuilder<>(b, r).and(parameters);
  }

}
