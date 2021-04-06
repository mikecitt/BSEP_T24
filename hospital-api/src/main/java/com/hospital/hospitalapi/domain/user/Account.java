package com.hospital.hospitalapi.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.hospital.hospitalapi.domain.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {

    @NotNull
    @Size(min = 3, max = 50)
    private String firstName;

    @NotNull
    @Size(min = 3, max = 50)
    private String lastName;

    @Email
    @Size(max = 128)
    @Column(unique = true)
    private String email;

    @NotNull
    @Size(min = 60, max = 60)
    private String encryptedPassword;

    @Column(nullable = false)
    private short yearOfBirth;

    @NotNull
    private Boolean active;

    @NotNull
    @Size(min = 36, max = 36)
    @Column(unique = true)
    private String hash;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "authority_id")
    private Authority authority;
}
