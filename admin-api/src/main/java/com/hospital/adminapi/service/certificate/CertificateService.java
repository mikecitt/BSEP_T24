package com.hospital.adminapi.service.certificate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.hospital.adminapi.configuration.ApplicationProperties;
import com.hospital.adminapi.domain.Certificate;
import com.hospital.adminapi.domain.CertificateSigningRequest;
import com.hospital.adminapi.domain.IssuerData;
import com.hospital.adminapi.domain.RevokedCertificate;
import com.hospital.adminapi.domain.SubjectData;
import com.hospital.adminapi.repository.CertificateRepository;
import com.hospital.adminapi.repository.CertificateRevokeRepository;
import com.hospital.adminapi.service.csr.CertificateSigningRequestService;
import com.hospital.adminapi.util.constants.Constants;
import com.hospital.adminapi.util.constants.Constants.CERT_TYPE;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateService {

  @Autowired
  private CertificateRepository certificateRepository;

  @Autowired
  private CertificateRevokeRepository certificateRevokeRepository;

  @Autowired
  private CertificateSigningRequestService certificateSigningRequestService;

  @Autowired
  public ApplicationProperties applicationProperties;

  public List<Certificate> getAll() {
    return certificateRepository.findAll();
  }

  public boolean isRevoked(Long serialNumber) {
    return certificateRevokeRepository.findBySerialNumber(new BigInteger(serialNumber.toString())).isPresent();
  }

  public void revokeCertificate(Long serialNumber) throws Exception {
    Optional<Certificate> cert = certificateRepository
        .findBySerialNumber(new BigInteger(serialNumber.toString()));
    if (cert.isEmpty()) {
      throw new Exception("Certificate with this serial number does not exist in this context");
    }

    certificateRevokeRepository
        .save(new RevokedCertificate(cert.get().getSerialNumber(), new Timestamp(new Date().getTime())));
  }

  public Certificate createCertificate(Long csrId, CERT_TYPE templateType) {
    CertificateSigningRequest csr = certificateSigningRequestService.get(csrId);

    PrivateKey issuerKey = readPrivateKey(applicationProperties.getKeyStore().getFilepath(),
        applicationProperties.getKeyStore().getPassword(), Constants.ROOT_ALIAS);
    X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
    builder.addRDN(BCStyle.CN, "ROOT");
    builder.addRDN(BCStyle.O, "MZ-Srbija");
    builder.addRDN(BCStyle.OU, "Klinicki centar");
    builder.addRDN(BCStyle.L, "Novi Sad");
    builder.addRDN(BCStyle.C, "RS");

    IssuerData issuerData = new IssuerData(issuerKey, builder.build());
    X500NameBuilder subjectName = generateName(csr);
    SubjectData subjectData = generateSubjectData(certificateSigningRequestService.getPublicKeyFromCSR(csrId),
        subjectName.build(), Constants.CERT_TYPE.LEAF_CERT, String.valueOf(csr.getId()));

    String keyStorePath = applicationProperties.getKeyStore().getFilepath();
    char[] keyStorePass = applicationProperties.getKeyStore().getPassword().toCharArray();

    X509Certificate certificate = generateCertificate(subjectData, issuerData, Constants.CERT_TYPE.LEAF_CERT);
    Certificate myCertificate = new Certificate(new BigInteger(csr.getId().toString()),
        new java.sql.Timestamp(subjectData.getStartDate().getTime()),
        new java.sql.Timestamp(subjectData.getEndDate().getTime()));
    myCertificate.setAlias(csr.getId().toString());
    myCertificate.setCommonName(csr.getCommonName());
    // myCertificate.setCertKeyStorePath(keyStorePath);

    try {
      KeyStore keyStore = KeyStore.getInstance("JKS", "SUN");
      File f = new File(keyStorePath);
      if (f.exists()) {
        keyStore.load(new FileInputStream(f), keyStorePass);
      } else {
        keyStore.load(null, keyStorePass);

        certificateRepository.save(myCertificate);

        keyStore.setKeyEntry(Constants.ROOT_ALIAS, issuerKey, keyStorePass,
            new java.security.cert.Certificate[] { certificate });

        keyStore.store(new FileOutputStream(keyStorePath), keyStorePass);

        return myCertificate;
      }
    } catch (IOException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException
        | KeyStoreException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public PrivateKey readPrivateKey(String keyStoreFile, String keyStorePass, String alias) {
    try {
      KeyStore ks = KeyStore.getInstance("JKS", "SUN");
      BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
      ks.load(in, keyStorePass.toCharArray());

      if (ks.isKeyEntry(alias)) {
        PrivateKey pk = (PrivateKey) ks.getKey(alias, keyStorePass.toCharArray());
        return pk;
      }
    } catch (KeyStoreException | NoSuchAlgorithmException | NoSuchProviderException | CertificateException | IOException
        | UnrecoverableKeyException e) {
      e.printStackTrace();
    }
    return null;
  }

  private X500NameBuilder generateName(CertificateSigningRequest csr) {
    X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
    // builder.addRDN(BCStyle.NAME, csr.getIssuerName());
    // builder.addRDN(BCStyle.SERIALNUMBER, csr.getSerialNumber());
    builder.addRDN(BCStyle.UNIQUE_IDENTIFIER, csr.getUsername());
    return builder;
  }

  public X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData, CERT_TYPE templateTypes) {
    try {
      JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
      builder = builder.setProvider("BC");

      ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());

      X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
          new BigInteger(subjectData.getSerialNumber()), subjectData.getStartDate(), subjectData.getEndDate(),
          subjectData.getX500name(), subjectData.getPublicKey());

      if (templateTypes == Constants.CERT_TYPE.ROOT_CERT) {
        certGen.addExtension(Extension.keyUsage, false,
            new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign));

        certGen.addExtension(Extension.basicConstraints, false, new BasicConstraints(true));

        byte[] subjectKeyIdentifier = new JcaX509ExtensionUtils().createSubjectKeyIdentifier(subjectData.getPublicKey())
            .getKeyIdentifier();

        certGen.addExtension(Extension.subjectKeyIdentifier, false, new SubjectKeyIdentifier(subjectKeyIdentifier));
      } else {
        certGen.addExtension(Extension.keyUsage, false,
            new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
        certGen.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));
      }

      X509CertificateHolder certHolder = certGen.build(contentSigner);
      JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
      certConverter = certConverter.setProvider("BC");

      return certConverter.getCertificate(certHolder);
    } catch (IllegalArgumentException | IllegalStateException | OperatorCreationException | CertificateException
        | NoSuchAlgorithmException | CertIOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public SubjectData generateSubjectData(PublicKey publicKey, X500Name name, Constants.CERT_TYPE certType,
      String serialNum) {
    Date endDate;

    Calendar calendarLater = Calendar.getInstance();
    calendarLater.setTime(new Date());

    if (certType == Constants.CERT_TYPE.ROOT_CERT) {
      calendarLater.add(Calendar.DAY_OF_MONTH, Constants.ROOT_CERT_DURATION);
    } else {
      calendarLater.add(Calendar.MONTH, Constants.LEAF_CERT_DURATION);
    }
    endDate = calendarLater.getTime();

    return new SubjectData(publicKey, name, serialNum, new Date(), endDate);
  }
}
