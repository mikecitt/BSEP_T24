package com.hospital.adminapi.web.rest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.hospital.adminapi.domain.Certificate;
import com.hospital.adminapi.service.certificate.CertificateService;
import com.hospital.adminapi.service.certificate.dto.CertificateResponseDTO;
import com.hospital.adminapi.util.constants.Constants.CERT_TYPE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cert")
public class CertificateResource {
    @Autowired
    private CertificateService service;

    @PostMapping("/create/{csrId}/{template}")
    public ResponseEntity<?> createCertificate(@PathVariable Long csrId, @PathVariable String template) {
        this.service.createCertificate(csrId, CERT_TYPE.valueOf(template));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getCertificates() {
        List<CertificateResponseDTO> response = service.getAll().stream().map(cert -> {
            CertificateResponseDTO dto = new CertificateResponseDTO(cert);
            System.out.println(dto);
            dto.setRevoked(service
                    .isRevoked(dto.getSerialNumber()) /* || dto.getEndDate().toInstant().isBefore(Instant.now()) */);
            return dto;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{serialNumber}/validity")
    public ResponseEntity<?> getCertificateValidity(@PathVariable Long serialNumber) {
        Optional<Certificate> certificate = service.findBySerialNumber(serialNumber);

        if (!certificate.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(
                !service.isRevoked(serialNumber) || certificate.get().getEndDate().toInstant().isBefore(Instant.now()),
                HttpStatus.OK);
    }

    @PostMapping("/{serialNumber}/revoke")
    public ResponseEntity<?> revokeCertificate(@PathVariable Long serialNumber) {
        try {
            service.revokeCertificate(serialNumber);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
