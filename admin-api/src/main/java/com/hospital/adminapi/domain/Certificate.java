package com.hospital.adminapi.domain;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
public class Certificate {

  @Id
  private BigInteger serialNumber;

  @Column
  private Timestamp startDate;

  @Column
  private Timestamp endDate;

  @Column
  private String commonName;

  @Column
  private String alias;

  public Certificate() {

  }

  public Certificate(BigInteger serialNumber) {
    this.serialNumber = serialNumber;
  }

  public Certificate(BigInteger serialNumber, Timestamp startDate, Timestamp endDate, String commonName, String alias) {
    this.serialNumber = serialNumber;
    this.startDate = startDate;
    this.endDate = endDate;
    this.commonName = commonName;
    this.alias = alias;
  }

  public BigInteger getSerialNumber() {
    return serialNumber;
  }

  public Timestamp getStartDate() {
    return startDate;
  }

  public void setStartDate(Timestamp startDate) {
    this.startDate = startDate;
  }

  public Timestamp getEndDate() {
    return endDate;
  }

  public void setEndDate(Timestamp endDate) {
    this.endDate = endDate;
  }

  public String getCommonName() {
    return commonName;
  }

  public void setCommonName(String commonName) {
    this.commonName = commonName;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }
}
