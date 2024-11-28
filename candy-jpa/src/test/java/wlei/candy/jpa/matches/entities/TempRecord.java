package wlei.candy.jpa.matches.entities;

import jakarta.persistence.Column;
import wlei.candy.jpa.BeanEntity;

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
