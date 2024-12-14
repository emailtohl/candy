package wlei.candy.share.tree;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Author: HeLei
 * Date: 2024/12/14
 */
public class Forest<T extends SelfReference> extends LinkedList<TreeNode<T>> {

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
   * 广度优先返回自引用集合，如此便于梳理依赖关系
   *
   * @return 广度优先返回自引用集合
   */
  public List<T> breadthFirst() {
    List<T> result = new LinkedList<>();
    bfs(treeNode -> result.add(treeNode.getNode()));
    return result;
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

}
