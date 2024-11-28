package wlei.candy.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.io.Serializable;
import java.util.Optional;

/**
 * 由调用方提供谓词
 * <p>
 * Created by HeLei on 2021/6/29.
 */
public interface PredicateSupplier<I extends Serializable, E extends GenericEntity<I, E>> {
  /**
   * 提供查询条件
   *
   * @param b 标准查询构造器
   * @param r 查询根
   * @return 查询条件
   */
  Optional<Predicate> get(CriteriaBuilder b, Root<E> r);
}
