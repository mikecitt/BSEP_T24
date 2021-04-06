package com.hospital.hospitalapi.web.rest.csr;

import java.io.IOException;
import java.util.Set;

import com.hospital.hospitalapi.service.csr.CertificateSigningRequestService;
import com.hospital.hospitalapi.util.ReturnResponse;
import com.hospital.hospitalapi.web.rest.csr.payload.response.CertificateSigningRequestDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/csr")
public class CertificateSigningRequestResource {

    @Autowired
    private CertificateSigningRequestService csrService;

    @GetMapping
    public ResponseEntity<Set<CertificateSigningRequestDTO>> getAllSigningRequests() {
        return ReturnResponse.entityGet(csrService.getAllSigningRequests());
    }

    @PostMapping
    public ResponseEntity<?> sendSigningRequest(@RequestBody byte[] request) throws IOException {

        csrService.saveRequest(request, "testUser");

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
