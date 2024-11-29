package wlei.candy.jpa.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import wlei.candy.jpa.GenericEntity;
import wlei.candy.jpa.GenericRepository;

import java.io.Serializable;
import java.util.List;

/**
 * 封装Hibernate Search
 * <p>
 * Created by HeLei on 2021/4/24.
 *
 * @param <I> ID类型
 * @param <E> 实体类型
 */
public interface SearchableRepository<I extends Serializable, E extends GenericEntity<I, E>> extends GenericRepository<I, E> {
  /**
   * 分页查询与query关键字相关的实体对象
   *
   * @param query    字符串关键字
   * @param pageable 分页排序对象
   * @param onFields 查询哪些字段，若为空则查询所有被索引的字段
   * @return 查询结果
   */
  Page<E> search(String query, Pageable pageable, String... onFields);

  /**
   * 查询与query关键字相关的实体对象列表
   *
   * @param query    字符串关键字
   * @param onFields 查询哪些字段，若为空则查询所有被索引的字段
   * @return 结果集合
   */
  List<E> search(String query, String... onFields);

  /**
   * 刷新实体索引
   *
   * @param id 实例id
   */
  void refreshIndex(I id);

}
