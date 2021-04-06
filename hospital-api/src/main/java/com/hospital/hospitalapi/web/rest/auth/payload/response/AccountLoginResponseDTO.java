package com.hospital.hospitalapi.web.rest.auth.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountLoginResponseDTO {

    private String token;
    private AccountResponseDTO account;
}
