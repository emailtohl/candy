package wlei.candy.jpa;

import java.io.Serializable;

/**
 * 轻量支持软删除，仅在Query查询时排除已软删除的记录，至于对象的级联加载在Hibernate框架层完成，故不能实现
 * <p>
 * Author: HeLei
 * Date: 2024/12/10
 */
public interface SoftDeletable<I extends Serializable, E extends GenericEntity<I, E>> {
  String PROP_SOFT_DEL = "deleted";

  boolean isDeleted();

  E setDeleted(boolean deleted);
}
