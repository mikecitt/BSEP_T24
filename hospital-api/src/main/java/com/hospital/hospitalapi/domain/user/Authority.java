package com.hospital.hospitalapi.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
@Table(name = "authorities")
public class Authority {

    @Id
    @NotBlank
    @Size(max = 50)
    @Column(unique = true, nullable = false)
    private String name;
}
