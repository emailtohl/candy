package wlei.candy.rsa;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 使用自实现的RSA算法加解密字符串
 *
 * @author HeLei
 */
public final class RSACipher extends CharEncoding {

  private static final Logger LOGGER = Logger.getLogger(RSACipher.class.getName());

  /**
   * 获取密钥对，数组的序号0是公钥，1是私钥
   *
   * @param bitLength 密钥长度
   * @return 公钥和私钥
   */
  public static String[] getKeys(int bitLength) {
    Keys keys = KeyGenerator.generateKeys(bitLength);
    String n = encode(keys.n.toString());
    String e = encode(keys.e.toString());
    String d = encode(keys.d.toString());
    return new String[]{n + delimiter + e, n + delimiter + d};
  }

  /**
   * 使用RSA算法加密明文
   *
   * @param plaintext        明文
   * @param encodedPublicKey 编码后的公钥
   * @return 密文
   * @throws ParamCodingException 执行失败
   */
  public static String RSAEncrypt(String plaintext, String encodedPublicKey) throws ParamCodingException {
    // 解码公钥
    BigInteger n, e;
    BigInteger[] tuples;
    try {
      String[] ne = encodedPublicKey.split(delimiter);
      n = new BigInteger(decodeToString(ne[0]));
      e = new BigInteger(decodeToString(ne[1]));
      tuples = resolve(plaintext, n);
    } catch (RuntimeException ex) {
      LOGGER.log(Level.WARNING, ex.getMessage(), ex);
      throw new ParamCodingException(ParamCodingException.PUBLIC_KEY_ERROR, "The Public key is incorrect", ex);
    }
    // 将明文转成数字后，由三个元素计算而成：余数、除数以及商
    BigInteger remainder = tuples[0], divisor = tuples[1], divide = tuples[2];
    // 加密余数和除数
    BigInteger encryptedRemainder = KeyGenerator.powModByMontgomery(remainder, e, n);
    BigInteger encryptedDivisor = KeyGenerator.powModByMontgomery(divisor, e, n);
    StringJoiner joiner = new StringJoiner(delimiter);
    // 前端jsencrypt加密的是字符串，为与之对应，此处不使用BigInteger#toByteArray()作为参数
    // 而是先将数字转成字符串，再转成字节数组
    joiner.add(encode(encryptedRemainder.toString()))
        .add(encode(encryptedDivisor.toString()))
        .add(encode(divide.toString()));
    return joiner.toString();
  }

  /**
   * 解密密文
   *
   * @param ciphertext        被RSAEncrypt或它使用的同样算法加密的密文
   * @param encodedPrivateKey 编码后的私钥
   * @return 明文
   * @throws ParamCodingException 执行失败
   */
  public static String RSADecrypt(String ciphertext, String encodedPrivateKey) throws ParamCodingException {
    // 解码私钥
    BigInteger n, d;
    try {
      String[] nd = encodedPrivateKey.split(delimiter);
      n = new BigInteger(decodeToString(nd[0]));
      d = new BigInteger(decodeToString(nd[1]));
    } catch (RuntimeException ex) {
      LOGGER.log(Level.WARNING, ex.getMessage(), ex);
      throw new ParamCodingException(ParamCodingException.PRIVATE_KEY_ERROR, "The Private key is incorrect", ex);
    }
    // 解码密文
    String[] tuples;
    BigInteger encryptedRemainder/*余*/, encryptedDivisor/*除数*/, divide/*商*/;
    try {
      tuples = ciphertext.split(delimiter);
      encryptedRemainder = new BigInteger(decodeToString(tuples[0]));
      encryptedDivisor = new BigInteger(decodeToString(tuples[1]));
      // 加密时使用的数字的字符串，所以解密后，需先将字节数组转成字符串
      divide = new BigInteger(decodeToString(tuples[2]));
    } catch (RuntimeException ex) {
      LOGGER.log(Level.WARNING, ex.getMessage(), ex);
      throw new ParamCodingException(ParamCodingException.CIPHERTEXT_ERROR, "The Ciphertext is incorrect", ex);
    }
    // 解密运算
    BigInteger remainder = KeyGenerator.powModByMontgomery(encryptedRemainder, d, n);
    BigInteger divisor = KeyGenerator.powModByMontgomery(encryptedDivisor, d, n);
    // 组装回原来的大数
    BigInteger p = remainder.add(divisor.multiply(divide));
    return CharEncoding.numberSequenceToString(p.toString());
  }

  /**
   * 将明文转码成大整数，然后表示成余、除数以及商三个部分，这样可以控制余和除数在较小范围内，以便于RSA加解密
   *
   * @param plaintext 明文
   * @return 大整数数组分别为余、除数以及商
   */
  private static BigInteger[] resolve(String plaintext, BigInteger n) {
    BigInteger p = new BigInteger(CharEncoding.stringToNumberSequence(plaintext));
    // 除数只需比模n小一点即可
    BigInteger divisor = new BigInteger(n.bitLength() - 1, new SecureRandom());
    BigInteger[] divideAndRemainder = p.divideAndRemainder(divisor);
    BigInteger divide = divideAndRemainder[0], remainder = divideAndRemainder[1];
    // p = remainder + divisor * divide
    // divide范围不定，有可能大于RSA密钥的n，故对小于n的remainder，divisor进行加密
    return new BigInteger[]{remainder, divisor, divide};
  }

}
