package com.hospital.hospitalapi.service.certificate;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.hospital.hospitalapi.configuration.ApplicationProperties;
import com.hospital.hospitalapi.configuration.constants.Constants;
import com.hospital.hospitalapi.configuration.constants.Constants.CERT_TYPE;
import com.hospital.hospitalapi.domain.cert.Certificate;
import com.hospital.hospitalapi.domain.cert.CertificateSigningRequest;
import com.hospital.hospitalapi.domain.cert.IssuerData;
import com.hospital.hospitalapi.domain.cert.SubjectData;
import com.hospital.hospitalapi.repository.CertificateRepository;
import com.hospital.hospitalapi.service.csr.CertificateSigningRequestService;
import com.hospital.hospitalapi.service.keystore.KeyStoreService;

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
  private KeyStoreService keyStoreService;

  @Autowired
  private CertificateSigningRequestService certificateSigningRequestService;

  @Autowired
  private CertificateRepository certificateRepository;

  @Autowired
  private ApplicationProperties applicationProperties;

  public List<Certificate> getAll() {
    return certificateRepository.findAll();
  }

  public Certificate createCertificate(Long csrId, CERT_TYPE templateType) {
    CertificateSigningRequest csr = certificateSigningRequestService.findById(csrId);

    PrivateKey issuerKey = readPrivateKey(applicationProperties.platform.getKeystorePath(),
        applicationProperties.platform.getKeystorePass(), Constants.ROOT_ALIAS);
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

    String keyStorePath = "pki/keystore/keyStore_" + csr.getId() + ".jks";
    char[] keyStorePass = csr.getId().toString().toCharArray();

    X509Certificate certificate = generateCertificate(subjectData, issuerData, Constants.CERT_TYPE.LEAF_CERT);
    Certificate myCertificate = new Certificate(csr.getId().toString(),
        new java.sql.Timestamp(subjectData.getStartDate().getTime()),
        new java.sql.Timestamp(subjectData.getEndDate().getTime()));
    myCertificate.setCertKeyStorePath(keyStorePath);

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
