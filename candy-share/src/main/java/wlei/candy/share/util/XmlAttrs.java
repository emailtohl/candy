package wlei.candy.share.util;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 简易的xml属性的数据结构，但满足自定义的equals hashcode，可在容器中识别
 *
 * @author HeLei
 */
public class XmlAttrs extends HashMap<String, String> {

  /**
   * hashCode方法
   *
   * @return hash值
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    for (Entry<String, String> e : super.entrySet()) {
      String val = e.getValue();
      if (StringUtils.hasText(val)) {
        result = prime * result + val.trim().hashCode();
      } else {
        // 空白的属性仍然会认为不一样，所以hashcode得计算一个值
        result = prime * result;
      }
    }
    return result;
  }

  /**
   * equals方法
   *
   * @param obj 比较对象
   * @return 比较结果
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    XmlAttrs other = (XmlAttrs) obj;
    // 若两个集合之间有差集，那么就看差集部分是否有值，若有值，则两者不相等
    if (!diff(other)) {
      return false;
    }
    //若两个集合元素数目不同，则两者不相等
    if (size() != other.size()) {
      return false;
    }
    // 差集比较没有差别后，就在交集中逐个对每个属性进行比较
    return intersection(other);
  }

  /**
   * 在差集中查看是否有属性的值不为空，空字符串和null都视为空
   *
   * @param other 另一个对比对象
   * @return 若差集中有属性不为空，则返回false，否则返回true
   */
  private boolean diff(XmlAttrs other) {
    Set<String> thisKeySet = new HashSet<>(super.keySet());
    Set<String> otherKeySet = new HashSet<>(other.keySet());
    if (thisKeySet.removeAll(otherKeySet)) {
      for (String key : thisKeySet) {
        if (StringUtils.hasText(super.get(key))) {
          return false;
        }
      }
    }
    thisKeySet = new HashSet<>(super.keySet());
    otherKeySet = new HashSet<>(other.keySet());
    if (otherKeySet.removeAll(thisKeySet)) {
      for (String key : otherKeySet) {
        if (StringUtils.hasText(other.get(key))) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * 在交集里面查找是否有不相等的属性，空字符串和null都视为相等
   *
   * @param other 另一个对比对象
   * @return 若全部匹配，则返回true，否则返回false
   */
  private boolean intersection(XmlAttrs other) {
    Set<String> thisKeySet = new HashSet<>(super.keySet());
    Set<String> otherKeySet = new HashSet<>(other.keySet());
    Set<String> keys;
    if (thisKeySet.size() == otherKeySet.size()) {
      thisKeySet.retainAll(otherKeySet);
      keys = thisKeySet;
    } else {
      return false;
    }
    for (String key : keys) {
      String thisVal = super.get(key);
      String otherVal = other.get(key);
      if (StringUtils.hasText(thisVal) && StringUtils.hasText(otherVal)) {
        if (!thisVal.trim().equals(otherVal.trim())) {
          return false;
        }
      }
    }
    return true;
  }

}
