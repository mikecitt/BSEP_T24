package com.hospital.hospitalapi.web.rest.auth.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class ResetPasswordRequestDTO {

    @NotBlank
    @Size(min = 6, max = 64)
    private String password;
}
