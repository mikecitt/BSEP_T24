package com.hospital.adminapi.web.rest;

import java.io.IOException;

import com.hospital.adminapi.domain.CertificateSigningRequest;
import com.hospital.adminapi.service.csr.CertificateSigningRequestService;
import com.hospital.adminapi.service.csr.dto.CertificateSigningRequestDTO;

import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/csr")
public class CeritificateSigningRequestResource {

    @Autowired
    private CertificateSigningRequestService service;

    @PostMapping
    public ResponseEntity<?> addRequest(@RequestBody CertificateSigningRequestDTO csrDTO) throws OperatorCreationException, IOException {
        service.sendRequest(csrDTO);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllRequests() {
        return new ResponseEntity<>(service.getAll().stream().map(csr -> new CertificateSigningRequestDTO(csr)), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRequest(@PathVariable Long id) {
        CertificateSigningRequest csr = service.get(id);
        if(csr == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(new CertificateSigningRequestDTO(csr), HttpStatus.OK);
    }

}
