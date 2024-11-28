package wlei.candy.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * 通用的实体仓库
 * <p>
 * Author: HeLei
 * Date: 2024/11/25
 *
 * @param <I> ID类型
 * @param <E> 实体类型
 */
public interface GenericRepository<I extends Serializable, E extends GenericEntity<I, E>> {
  /**
   * 添加一个实体
   *
   * @param entity 添加一个实体
   * @return 持久化状态的实体对象
   */
  E add(E entity);

  /**
   * 根据id进行查找
   *
   * @param id 根据该实体的id进行查询
   * @return 该id的实体
   */
  Optional<E> get(I id);

  /**
   * 更新指定的属性
   *
   * @param parameters       查询参数，注意不支持“.”分隔的路径查询
   * @param updateAttributes 更新的属性，注意不支持“.”分隔的级联更新
   * @return 更新行数
   */
  int update(QueryParameters parameters, KeyAttribute... updateAttributes);

  /**
   * 删除实体对象 这里的业务实体对象由于既没id也没有唯一性的约束，故只能传入持久化的实体对象进行删除
   *
   * @param id 实体的id
   */
  void delete(I id);

  /**
   * 根据查询参数进行删除
   * 注意：经过实践，直接调用CriteriaDelete删除的，不记录到审计日志中
   *
   * @param parameters 查询参数
   * @return 删除的行数
   */
  int delete(QueryParameters parameters);

  /**
   * 查询结果的总数
   *
   * @param supplier 调用方提供谓词构造器
   * @return 结果的总数
   */
  int count(PredicateSupplier<I, E> supplier);

  /**
   * 查询结果的总数
   *
   * @param parameters 查询参数
   * @return 结果的总数
   */
  int count(QueryParameters parameters);

  /**
   * 分页查询
   *
   * @param supplier 调用方提供谓词构造器
   * @param pageable 分页对象
   * @return 分页查询结果
   */
  Page<E> query(PredicateSupplier<I, E> supplier, @Nullable Pageable pageable);

  /**
   * 分页查询
   *
   * @param parameters 查询参数
   * @param pageable   分页对象
   * @return 分页查询结果
   */
  Page<E> query(QueryParameters parameters, @Nullable Pageable pageable);

  /**
   * 根据提供的参数进行动态查询
   *
   * @param supplier 调用方提供谓词构造器
   * @return 实体列表
   */
  List<E> query(PredicateSupplier<I, E> supplier);

  /**
   * 根据提供的参数进行动态查询
   *
   * @param parameters 动态参数
   * @return 实体列表
   */
  List<E> query(QueryParameters parameters);
}
