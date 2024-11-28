package wlei.candy.rsa;

import org.junit.jupiter.api.Test;

import static wlei.candy.rsa.PlainText.PrefaceToTengwangPavillion;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CharEncodingTest {

  @Test
  void stringToNumberSequence() throws ParamCodingException {
    String s = CharEncoding.stringToNumberSequence(PrefaceToTengwangPavillion);
    s = CharEncoding.numberSequenceToString(s);
    assertEquals(PrefaceToTengwangPavillion, s);
  }
}