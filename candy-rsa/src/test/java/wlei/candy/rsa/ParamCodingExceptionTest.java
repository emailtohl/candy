package wlei.candy.rsa;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParamCodingExceptionTest {

  @Test
  void invokerMessage() {
    ParamCodingException e = new ParamCodingException("test");
    String[] msg = e.invokerMessage();
    assertEquals(ParamCodingExceptionTest.class.getSimpleName(), msg[0]);
    assertTrue(Integer.parseInt(msg[1]) > 0);
  }

  @Test
  void testCode() {
    assertEquals(0b1100, (ParamCodingException.CIPHERTEXT_ERROR | ParamCodingException.PRIVATE_KEY_ERROR));
  }
}