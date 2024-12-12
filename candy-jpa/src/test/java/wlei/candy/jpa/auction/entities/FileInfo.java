package wlei.candy.jpa.auction.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import org.apache.commons.lang3.StringUtils;
import wlei.candy.jpa.GenericEntity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Author: HeLei
 * Date: 2024/12/12
 */
@Entity
public class FileInfo extends GenericEntity<String, FileInfo> {
  private String id;

  private String name;

  private String encodeData;

  private String signature;

  public FileInfo withData(byte[] data) {
    Base64.Encoder encoder = Base64.getEncoder();
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] hash = md.digest(data);
      setSignature(encoder.encodeToString(hash));
      setEncodeData(encoder.encodeToString(data));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
    return this;
  }

  public byte[] fetchData() {
    String s = getEncodeData();
    if (StringUtils.isBlank(s)) {
      return new byte[0];
    }
    Base64.Decoder decoder = Base64.getDecoder();
    return decoder.decode(s);
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public FileInfo setId(String id) {
    this.id = id;
    return this;
  }

  // 文件名
  @Column(nullable = false, updatable = false)
  public String getName() {
    return name;
  }

  public FileInfo setName(String name) {
    this.name = name;
    return this;
  }

  // 不用@Lob，否则是以对象方式存储到数据库，使得不容易迁移
  // @Lob
  @Column(columnDefinition = "TEXT")
  @Basic(optional = false, fetch = FetchType.LAZY)
  public String getEncodeData() {
    return encodeData;
  }

  public FileInfo setEncodeData(String encodeData) {
    this.encodeData = encodeData;
    return this;
  }

  @Column(nullable = false, updatable = false)
  public String getSignature() {
    return signature;
  }

  public FileInfo setSignature(String signature) {
    this.signature = signature;
    return this;
  }
}
