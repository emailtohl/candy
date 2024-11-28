package wlei.candy.rsa;

import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 加解密过程中的编码错误
 */
public class ParamCodingException extends GeneralSecurityException {
  /**
   * 错误码：未知
   */
  public static final int UNKNOWN = 0;
  /**
   * 错误码：密钥错误
   */
  public static final int KEY_ERROR = 0b1;
  /**
   * 错误码：公钥错误
   */
  public static final int PUBLIC_KEY_ERROR = 0b10;
  /**
   * 错误码：私钥错误
   */
  public static final int PRIVATE_KEY_ERROR = 0b100;
  /**
   * 错误码：密文错误
   */
  public static final int CIPHERTEXT_ERROR = 0b1000;
  /**
   * 错误码：签名错误
   */
  public static final int SIGNATURE_ERROR = 0b10000;
  private static final Logger LOGGER = Logger.getLogger(ParamCodingException.class.getName());
  /**
   * 错误码，0未知，0b100私钥错误，0b1000密文错误，0b1100私钥或密文错误
   */
  public final int code;
  /**
   * 失败的大致原因
   */
  public final String reason;

  public ParamCodingException(String msg) {
    this(UNKNOWN, msg);
  }

  public ParamCodingException(int code, String msg) {
    super(msg);
    this.code = code;
    String[] tuples = invokerMessage();
    this.reason = String.format("%s:%s %s", tuples[0], tuples[1], msg);
  }

  public ParamCodingException(String message, Throwable cause) {
    this(UNKNOWN, message, cause);
  }

  public ParamCodingException(int code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
    String[] tuples = invokerMessage();
    this.reason = String.format("%s:%s %s", tuples[0], tuples[1], message);
  }

  /**
   * 返回调用该日志打印的类信息以及打印行信息
   *
   * @return 返回字符串数组，该数组为一个2元组，第一个元素是调用的类名，第二个元素是调用的具体行
   */
  String[] invokerMessage() {
    String[] msg = new String[2];
    StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
    try {
      String thisName = ParamCodingException.class.getName();
      int i = 1;
      for (; i < stacks.length; i++) {
        if (!thisName.equals(stacks[i].getClassName())) {
          break;
        }
      }
      String className = stacks[i].getClassName();
      int index = className.lastIndexOf(".");
      className = className.substring(index + 1);
      msg[0] = className;
      msg[1] = String.valueOf(stacks[i].getLineNumber());
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    }
    return msg;
  }
}
