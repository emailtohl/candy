package wlei.candy.share.util;

import java.io.Serializable;
import java.util.Objects;

/**
 * 三元组
 * Created by HeLei on 2021/8/26.
 *
 * @param <A> left的类型
 * @param <B> middle的类型
 * @param <C> right的类型
 */
public class Triple<A, B, C> implements Serializable {
  public final A left;
  public final B middle;
  public final C right;

  public Triple(A left, B middle, C right) {
    this.left = left;
    this.middle = middle;
    this.right = right;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
    return Objects.equals(left, triple.left) && Objects.equals(middle, triple.middle) && Objects.equals(right, triple.right);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, middle, right);
  }

  @Override
  public String toString() {
    return String.format("left: %s\nmiddle: %s\nright: %s",
        left == null ? "" : left.toString(),
        middle == null ? "" : middle.toString(),
        right == null ? "" : right.toString()
    );
  }
}
