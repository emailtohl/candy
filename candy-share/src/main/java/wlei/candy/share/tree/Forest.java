package wlei.candy.share.tree;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Author: HeLei
 * Date: 2024/12/14
 */
public class Forest<T extends SelfReference<T>> extends LinkedList<TreeNode<T>> {

  public Forest() {
  }

  public Forest(List<T> src) {
    List<TreeNode<T>> tmp = new LinkedList<>();
    for (T sr : src) {
      // 此处会初始化节点的key
      tmp.add(new TreeNode<>(sr));
    }
    Set<String> keys = new HashSet<>();
    for (TreeNode<T> n1 : tmp) {
      Optional<String> o = Optional.ofNullable(n1.getNode().getParent()).map(SelfReference::getKey);
      if (!o.isPresent()) {
        continue;
      }
      String parentKey = o.get();
      n1.setParentKey(parentKey);
      for (TreeNode<T> n2 : tmp) {
        if (!parentKey.equals(n2.getNode().getKey())) {
          continue;
        }
        if (n2.getChildren() == null) {
          n2.setChildren(new LinkedList<>());
        }
        n2.getChildren().add(n1);
        keys.add(n1.getNode().getKey());
        break;
      }
    }
    for (TreeNode<T> n : tmp) {
      if (keys.contains(n.getNode().getKey())) {
        continue;
      }
      super.add(n);
    }
  }

  /**
   * 根据指定路径的key查找或创建节点
   *
   * @param keys 层次路径的key
   * @return 查找到或创建的节点
   */
  public TreeNode<T> findOrCreateNode(String... keys) {
    if (keys.length == 0) {
      throw new IllegalArgumentException("miss args");
    }
    TreeNode<T> current = stream().filter(n -> StringUtils.equals(n.getKey(), keys[0])).findFirst().orElseGet(() -> {
      TreeNode<T> n = new TreeNode<>();
      n.setKey(keys[0]);
      add(n);
      return n;
    });
    for (int i = 1; i < keys.length; i++) {
      current = findOrCreateChild(current, keys[i]);
    }
    return current;
  }

  /**
   * 查找当前节点的子节点，如果找到则返回，如果没有找到则创建一个返回
   *
   * @param current  当前节点
   * @param childKey 子节点的key
   * @return key为childKey的节点
   */
  private TreeNode<T> findOrCreateChild(TreeNode<T> current, String childKey) {
    if (current.getChildren() == null) {
      current.setChildren(new LinkedList<>());
    }
    for (TreeNode<T> child : current.getChildren()) {
      // 如果找到子节点，直接返回
      if (StringUtils.equals(childKey, child.getKey())) {
        return child;
      }
    }
    // 如果没有找到，创建新的子节点
    TreeNode<T> newChild = new TreeNode<>();
    newChild.setKey(childKey);
    current.getChildren().add(newChild);
    newChild.setParentKey(current.getKey());
    return newChild;
  }

  /**
   * 广度优先返回自引用集合，如此便于梳理依赖关系
   *
   * @return 广度优先返回自引用集合
   */
  public List<T> allNodes() {
    List<TreeNode<T>> nodes = new LinkedList<>();
    bfs(nodes::add);
    for (TreeNode<T> tn : nodes) {
      tn.parentIfAbsent();
    }
    return nodes.stream().map(TreeNode::getNode).collect(Collectors.toList());
  }

  private void bfs(Consumer<TreeNode<T>> consumer) {
    List<TreeNode<T>> tmp1 = new LinkedList<>(this);
    while (!tmp1.isEmpty()) {
      List<TreeNode<T>> tmp2 = new LinkedList<>(tmp1);
      tmp1 = new ArrayList<>();
      for (TreeNode<T> n : tmp2) {
        consumer.accept(n);
        if (n.getChildren() != null) {
          tmp1.addAll(n.getChildren());
        }
      }
    }
  }

  /**
   * 如果节点没有key，则初始化key
   */
  public void initKey() {
    AtomicInteger keyGen = new AtomicInteger();
    bfs(treeNode -> {
      if (treeNode.getKey() == null) {
        String key = Optional.ofNullable(treeNode.getNode())
            .map(T::getKey)
            .orElseGet(() -> String.valueOf(keyGen.incrementAndGet()));
        treeNode.setKey(key);
      }
    });
    bindParentKey();
  }

  /**
   * 绑定父节点的key
   */
  private void bindParentKey() {
    for (TreeNode<T> tn : this) {
      tn.bindParentKey();
    }
  }

  public String format() {
    StringJoiner j = new StringJoiner("");
    for (TreeNode<T> tn : this) {
      j.add(preFormat(null, tn));
    }
    return j.toString();
  }

  private String preFormat(String pre, TreeNode<T> tn) {
    String key;
    if (StringUtils.isBlank(pre)) {
      key = tn.getKey();
    } else {
      key = String.format("%s.%s", pre, tn.getKey());
    }
    StringJoiner j = new StringJoiner("");
    j.add(String.format("{%s:%s}", key, tn));
    if (tn.getChildren() != null) {
      for (TreeNode<T> child : tn.getChildren()) {
        j.add(preFormat(key, child));
      }
    }
    return j.toString();
  }

  @Override
  public String toString() {
    return format();
  }
}
