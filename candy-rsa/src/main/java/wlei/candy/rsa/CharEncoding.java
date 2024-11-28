package wlei.candy.rsa;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 字符编码工具
 *
 * @author HeLei
 */
public class CharEncoding {

  /**
   * 分隔符
   */
  static final String delimiter = ",";
  private static final Logger LOGGER = Logger.getLogger(CharEncoding.class.getName());

  /**
   * 将字符串编码成数字序列，以便于生成BigInteger
   * <p>
   * 数字序列是每个字符的Unicode码的组合，在每个Unicode码之前插入该Unicode码的长度 例如字符串"你好",'你'是20320，有5位，'好'是22909，有5位，那么编码成"520320522909"
   * </p>
   * <p>
   * 为什么不用简单的 new BigInteger(text.getBytes()) 呢？这是因为在JavaScript没有对应的方法，为了与JavaScript的编码保持一致，故采用这种算法
   * </p>
   *
   * @param text 字符串
   * @return 数字序列
   */
  public static String stringToNumberSequence(String text) {
    char[] chars = text.toCharArray();
    StringBuilder sb = new StringBuilder();
    for (int unicode : chars) {
      // 第一个数字作为该Unicode码的长度
      sb.append(String.valueOf(unicode).length()).append(unicode);
    }
    return sb.toString();
  }

  /**
   * 将stringToNumberSequence转成的编码进行反编码
   *
   * @param numberSequence 数字序列
   * @return 原字符串
   * @throws ParamCodingException 如果不是stringToNumberSequence生成的数字序列，则抛此异常
   */
  public static String numberSequenceToString(String numberSequence) throws ParamCodingException {
    StringBuilder sb = new StringBuilder();
    int length = numberSequence.length();
    int i = 0;
    int j = i + 1;
    try {
      while (j < length) {
        int size = Integer.parseInt(numberSequence.substring(i, j));
        i += 1 + size;
        String num = numberSequence.substring(j, i);
        sb.append((char) Integer.parseInt(num));
        j = i + 1;
      }
    } catch (NumberFormatException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      throw new ParamCodingException("Numeric sequences contain non-numeric characters as parameters", e);
    }
    return sb.toString();
  }

  /**
   * 转码成Base64格式的字符串
   *
   * @param bytes 字节数组
   * @return Base64格式的字符串
   */
  public static String encode(byte[] bytes) {
    return Base64.getEncoder().encodeToString(bytes);
  }

  /**
   * 转码成Base64格式的字符串
   *
   * @param txt 原文
   * @return Base64格式的字符串
   */
  public static String encode(String txt) {
    return encode(txt.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * 解码Base64编码的字符串
   *
   * @param encodeText Base64编码的字符串
   * @return 字节数组
   * @throws ParamCodingException 解码失败则报此异常
   */
  public static byte[] decodeToBytes(String encodeText) throws ParamCodingException {
    try {
      return Base64.getDecoder().decode(encodeText);
    } catch (IllegalArgumentException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      throw new ParamCodingException("Base64 decode to Bytes error", e);
    }
  }

  /**
   * 解码Base64编码的字符串
   *
   * @param encodeText Base64编码的字符串
   * @return 原文
   * @throws ParamCodingException 解码失败则报此异常
   */
  public static String decodeToString(String encodeText) throws ParamCodingException {
    try {
      byte[] bytes = decodeToBytes(encodeText);
      return new String(bytes, StandardCharsets.UTF_8);
    } catch (ParamCodingException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      throw new ParamCodingException("Base64 decode to String error", e);
    }
  }

}
