package wlei.candy.jpa;

/**
 * 常用的数据访问仓库，ID是Long类型
 * <p>
 * Author: HeLei
 * Date: 2024/11/28
 */
public interface UsualRepository<E extends UsualEntity<E>> extends GenericRepository<Long, E> {
}
