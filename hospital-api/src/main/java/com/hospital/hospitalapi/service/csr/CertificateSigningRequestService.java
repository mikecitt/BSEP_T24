package com.hospital.hospitalapi.service.csr;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.hospital.hospitalapi.domain.cert.CertificateSigningRequest;
import com.hospital.hospitalapi.repository.CertificateSigningRequestRepository;
import com.hospital.hospitalapi.web.rest.csr.payload.response.CertificateSigningRequestDTO;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateSigningRequestService {

    @Autowired
    private CertificateSigningRequestRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    public Set<CertificateSigningRequestDTO> getAllSigningRequests() {
        return repository.findAll().stream()
                .map((request) -> modelMapper.map(request, CertificateSigningRequestDTO.class))
                .collect(Collectors.toSet());
    }

    public void saveRequest(byte[] request, String username) throws IOException {
        PEMParser pm = new PEMParser(new StringReader(new String(request)));
        PKCS10CertificationRequest certReq = (PKCS10CertificationRequest) pm.readObject();
        Map<String, String> attributes = this.parseCsrAttributes(certReq);

        repository.save(new CertificateSigningRequest(new String(request), attributes.get("issuerId"), username));
    }

    private Map<String, String> parseCsrAttributes(PKCS10CertificationRequest csr) {
        Map<String, String> result = new HashMap<>();

        String attrVal = null;
        // @TODO: BOLJE CITATI KLJUCEVE od GenralNames
        String[] attNames = { "issuerId", "certSerialNumber" };

        Attribute[] attributes = csr.getAttributes(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest);
        for (Attribute attribute : attributes) {
            int i = 0;
            for (ASN1Encodable values : attribute.getAttributeValues()) {
                for (ASN1Encodable value : (DERSequence) values) {
                    DEROctetString oo = (DEROctetString) ((DERTaggedObject) value).getObject();
                    attrVal = new String(oo.getOctets());
                    result.put(attNames[i], attrVal);
                    i++;
                }
            }
        }
        return result;
    }
}
