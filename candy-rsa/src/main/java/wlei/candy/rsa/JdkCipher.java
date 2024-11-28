package wlei.candy.rsa;

import javax.crypto.KeyGenerator;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 封装JDK的加密工具实现RSA和AES的加密和解密
 *
 * @author HeLei
 */
public final class JdkCipher extends CharEncoding {

  private static final Logger LOGGER = Logger.getLogger(JdkCipher.class.getName());

  private static final String RSA = "RSA";
  private static final String AES = "AES";

  private JdkCipher() {
  }

  static SecretKey getAESKey() {
    KeyGenerator kg;
    try {
      kg = KeyGenerator.getInstance(AES);
    } catch (NoSuchAlgorithmException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new IllegalStateException(e);
    }
    kg.init(new SecureRandom());
    return kg.generateKey();
  }

  /**
   * @return 获取Base64编码的AES密钥
   */
  public static String getEncodedAESKey() {
    SecretKey key = getAESKey();
    byte[] data = key.getEncoded();
    return encode(data);
  }

  /**
   * 将Base64编码的AES密钥生成对应的实例
   *
   * @param key Base64编码的AES密钥
   * @return 密钥实例
   * @throws ParamCodingException 密钥不正确
   */
  public static SecretKey decodeAESKey(String key) throws ParamCodingException {
    byte[] data = decodeToBytes(key);
    return new SecretKeySpec(data, AES);
  }

  /**
   * 使用Base64编码的AES密钥加密明文
   *
   * @param plaintext     明文
   * @param encodedAESKey Base64编码的AES密钥
   * @return 密文
   * @throws ParamCodingException 密钥不正确
   */
  public static String AESEncrypt(String plaintext, String encodedAESKey) throws ParamCodingException {
    SecretKey aesKey = decodeAESKey(encodedAESKey);
    try (ByteArrayInputStream in = new ByteArrayInputStream(plaintext.getBytes());
         ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      Cipher cipher = Cipher.getInstance(AES);
      cipher.init(Cipher.ENCRYPT_MODE, aesKey);
      transform(in, out, cipher);
      return encode(out.toByteArray());
    } catch (IOException | NoSuchAlgorithmException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new IllegalStateException(e);
    } catch (GeneralSecurityException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      throw new ParamCodingException(ParamCodingException.KEY_ERROR, "The AES key is incorrect", e);
    }
  }

