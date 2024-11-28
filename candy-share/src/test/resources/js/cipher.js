/**
 * 实现与Java端的加解密通信的工具
 * 
 * 依赖crypto-js.js和jsencrypt.js
 */

/**
 * 转码工具
 * 
 * 依赖crypto-js.js和jsencrypt.js
 * 
 * Original author: HeLei
 */
var charEncoding;
(function () {
  /**
   * 将字符串编码成数字序列，以便于生成BigInteger
   * 数字序列是每个字符的Unicode码的组合，在每个Unicode码之前插入该Unicode码的长度
   * 例如字符串"你好",'你'是20320，有5位，'好'是22909，有5位，那么编码成"520320522909"
   *
   * @param {String} text 字符串
   */
  function stringToNumberSequence(text) {
    var arr = [];
    for (var i = 0; i < text.length; i++) {
      // 字符转成Unicode码
      var unicode = new String(text.codePointAt(i));
      // 第一个数字作为该Unicode码的长度
      arr.push(unicode.length);
      arr.push(unicode);
    }
    return arr.join('');
  }

  /**
   * 将stringToNumberSequence转成的编码进行反编码
   * @param {String} numberSequence 数字序列
   */
  function numberSequenceToString(numberSequence) {
    var arr = [];
    var i = 0;
    var j = i + 1;
    while (j < numberSequence.length) {
      var size = parseInt(numberSequence.substring(i, j));
      i += 1 + size;
      var num = numberSequence.substring(j, i);
      num = String.fromCharCode(num);
      arr.push(num);
      j = i + 1;
    }
    return arr.join('');
  }

  /**
   * 解码base64编码的字符串
   * @param {String} base64Str base64编码的字符串
   */
  function base64Decode(base64Str) {
    var arr = CryptoJS.enc.Base64.parse(base64Str);
    return arr.toString(CryptoJS.enc.Utf8);
  }

  /**
   * 将字符串编码成base64格式
   * @param {String} str 字符串
   */
  function base64Encode(str) {
    var arr = CryptoJS.enc.Utf8.parse(str);
    return CryptoJS.enc.Base64.stringify(arr);
  }

  charEncoding = {
    stringToNumberSequence: stringToNumberSequence,
    numberSequenceToString: numberSequenceToString,
    base64Decode: base64Decode,
    base64Encode: base64Encode
  };
}());

/**
 * 前端的RSA加密程序，与Java端的JDKCipher结合使用
 * 此程序使用JdkCipher.getEncodedRSAKeyPair返回中的公钥，并按JdkCipher.RSAEncrypt的算法加密明文
 * 如此java端的JdkCipher.RSADecrypt就可以解密密文了
 * 
 * Java后端的RSA的公钥使用X509编码，私钥使用PKCS8编码，故依赖jsencrypt.js库以及charEncoding
 * 
 * Original author: HeLei
 */
var JDKCipher;
(function () {
  var delimiter = ',';
  /**
   * 将明文转码成大整数，然后表示成余、除数以及商三个部分，这样可以控制余和除数在较小范围内，以便于RSA加解密
   * @param {String} plaintext 明文
   * @return 大整数数组分别为余、除数以及商
   */
  function resolve(plaintext) {
    var num = charEncoding.stringToNumberSequence(plaintext);
    if (charEncoding.numberSequenceToString(num) != plaintext) {
      throw new Error('stringToNumberSequence fail');
    }
    var p = new BigInteger(num);
    // JDK的RSA密码最低位数是512，加密数据位数不超过53个字节
    var divisor = new BigInteger(53, new SecureRandom());
    var divideAndRemainder = p.divideAndRemainder(divisor);
    var divide = divideAndRemainder[0], remainder = divideAndRemainder[1];
    return [remainder, divisor, divide];
  }

  /**
   * 按JdkCipher.RSAEncrypt的算法加密明文，这样Java端的JdkCipher.RSADecrypt就可以解密此密文了
   * @param {String} plaintext 明文
   * @param {String} encodedPublicKey X509编码的公钥
   */
  function RSAEncrypt(plaintext, encodedPublicKey) {
    var crypt = new JSEncrypt();
    crypt.setPublicKey(encodedPublicKey);
    // 将明文转成数字后，由三个元素计算而成：余数、除数以及商
    const tuples = resolve(plaintext);
    var remainder = tuples[0], divisor = tuples[1], divide = tuples[2];
    var encryptedRemainder = crypt.encrypt(remainder.toString());
    var encryptedDivisor = crypt.encrypt(divisor.toString());
    var divideArray = CryptoJS.enc.Utf8.parse(divide.toString());
    var divideBase64 = CryptoJS.enc.Base64.stringify(divideArray);
    return [encryptedRemainder, encryptedDivisor, divideBase64].join(delimiter);
  }

  /**
   * 解密密文
   * 
   * @param {String} ciphertext 密文
   * @param {String} encodedPrivateKey PKCS8编码的私钥
   */
  function RSADecrypt(ciphertext, encodedPrivateKey) {
    var crypt = new JSEncrypt();
    crypt.setPrivateKey(encodedPrivateKey);
    var tuples = ciphertext.split(delimiter);
    var remainder = crypt.decrypt(tuples[0]);
    var divisor = crypt.decrypt(tuples[1]);
    var divide = CryptoJS.enc.Base64.parse(tuples[2]);
    divide = divide.toString(CryptoJS.enc.Utf8);
    // 加密时使用的数字的字符串，所以解密后，需先将字节数组转成字符串
    remainder = new BigInteger(remainder);
    divisor = new BigInteger(divisor);
    divide = new BigInteger(divide);
    // 组装回原来的大数
    var p = remainder.add(divisor.multiply(divide));
    return charEncoding.numberSequenceToString(p.toString());
  }

  /**
   * SHA256withRSA签名
   * @param {String} plaintext 明文
   * @param {String} encodedPrivateKey PKCS8编码的私钥
   */
  function signSHA256withRSA(plaintext, encodedPrivateKey) {
    var sign = new JSEncrypt();
    sign.setPrivateKey(encodedPrivateKey);
    return sign.sign(plaintext, CryptoJS.SHA256, 'sha256');
  }

  /**
   * SHA256withRSA验签
   * @param {String} plaintext 明文
   * @param {String} signature 签名码
   * @param {String} encodedPublicKey X509编码的公钥
   */
  function verifySHA256withRSA(plaintext, signature, encodedPublicKey) {
    var verify = new JSEncrypt();
    verify.setPublicKey(encodedPublicKey);
    return verify.verify(plaintext, signature, CryptoJS.SHA256);
  }

  JDKCipher = {
    RSAEncrypt: RSAEncrypt,
    RSADecrypt: RSADecrypt,
    signSHA256withRSA: signSHA256withRSA,
    verifySHA256withRSA: verifySHA256withRSA
  };
}());


