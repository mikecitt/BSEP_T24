package com.hospital.hospitalapi.web.rest.auth.payload.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class AccountLoginRequestDTO {

    @Email
    @Size(max = 128)
    @NotNull
    private String email;

    @NotBlank
    @Size(min = 6, max = 64)
    private String password;
}
