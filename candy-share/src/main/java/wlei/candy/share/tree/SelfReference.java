package wlei.candy.share.tree;

/**
 * 自引用
 * <p>
 * Author: HeLei
 * Date: 2024/12/13
 */
public interface SelfReference<T extends SelfReference<T>> {
  String getKey();

  T getParent();

  T setParent(T parent);
}
