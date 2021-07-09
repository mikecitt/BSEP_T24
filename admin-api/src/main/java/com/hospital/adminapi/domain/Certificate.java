package com.hospital.adminapi.domain;

import javax.persistence.*;

import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
public class Certificate {

  @Id
  private Long serialNumber;

  private Timestamp startDate;
  private Timestamp endDate;
  private String commonName;
  private String alias;

  public Certificate() {

  }

  public Certificate(Long serialNumber) {
    this.serialNumber = serialNumber;
  }

  public Certificate(Long serialNumber, Timestamp startDate, Timestamp endDate) {
    this.serialNumber = serialNumber;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public Certificate(Long serialNumber, Timestamp startDate, Timestamp endDate, String commonName, String alias) {
    this.serialNumber = serialNumber;
    this.startDate = startDate;
    this.endDate = endDate;
    this.commonName = commonName;
    this.alias = alias;
  }

}