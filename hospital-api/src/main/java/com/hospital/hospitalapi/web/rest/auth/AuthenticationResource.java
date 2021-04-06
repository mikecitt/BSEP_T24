package com.hospital.hospitalapi.web.rest.auth;

import javax.validation.Valid;

import com.hospital.hospitalapi.configuration.security.annotations.AuthenticatedUser;
import com.hospital.hospitalapi.service.auth.AuthenticationService;
import com.hospital.hospitalapi.util.ReturnResponse;
import com.hospital.hospitalapi.web.rest.auth.payload.request.AccountLoginRequestDTO;
import com.hospital.hospitalapi.web.rest.auth.payload.request.AccountRegisterRequest;
import com.hospital.hospitalapi.web.rest.auth.payload.request.ChangePasswordRequestDTO;
import com.hospital.hospitalapi.web.rest.auth.payload.request.ResetPasswordRequestDTO;
import com.hospital.hospitalapi.web.rest.auth.payload.response.AccountLoginResponseDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationResource {

    private final AuthenticationService service;

    @Autowired
    public AuthenticationResource(AuthenticationService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<AccountLoginResponseDTO> login(@RequestBody @Valid AccountLoginRequestDTO request) {
        return ReturnResponse.entityGet(service.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid AccountRegisterRequest request) {
        return ReturnResponse.entityCreated(service.register(request));
    }

    @PostMapping("/activate")
    public ResponseEntity<Void> activate(@RequestParam String hash) {
        return ReturnResponse.entityUpdated(service.activate(hash));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        return ReturnResponse.entityGet(service.forgotPassword(email));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestParam String hash,
            @RequestBody @Valid ResetPasswordRequestDTO request) {
        return ReturnResponse.entityUpdated(service.resetPassword(hash, request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@AuthenticatedUser User user,
            @RequestBody @Valid ChangePasswordRequestDTO request) {
        return ReturnResponse.entityUpdated(service.changePassword(user, request));
    }
}
