package wlei.candy.share.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Author: HeLei
 * Date: 2024/12/21
 */
public class LongId {

  public static long getUniqueId(String str) {
    try {
      // 获取 SHA-256 摘要算法实例
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      // 将字符串转换为字节数组
      byte[] hashBytes = md.digest(str.getBytes());
      // 获取哈希值的前 8 字节（64 位）
      long uniqueId = 0;
      for (int i = 0; i < 8; i++) {
        uniqueId = (uniqueId << 8) | (hashBytes[i] & 0xFF);
      }
      return uniqueId;
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 algorithm not available", e);
    }
  }
}
