package wlei.candy.rsa;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * 存储RSA密钥对最关键的信息：n，e，d
 */
class Keys implements Serializable {
  final BigInteger n, e, d;

  Keys(BigInteger n, BigInteger e, BigInteger d) {
    this.n = n;
    this.e = e;
    this.d = d;
  }

  @Override
  public String toString() {
    return String.format("{\"n\":\"%s\",\"e\":\"%s\",\"d\":\"%s\"}", n.toString(), e.toString(), d.toString());
  }
}
