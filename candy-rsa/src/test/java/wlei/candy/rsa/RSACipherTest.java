package wlei.candy.rsa;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RSACipherTest {

  @Test
  void test() throws ParamCodingException {
    String[] keys = RSACipher.getKeys(1024);
    String c = RSACipher.RSAEncrypt(PlainText.PrefaceToTengwangPavillion, keys[0]);
    String m = RSACipher.RSADecrypt(c, keys[1]);
    assertEquals(PlainText.PrefaceToTengwangPavillion, m);

    final String msg = "msg";
    ParamCodingException e = new ParamCodingException(123, msg, new ParamCodingException(msg));
    assertEquals(msg, e.getMessage());
  }
}