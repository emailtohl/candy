package wlei.candy.share.tree;

import java.util.*;

/**
 * 树节点
 * <p>
 * Author: HeLei
 * Date: 2024/12/13
 */
public class TreeNode<T extends SelfReference> {
  private T node;
  private List<TreeNode<T>> children;

  /**
   * 将自引用的集合组装成树形结构
   *
   * @param src 自引用集合
   * @return 树形结构
   */
  public static <T extends SelfReference> List<TreeNode<T>> build(List<T> src) {
    List<TreeNode<T>> tmp = new ArrayList<>();
    for (T sr : src) {
      TreeNode<T> n = new TreeNode<>();
      n.setNode(sr);
      tmp.add(n);
    }
    Set<String> keys = new HashSet<>();
    for (TreeNode<T> n1 : tmp) {
      for (TreeNode<T> n2 : tmp) {
        if (!Objects.equals(n1.getNode().parentKey(), n2.getNode().key())) {
          continue;
        }
        if (n2.getChildren() == null) {
          n2.setChildren(new ArrayList<>());
        }
        n2.getChildren().add(n1);
        keys.add(n1.getNode().key());
        break;
      }
    }
    List<TreeNode<T>> result = new ArrayList<>();
    for (TreeNode<T> n : tmp) {
      if (keys.contains(n.getNode().key())) {
        continue;
      }
      result.add(n);
    }
    return result;
  }

  /**
   * 广度优先返回自引用集合，如此便于梳理依赖关系
   *
   * @param treeNodes 树形结构
   * @return 广度优先返回自引用集合
   */
  @SuppressWarnings("unchecked")
  public static <T extends SelfReference> List<T> bfs(List<TreeNode<T>> treeNodes) {
    List<T> result = new ArrayList<>();
    List<TreeNode<T>> tmp1 = new ArrayList<>(treeNodes);
    while (!tmp1.isEmpty()) {
      List<TreeNode<T>> tmp2 = new ArrayList<>(tmp1);
      tmp1 = new ArrayList<>();
      for (TreeNode<T> n : tmp2) {
        result.add((T) n.getNode());
        if (n.getChildren() != null) {
          tmp1.addAll(n.getChildren());
        }
      }
    }
    return result;
  }

  public SelfReference getNode() {
    return node;
  }

  public TreeNode<T> setNode(T node) {
    this.node = node;
    return this;
  }

  public List<TreeNode<T>> getChildren() {
    return children;
  }

  public TreeNode<T> setChildren(List<TreeNode<T>> children) {
    this.children = children;
    return this;
  }
}
