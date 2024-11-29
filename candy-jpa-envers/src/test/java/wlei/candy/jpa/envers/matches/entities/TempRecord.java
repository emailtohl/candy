package wlei.candy.jpa.envers.matches.matches.entities;

import jakarta.persistence.Column;

/**
 * Author: HeLei
 * Date: 2024/11/28
 */
public class TempRecord extends BeanEntity<TempRecord> {
  private int recordType;

  @Column(nullable = false)
  public int getRecordType() {
    return recordType;
  }

  public TempRecord setRecordType(int recordType) {
    this.recordType = recordType;
    return this;
  }
}
