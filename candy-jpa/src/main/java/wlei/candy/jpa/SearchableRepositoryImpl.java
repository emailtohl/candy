package wlei.candy.jpa;

import org.hibernate.search.engine.search.query.SearchQuery;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.query.dsl.SearchQueryOptionsStep;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.search.loading.dsl.SearchLoadingOptionsStep;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.util.common.SearchException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索库实现
 * <p>
 * Created by HeLei on 2021/4/24.
 *
 * @param <I> ID类型
 * @param <E> 实体类型
 */
public abstract class SearchableRepositoryImpl<I extends Serializable, E extends GenericEntity<I, E>> extends GenericRepositoryImpl<I, E> implements SearchableRepository<I, E> {
  private static volatile boolean isIndexed = false;
  private final String[] allFields;

  /**
   * 默认构造方法，可分析泛型确定实体类型
   */
  public SearchableRepositoryImpl() {
    super();
    allFields = new FieldsAnalyzer(entityClass).parse();
  }

  /**
   * 构造方法，指定实体类型
   *
   * @param entityClass 实体的类型
   */
  public SearchableRepositoryImpl(Class<E> entityClass) {
    super(entityClass);
    allFields = new FieldsAnalyzer(entityClass).parse();
  }

  /**
   * 分页查询与query关键字相关的实体对象
   *
   * @param query    字符串关键字
   * @param pageable 分页排序对象
   * @param onFields 查询哪些字段，若为空则查询所有被索引的字段
   * @return 查询结果
   */
  @Override
  public Page<E> search(String query, Pageable pageable, String... onFields) {
    try {
      SearchQueryOptionsStep<?, E, SearchLoadingOptionsStep, ?, ?> step = searchSession().search(entityClass)
          .where(f -> f.match().fields(onFields.length == 0 ? allFields : onFields).matching(query))
          .sort(s -> {
            for (Sort.Order order : pageable.getSort()) {
              if (order.isDescending()) {
                s.field(order.getProperty()).desc();
              } else {
                s.field(order.getProperty());
              }
            }
            return s.composite();
          });

      SearchQuery<E> q = step.toQuery();
      SearchResult<E> result = q.fetch((int) pageable.getOffset(), pageable.getPageSize());
      return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    } catch (SearchException e) {
      logger.warn(e.getMessage(), e);
      return new PageImpl<>(new ArrayList<>());
    }
  }

  /**
   * 查询与query关键字相关的实体对象列表
   *
   * @param query    字符串关键字
   * @param onFields 查询哪些字段，若为空则查询所有被索引的字段
   * @return 结果集合
   */
  @Override
  public List<E> search(String query, String... onFields) {
    try {
      SearchSession session = searchSession();
      return session.search(entityClass).where(f -> f.match().fields(onFields.length == 0 ? allFields : onFields)
          .matching(query)).fetchAllHits();
    } catch (SearchException e) {
      logger.warn(e.getMessage(), e);
      return new ArrayList<>();
    }
  }

  /**
   * 刷新实体索引
   *
   * @param id 实例id
   */
  @Override
  public void refreshIndex(I id) {
    get(id).ifPresent(entity -> searchSession().indexingPlan().addOrUpdate(entity));
  }

  /**
   * 获取已索引的管理器，调用方不用再考虑索引是否已执行
   *
   * @return 已索引的管理器
   */
  protected SearchSession searchSession() {
    SearchSession session = Search.session(entityManager);
    if (!isIndexed) {
      synchronized (SearchableRepositoryImpl.class) {
        if (!isIndexed) {
          try {
            session.massIndexer().startAndWait();
            isIndexed = true;
          } catch (InterruptedException e) {
            logger.error("{} index failed", entityClass.getSimpleName(), e);
            throw new IllegalStateException(e);
          }
        }
      }
    }
    return session;
  }

}
