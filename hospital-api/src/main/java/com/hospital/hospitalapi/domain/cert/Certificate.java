package com.hospital.hospitalapi.domain.cert;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Certificate {
    @Id
    private String serialNumber;

    @Column
    private Timestamp startDate;

    @Column
    private Timestamp endDate;

    @Column
    private String certKeyStorePath;

    public Certificate() {
    }

    public Certificate(String serialNumber, Timestamp startDate, Timestamp endDate) {
        this.serialNumber = serialNumber;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Certificate(String serialNumber, Timestamp startDate, Timestamp endDate, String certKeyStorePath) {
        this.serialNumber = serialNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.certKeyStorePath = certKeyStorePath;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Timestamp getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public Certificate serialNumber(String serialNumber) {
        setSerialNumber(serialNumber);
        return this;
    }

    public Certificate startDate(Timestamp startDate) {
        setStartDate(startDate);
        return this;
    }

    public Certificate endDate(Timestamp endDate) {
        setEndDate(endDate);
        return this;
    }

    public String getCertKeyStorePath() {
        return this.certKeyStorePath;
    }

    public void setCertKeyStorePath(String certKeyStorePath) {
        this.certKeyStorePath = certKeyStorePath;
    }
}
