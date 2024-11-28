package wlei.candy.jpa;

/**
 * 常用的仓库，ID使用Long型
 * <p>
 * Author: HeLei
 * Date: 2024/11/28
 */
public class UsualRepositoryImpl<E extends UsualEntity<E>> extends GenericRepositoryImpl<Long, E> implements UsualRepository<E> {
}
