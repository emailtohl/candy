package wlei.candy.share.tree;

/**
 * 自引用
 * <p>
 * Author: HeLei
 * Date: 2024/12/13
 */
public interface SelfReference {
  String getKey();

  SelfReference getParent();

}
