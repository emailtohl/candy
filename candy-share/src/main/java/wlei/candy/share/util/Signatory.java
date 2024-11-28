package wlei.candy.share.util;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 签名工具
 */
public enum Signatory {
  MD5withRSA("RSA", 1024, "MD5withRSA"),
  SHA1withRSA("RSA", 1024, "SHA1withRSA"),
  SHA256withRSA("RSA", 1024, "SHA256withRSA"),
  SHA512withRSA("RSA", 1024, "SHA512withRSA"),
  SHA256withDSA("DSA", 1024, "SHA256withDSA"),
  SHA256withECDSA("EC", 256, "SHA256withECDSA");

  private static final Logger LOGGER = Logger.getLogger(Signatory.class.getName());
  /**
   * 算法
   */
  private final String algorithm;
  /**
   * 签名
   */
  private final String signature;
  /**
   * 默认密钥长度，可指定
   */
  private final int bit;

  Signatory(String algorithm, int bit, String signature) {
    this.algorithm = algorithm;
    this.bit = bit;
    this.signature = signature;
  }

  /**
   * 获取Base64编码的公钥和私钥，私钥用于自己的签名，公钥发送给消息接受者用于验签
   *
   * @return String[0]是公钥，String[1]是私钥
   */
  public Pair<String, String> getEncodedKeyPair() {
    KeyPairGenerator kg;
    try {
      kg = KeyPairGenerator.getInstance(algorithm);
    } catch (NoSuchAlgorithmException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new IllegalStateException(e);
    }
    kg.initialize(bit, new SecureRandom());
    KeyPair keyPair = kg.generateKeyPair();
    PublicKey publicKey = keyPair.getPublic();
    PrivateKey privateKey = keyPair.getPrivate();
    String pub = Base64.getEncoder().encodeToString(publicKey.getEncoded());
    String pri = Base64.getEncoder().encodeToString(privateKey.getEncoded());
    return ImmutablePair.of(pub, pri);
  }

  /**
   * 解析Base64编码后的公钥
   *
   * @param encodedPublicKey 公钥
   * @return 公钥实例
   * @throws IllegalArgumentException 公钥格式不正确
   */
  public PublicKey decodePublicKey(String encodedPublicKey) throws IllegalArgumentException {
    try {
      byte[] data = decode(encodedPublicKey);
      X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
      KeyFactory factory = KeyFactory.getInstance(algorithm);
      return factory.generatePublic(spec);
    } catch (NoSuchAlgorithmException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new IllegalStateException(e);
    } catch (InvalidKeySpecException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      throw new IllegalArgumentException("The public key is not properly formatted", e);
    }
  }

  /**
   * 解析Base64编码后的私钥
   *
   * @param encodedPrivateKey 私钥
   * @return 私钥实例
   * @throws IllegalArgumentException 私钥格式不正确
   */
  public PrivateKey decodePrivateKey(String encodedPrivateKey) throws IllegalArgumentException {
    try {
      byte[] data = decode(encodedPrivateKey);
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(data);
      KeyFactory factory = KeyFactory.getInstance(algorithm);
      return factory.generatePrivate(spec);
    } catch (NoSuchAlgorithmException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new IllegalStateException(e);
    } catch (IllegalArgumentException | InvalidKeySpecException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      throw new IllegalArgumentException("Private key encoding error", e);
    }
  }

  /**
   * 将明文进行签名
   *
   * @param plaintext         明文
   * @param encodedPrivateKey Base64编码后的私钥
   * @return 签名码
   * @throws IllegalArgumentException 私钥不正确
   */
  public String sign(byte[] plaintext, String encodedPrivateKey) throws IllegalArgumentException {
    try {
      PrivateKey privateKey = decodePrivateKey(encodedPrivateKey);
      Signature signature = Signature.getInstance(this.signature);
      signature.initSign(privateKey);
      signature.update(plaintext);
      byte[] result = signature.sign();
      return Base64.getEncoder().encodeToString(result);
    } catch (NoSuchAlgorithmException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new IllegalStateException(e);
    } catch (SignatureException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      throw new IllegalArgumentException("Signature encoding error", e);
    } catch (InvalidKeyException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      throw new IllegalArgumentException("Private key encoding error", e);
    }
  }

  /**
   * 将明文进行签名
   *
   * @param plaintext         明文
   * @param encodedPrivateKey Base64编码后的私钥
   * @return 签名码
   * @throws IllegalArgumentException 私钥不正确
   */
  public String sign(String plaintext, String encodedPrivateKey) throws IllegalArgumentException {
    return sign(plaintext.getBytes(StandardCharsets.UTF_8), encodedPrivateKey);
  }

  /**
   * 校验签名
   *
   * @param plaintext        明文
   * @param sign             签名码
   * @param encodedPublicKey Base64编码后的公钥
   * @return 该签名码是否匹配此明文
   * @throws IllegalArgumentException 签名码不正确或公钥不正确
   */
  public boolean verify(byte[] plaintext, String sign, String encodedPublicKey) throws IllegalArgumentException {
    try {
      PublicKey publicKey = decodePublicKey(encodedPublicKey);
      Signature signature = Signature.getInstance(this.signature);
      signature.initVerify(publicKey);
      signature.update(plaintext);
      byte[] bytes = Base64.getDecoder().decode(sign);
      return signature.verify(bytes);
    } catch (NoSuchAlgorithmException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new IllegalStateException(e);
    } catch (SignatureException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      throw new IllegalArgumentException("Signature encoding error", e);
    } catch (InvalidKeyException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      throw new IllegalArgumentException("Public key encoding error", e);
    }
  }

  /**
   * 校验签名
   *
   * @param plaintext        明文
   * @param sign             签名码
   * @param encodedPublicKey Base64编码后的公钥
   * @return 该签名码是否匹配此明文
   * @throws IllegalArgumentException 签名码不正确或公钥不正确
   */
  public boolean verify(String plaintext, String sign, String encodedPublicKey) throws IllegalArgumentException {
    return verify(plaintext.getBytes(StandardCharsets.UTF_8), sign, encodedPublicKey);
  }

  private byte[] decode(String encodedText) throws IllegalArgumentException {
    try {
      return Base64.getDecoder().decode(encodedText);
    } catch (IllegalArgumentException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      throw new IllegalArgumentException("Base64 decode error", e);
    }
  }

}