/**
 * 前端的RSA加密程序，与Java端的RSACipher结合使用
 * 此程序使用RSACipher.getKeys返回中的公钥，并按RSACipher.RSAEncrypt的算法加密明文
 * 如此java端的RSACipher.RSADecrypt就可以解密密文了
 * 
 * 依赖jsencrypt.js库，此外Base64编码依赖cryptoJS库以及charEncoding
 * 
 * Original author: HeLei
 */
var RSACipher;
(function () {
  var delimiter = ',';

  /**
   * 将明文转码成大整数，然后表示成余、除数以及商三个部分，这样可以控制余和除数在较小范围内，以便于RSA加解密
   * @param {String} plaintext 明文
   * @param {BigInteger} n 密钥的模
   * @return 大整数数组分别为余、除数以及商
   */
  function resolve(plaintext, n) {
    var num = charEncoding.stringToNumberSequence(plaintext);
    if (charEncoding.numberSequenceToString(num) != plaintext) {
      throw new Error('stringToNumberSequence fail');
    }
    var p = new BigInteger(num);
    // 根据n确定除数，只需将除数和余控制在n的范围内即可
    var divisor = new BigInteger(n.bitLength() - 1, new SecureRandom());
    var divideAndRemainder = p.divideAndRemainder(divisor);
    var divide = divideAndRemainder[0], remainder = divideAndRemainder[1];
    return [remainder, divisor, divide];
  }

  /**
   * 使用RSA算法加密明文
   * @param {String} plaintext 明文
   * @param {String} encodedPublicKey 编码后的公钥
   */
  function RSAEncrypt(plaintext, encodedPublicKey) {
    var ne = encodedPublicKey.split(delimiter);
    var n = new BigInteger(charEncoding.base64Decode(ne[0]));
    var e = new BigInteger(charEncoding.base64Decode(ne[1]));
    // 将明文转成数字后，由三个元素计算而成：余数、除数以及商
    var tuples = resolve(plaintext, n);
    var remainder = tuples[0], divisor = tuples[1], divide = tuples[2];
    // 加密
    var encryptedRemainder = remainder.modPow(e, n);
    var encryptedDivisor = divisor.modPow(e, n);
    return [charEncoding.base64Encode(encryptedRemainder.toString()), charEncoding.base64Encode(encryptedDivisor.toString()), charEncoding.base64Encode(divide.toString())];
  }

  /**
   * 解密密文
   * @param {String} ciphertext 被RSAEncrypt或它使用的同样算法加密的密文
   * @param {String} encodedPrivateKey 编码后的私钥
   */
  function RSADecrypt(ciphertext, encodedPrivateKey) {
    var nd = encodedPrivateKey.split(delimiter);
    var n = new BigInteger(charEncoding.base64Decode(nd[0]));
    var d = new BigInteger(charEncoding.base64Decode(nd[1]));
    var tuples = ciphertext.split(delimiter);
    var encryptedRemainder = new BigInteger(charEncoding.base64Decode(tuples[0]));
    var encryptedDivisor = new BigInteger(charEncoding.base64Decode(tuples[1]));
    var remainder = encryptedRemainder.modPow(d, n);
    var divisor = encryptedDivisor.modPow(d, n);
    // 加密时使用的数字的字符串，所以解密后，需先将字节数组转成字符串
    var divide = new BigInteger(charEncoding.base64Decode(tuples[2]));
    // 组装回原来的大数
    var p = remainder.add(divisor.multiply(divide));
    return charEncoding.numberSequenceToString(p.toString());
  }

  RSACipher = {
    RSAEncrypt: RSAEncrypt,
    RSADecrypt: RSADecrypt
  };
}());
