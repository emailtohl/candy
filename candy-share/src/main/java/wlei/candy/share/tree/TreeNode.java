package wlei.candy.share.tree;

import java.util.List;

/**
 * 树节点
 * <p>
 * Author: HeLei
 * Date: 2024/12/13
 */
public class TreeNode<T extends SelfReference> {
  private String key;
  private String parentKey;
  private T node;
  private List<TreeNode<T>> children;

  public TreeNode() {
  }

  public TreeNode(T node) {
    if (node == null) {
      return;
    }
    this.node = node;
    this.key = node.getKey();
  }

  void bindParentKey() {
    if (children == null) {
      return;
    }
    for (TreeNode<T> child : children) {
      child.setParentKey(key);
      child.bindParentKey();
    }
  }

  @Override
  public String toString() {
    return key == null ? "" : key;
  }

  public String getKey() {
    return key;
  }

  public TreeNode<T> setKey(String key) {
    this.key = key;
    return this;
  }

  public String getParentKey() {
    return parentKey;
  }

  public TreeNode<T> setParentKey(String parentKey) {
    this.parentKey = parentKey;
    return this;
  }

  public T getNode() {
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