  /**
   * 用Base64编码的AES密钥解密密文
   *
   * @param ciphertext    密文
   * @param encodedAESKey Base64编码的AES密钥
   * @return 明文
   * @throws ParamCodingException 密文格式不正确或密码格式不正确
   */
  public static String AESDecrypt(String ciphertext, String encodedAESKey) throws ParamCodingException {
    byte[] data = decodeToBytes(ciphertext);
    SecretKey aesKey = decodeAESKey(encodedAESKey);
    try (ByteArrayInputStream in = new ByteArrayInputStream(data);
         ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      Cipher cipher = Cipher.getInstance(AES);
      cipher.init(Cipher.DECRYPT_MODE, aesKey);
      transform(in, out, cipher);
      return out.toString();
    } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new IllegalStateException(e);
    } catch (InvalidKeyException e) {
      throw new ParamCodingException(ParamCodingException.CIPHERTEXT_ERROR | ParamCodingException.KEY_ERROR, "Ciphertext or AES key is incorrect", e);
    }
  }

  static KeyPair getRSAKeyPair(int bit) {
    KeyPairGenerator kg;
    try {
      kg = KeyPairGenerator.getInstance(RSA);
    } catch (NoSuchAlgorithmException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new IllegalStateException(e);
    }
    kg.initialize(bit, new SecureRandom());
    return kg.generateKeyPair();
  }

  /**
   * 根据密钥长度创建Base64编码的RSA密钥对
   *
   * @param bit 密钥长度
   * @return String[0]是公钥，String[1]是私钥
   */
  public static String[] getEncodedRSAKeyPair(int bit) {
    KeyPair keyPair = getRSAKeyPair(bit);
    PublicKey publicKey = keyPair.getPublic();
    PrivateKey privateKey = keyPair.getPrivate();
    String[] result = new String[2];
    result[0] = encode(publicKey.getEncoded());
    result[1] = encode(privateKey.getEncoded());
    return result;
  }

  /**
   * 将Base64编码的RSA公钥解析成实例
   *
   * @param publicKey Base64编码的RSA公钥
   * @return 公钥实例
   * @throws ParamCodingException 公钥编码不正确
   */
  public static PublicKey decodePublicKey(String publicKey) throws ParamCodingException {
    try {
      byte[] data = decodeToBytes(publicKey);
      X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
      KeyFactory factory = KeyFactory.getInstance(RSA);
      return factory.generatePublic(spec);
    } catch (NoSuchAlgorithmException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new IllegalStateException(e);
    } catch (InvalidKeySpecException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      throw new ParamCodingException(ParamCodingException.PUBLIC_KEY_ERROR, "The Public key is incorrect", e);
    }
  }

  /**
   * 将Base64编码的RSA私钥解析成实例
   *
   * @param privateKey Base64编码的RSA私钥
   * @return 密钥实例
   * @throws ParamCodingException 私钥格式不正确
   */
  public static PrivateKey decodePrivateKey(String privateKey) throws ParamCodingException {
    try {
      byte[] data = decodeToBytes(privateKey);
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(data);
      KeyFactory factory = KeyFactory.getInstance(RSA);
      return factory.generatePrivate(spec);
    } catch (NoSuchAlgorithmException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new IllegalStateException(e);
    } catch (InvalidKeySpecException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      throw new ParamCodingException(ParamCodingException.PRIVATE_KEY_ERROR, "The Private key is incorrect", e);
    }
  }

  /**
   * 使用RSA算法加密明文
   *
   * @param plaintext        明文
   * @param encodedPublicKey Base64编码后的公钥
   * @return 密文
   * @throws ParamCodingException 公钥编码不正确
   */
  public static String RSAEncrypt(String plaintext, String encodedPublicKey) throws ParamCodingException {
    PublicKey publicKey = decodePublicKey(encodedPublicKey);
    BigInteger[] tuples = resolve(plaintext);
    // 将明文转成数字后，由三个元素计算而成：余数、被除数以及除数
    BigInteger remainder = tuples[0], divisor = tuples[1], divide = tuples[2];
    // 前端jsencrypt加密的是字符串，为与之对应，此处不使用BigInteger#toByteArray()作为参数
    // 而是先将数字转成字符串，再转成字节数组
    byte[] encryptedRemainder = RSAEncrypt(remainder.toString().getBytes(), publicKey);
    byte[] encryptedDivisor = RSAEncrypt(divisor.toString().getBytes(), publicKey);
    StringJoiner joiner = new StringJoiner(delimiter);
    joiner.add(encode(encryptedRemainder))
        .add(encode(encryptedDivisor))
        .add(encode(divide.toString().getBytes()));
    return joiner.toString();
  }

  /**
   * 将明文转码成大整数，然后表示成余、除数以及商三个部分，这样可以控制余和除数在较小范围内，以便于RSA加解密
   *
   * @param plaintext 明文
   * @return 大整数数组分别为余、除数以及商
   */
  private static BigInteger[] resolve(String plaintext) {
    BigInteger p = new BigInteger(CharEncoding.stringToNumberSequence(plaintext));
    // JDK的RSA密码最低位数是512，加密数据位数不超过53个字节
    BigInteger divisor = new BigInteger(53, new SecureRandom());
    BigInteger[] divideAndRemainder = p.divideAndRemainder(divisor);
    BigInteger divide = divideAndRemainder[0], remainder = divideAndRemainder[1];
    // p = remainder + divisor * divide
    // divide范围不定，有可能大于RSA密钥的n，故对小于n的remainder，divisor进行加密
    return new BigInteger[]{remainder, divisor, divide};
  }

  /**
   * 解密密文
   *
   * @param ciphertext        被RSAEncrypt或它使用的同样算法加密的密文
   * @param encodedPrivateKey 编码后的私钥
   * @return 明文
   * @throws ParamCodingException 密文格式不正确或私钥格式不正确
   */
  public static String RSADecrypt(String ciphertext, String encodedPrivateKey) throws ParamCodingException {
    PrivateKey privateKey = decodePrivateKey(encodedPrivateKey);
    try {
      String[] tuples = ciphertext.split(delimiter);
      byte[] encryptedRemainder = decodeToBytes(tuples[0]);
      byte[] encryptedDivisor = decodeToBytes(tuples[1]);
      byte[] _remainder = RSADecrypt(encryptedRemainder, privateKey);
      byte[] _divisor = RSADecrypt(encryptedDivisor, privateKey);
      byte[] _divide = decodeToBytes(tuples[2]);
      // 加密时使用的数字的字符串，所以解密后，需先将字节数组转成字符串
      BigInteger remainder = new BigInteger(new String(_remainder));
      BigInteger divisor = new BigInteger(new String(_divisor));
      BigInteger divide = new BigInteger(new String(_divide));
      // 组装回原来的大数
      BigInteger p = remainder.add(divisor.multiply(divide));
      return CharEncoding.numberSequenceToString(p.toString());
    } catch (RuntimeException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      // 处理密文时，任何运行时异常都抛出密文格式不正确的异常
      throw new ParamCodingException(ParamCodingException.CIPHERTEXT_ERROR, "The Ciphertext is incorrect", e);
    }
  }

  private static byte[] RSAEncrypt(byte[] plaintext, PublicKey publicKey) throws ParamCodingException {
    try {
      Cipher cipher = Cipher.getInstance(RSA);
      cipher.init(Cipher.ENCRYPT_MODE, publicKey);
      return cipher.doFinal(plaintext);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new IllegalStateException(e);
    } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      throw new ParamCodingException(ParamCodingException.PUBLIC_KEY_ERROR, "The public key is not properly formatted", e);
    }
  }

  private static byte[] RSADecrypt(byte[] ciphertext, PrivateKey privateKey) throws ParamCodingException {
    try {
      Cipher cipher = Cipher.getInstance(RSA);
      cipher.init(Cipher.DECRYPT_MODE, privateKey);
      return cipher.doFinal(ciphertext);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new IllegalStateException(e);
    } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      throw new ParamCodingException(ParamCodingException.PRIVATE_KEY_ERROR | ParamCodingException.CIPHERTEXT_ERROR,
          "Ciphertext or private key error", e);
    }
  }

  /**
   * 将明文进行RSA+AES加密
   *
   * @param plaintext        明文
   * @param encodedPublicKey Base64编码的公钥
   * @return 密文
   * @throws ParamCodingException 公钥编码不正确
   */
  public static String mixEncrypt(String plaintext, String encodedPublicKey) throws ParamCodingException {
    try (ByteArrayInputStream in = new ByteArrayInputStream(plaintext.getBytes());
         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         DataOutputStream out = new DataOutputStream(bout)) {
      mixEncrypt(in, out, encodedPublicKey);
      return encode(bout.toByteArray());
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new IllegalStateException(e);
    }
  }

  /**
   * 对文件进行加密
   *
   * @param in               原始文件
   * @param out              加密后的文件
   * @param encodedPublicKey Base64编码的公钥
   * @throws IOException          输入输出异常
   * @throws ParamCodingException 公钥编码不正确
   */
  public static void mixEncrypt(File in, File out, String encodedPublicKey) throws IOException, ParamCodingException {
    try (FileInputStream fIn = new FileInputStream(in);
         FileOutputStream fOut = new FileOutputStream(out);
         DataOutputStream dOut = new DataOutputStream(fOut)) {
      mixEncrypt(fIn, dOut, encodedPublicKey);
    }
  }

  private static void mixEncrypt(InputStream in, DataOutputStream out, String encodedPublicKey) throws IOException, ParamCodingException {
    SecretKey aesKey = getAESKey();
    PublicKey publicKey = decodePublicKey(encodedPublicKey);
    try {
      // 用RSA加密AES的密钥
      Cipher cipher = Cipher.getInstance(RSA);
      cipher.init(Cipher.WRAP_MODE, publicKey);
      byte[] wrappedKey = cipher.wrap(aesKey);
      out.writeInt(wrappedKey.length);
      out.write(wrappedKey);
      // 再用AES加密实际的内容
      cipher = Cipher.getInstance(AES);
      cipher.init(Cipher.ENCRYPT_MODE, aesKey);
      // 加密
      transform(in, out, cipher);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new IllegalStateException(e);
    } catch (InvalidKeyException | IllegalBlockSizeException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      throw new ParamCodingException(ParamCodingException.KEY_ERROR, "RSA+AES Encrypt failed", e);
    }
  }

  /**
   * 将密文进行RSA+AES解密
   *
   * @param ciphertext        密文
   * @param encodedPrivateKey Base64编码的私钥
   * @return 明文
   * @throws ParamCodingException 密文格式不正确或私钥格式不正确
   */
  public static String mixDecrypt(String ciphertext, String encodedPrivateKey) throws ParamCodingException {
    byte[] data = decodeToBytes(ciphertext);
    try (ByteArrayInputStream bin = new ByteArrayInputStream(data);
         DataInputStream in = new DataInputStream(bin);
         ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      mixDecrypt(in, out, encodedPrivateKey);
      return out.toString();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new IllegalStateException(e);
    }
  }

  /**
   * 将文件进行解密
   *
   * @param in                加密后的文件
   * @param out               原始文件
   * @param encodedPrivateKey Base64编码的私钥
   * @throws IOException          输入输出异常
   * @throws ParamCodingException 私钥编码不正确
   */
  public static void mixDecrypt(File in, File out, String encodedPrivateKey) throws IOException, ParamCodingException {
    try (FileInputStream fIn = new FileInputStream(in);
         DataInputStream dIn = new DataInputStream(fIn);
         FileOutputStream fOut = new FileOutputStream(out)) {
      mixDecrypt(dIn, fOut, encodedPrivateKey);
    }
  }

  private static void mixDecrypt(DataInputStream in, OutputStream out, String encodedPrivateKey) throws IOException, ParamCodingException {
    PrivateKey privateKey = decodePrivateKey(encodedPrivateKey);
    try {
      // 先提取出加密的AES密钥
      int length = in.readInt();
      byte[] wrappedKey = new byte[length];
      int _length = in.read(wrappedKey, 0, length);
      assert _length == length;
      // 解密AES的密钥
      Cipher cipher = Cipher.getInstance(RSA);
      cipher.init(Cipher.UNWRAP_MODE, privateKey);
      Key key = cipher.unwrap(wrappedKey, AES, Cipher.SECRET_KEY);
      // 根据AES的密钥创建解码器
      cipher = Cipher.getInstance(AES);
      cipher.init(Cipher.DECRYPT_MODE, key);
      // 解密
      transform(in, out, cipher);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new IllegalStateException(e);
    } catch (InvalidKeyException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      throw new ParamCodingException(ParamCodingException.CIPHERTEXT_ERROR | ParamCodingException.KEY_ERROR, "RSA+AES Decrypt failed", e);
    }
  }

  private static void transform(InputStream in, OutputStream out, Cipher cipher) throws IOException, ParamCodingException {
    int blockSize = cipher.getBlockSize();
    int outputSize = cipher.getOutputSize(blockSize);
    byte[] inBytes = new byte[blockSize];
    byte[] outBytes = new byte[outputSize];

    try {
      int inLength = 0;
      boolean more = true;
      while (more) {
        inLength = in.read(inBytes);
        if (inLength == blockSize) {
          int outLength = cipher.update(inBytes, 0, blockSize, outBytes);
          out.write(outBytes, 0, outLength);
        } else {
          more = false;
        }
      }
      if (inLength > 0) {
        outBytes = cipher.doFinal(inBytes, 0, inLength);
      } else {
        outBytes = cipher.doFinal();
      }
      out.write(outBytes);
    } catch (ShortBufferException | IllegalBlockSizeException | BadPaddingException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      throw new ParamCodingException("An error occurred while encrypting or decrypting", e);
    }
  }

}
