package com.hospital.adminapi.service.csr;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.security.auth.x500.X500Principal;

import com.hospital.adminapi.domain.CertificateSigningRequest;
import com.hospital.adminapi.repository.CertificateSigningRequestRepository;
import com.hospital.adminapi.service.csr.dto.CertificateSigningRequestDTO;

import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateSigningRequestService {

  @Autowired
  private CertificateSigningRequestRepository repository;

  public List<CertificateSigningRequest> getAll() {
    return repository.findAll();
  }

  public CertificateSigningRequest get(Long id) {
    return repository.findById(id).orElseGet(null);
  }

  public void sendRequest(CertificateSigningRequestDTO csrDTO) throws OperatorCreationException, IOException {
    KeyPair pair = generateKeyPair();

    HashMap<String, String> subjectData = new HashMap<>();

    subjectData.put("CN", csrDTO.getCommonName());
    subjectData.put("O", csrDTO.getOrganization());
    subjectData.put("OU", csrDTO.getOrganizationUnit());
    subjectData.put("C", csrDTO.getCountry());
    subjectData.put("EMAIL", csrDTO.getEmail());
    subjectData.put("L", csrDTO.getLocality());

    String mapAsString = subjectData.keySet().stream()
        .filter(key -> subjectData.get(key) != null && !subjectData.get(key).isEmpty())
        .map(key -> key + "=" + subjectData.get(key)).collect(Collectors.joining(","));

    PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(
        new X500Principal(mapAsString), pair.getPublic());
    JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder("SHA256withRSA");
    ContentSigner signer = csBuilder.build(pair.getPrivate());
    PKCS10CertificationRequest csr = p10Builder.build(signer);

    System.out.println("Private key: " + Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded()));

    CertificateSigningRequest csrDb = new CertificateSigningRequest();
    csrDb.setCommonName(csrDTO.getCommonName());
    csrDb.setCountry(csrDTO.getCountry());
    csrDb.setEmail(csrDTO.getEmail());
    csrDb.setLocality(csrDTO.getLocality());
    csrDb.setOrganization(csrDTO.getOrganization());
    csrDb.setOrganizationUnit(csrDTO.getOrganizationUnit());
    csrDb.setCertificate(csr.getEncoded());
    csrDb.setUsername("user123");

    repository.save(csrDb);
  }

  private KeyPair generateKeyPair() {
    try {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
      keyGen.initialize(2048, random);
      return keyGen.generateKeyPair();
    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
      e.printStackTrace();
    }
    return null;
  }

  public PublicKey getPublicKeyFromCSR(Long id) {
    try {
      PKCS10CertificationRequest csr = new PKCS10CertificationRequest(get(id).getCertificate());

      JcaPKCS10CertificationRequest jcaCertRequest = new JcaPKCS10CertificationRequest(csr.getEncoded())
          .setProvider("BC");
      return jcaCertRequest.getPublicKey();
    } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
      e.printStackTrace();
    }

    return null;
  }
}
