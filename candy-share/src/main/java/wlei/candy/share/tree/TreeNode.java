package wlei.candy.share.tree;

import java.util.*;

/**
 * 树节点
 * <p>
 * Author: HeLei
 * Date: 2024/12/13
 */
public class TreeNode {
  private SelfReference node;
  private List<TreeNode> children;

  /**
   * 将自引用的集合组装成树形结构
   *
   * @param src 自引用集合
   * @return 树形结构
   */
  public static List<TreeNode> build(List<? extends SelfReference> src) {
    List<TreeNode> tmp = new ArrayList<>();
    for (SelfReference sr : src) {
      tmp.add(new TreeNode().setNode(sr));
    }
    Set<String> keys = new HashSet<>();
    for (TreeNode n1 : tmp) {
      for (TreeNode n2 : tmp) {
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
    List<TreeNode> result = new ArrayList<>();
    for (TreeNode n : tmp) {
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
  public static List<SelfReference> bfs(List<TreeNode> treeNodes) {
    List<SelfReference> result = new ArrayList<>();
    List<TreeNode> tmp1 = new ArrayList<>(treeNodes);
    while (!tmp1.isEmpty()) {
      List<TreeNode> tmp2 = new ArrayList<>(tmp1);
      tmp1 = new ArrayList<>();
      for (TreeNode n : tmp2) {
        result.add(n.getNode());
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

  public TreeNode setNode(SelfReference node) {
    this.node = node;
    return this;
  }

  public List<TreeNode> getChildren() {
    return children;
  }

  public TreeNode setChildren(List<TreeNode> children) {
    this.children = children;
    return this;
  }
}
