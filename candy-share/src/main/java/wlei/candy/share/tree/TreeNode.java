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
