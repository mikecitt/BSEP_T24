package com.hospital.hospitalapi.configuration.cert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;

import com.hospital.hospitalapi.configuration.ApplicationProperties;
import com.hospital.hospitalapi.configuration.constants.Constants;
import com.hospital.hospitalapi.domain.cert.Certificate;
import com.hospital.hospitalapi.domain.cert.IssuerData;
import com.hospital.hospitalapi.domain.cert.SubjectData;
import com.hospital.hospitalapi.service.keystore.KeyStoreService;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyKeyStore {

    @Autowired
    private KeyStoreService keyStoreService;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Bean(name = "setupKeyStore")
    public KeyStore setupKeyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS", "SUN");
            File file = new File(applicationProperties.getPlatform().getKeystorePath());
            if (file.exists())
                keyStore.load(new FileInputStream(file),
                        applicationProperties.getPlatform().getKeystorePass().toCharArray());
            else
                keyStore.load(null, applicationProperties.getPlatform().getKeystorePass().toCharArray());

            Certificate root_cert = generateRoot(keyStore);

            keyStoreService.writeCertificateToFile(keyStore, Constants.ROOT_ALIAS,
                    applicationProperties.getPlatform().getCertDir());

            keyStoreService.save(root_cert);

            keyStore.store(new FileOutputStream(applicationProperties.getPlatform().getKeystorePath()),
                    applicationProperties.getPlatform().getKeystorePass().toCharArray());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private X500NameBuilder generateName(String CN) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, CN);
        builder.addRDN(BCStyle.O, "Ministartstvo Zdravlja");
        builder.addRDN(BCStyle.OU, "Klinicki centar Vojvodine");
        builder.addRDN(BCStyle.L, "Novi Sad");
        builder.addRDN(BCStyle.C, "RS");
        return builder;
    }

    private Certificate generateRoot(KeyStore keyStore) throws KeyStoreException {
        KeyPair kp = keyStoreService.generateKeyPair();

        X500NameBuilder builder = generateName("ROOT");

        IssuerData issuerData = keyStoreService.generateIssuerData(kp.getPrivate(), builder.build());
        SubjectData subjectData = keyStoreService.generateSubjectData(kp.getPublic(), builder.build(),
                Constants.CERT_TYPE.ROOT_CERT, Constants.ROOT_ALIAS);

        subjectData.setSerialNumber(Constants.ROOT_ALIAS);
        X509Certificate x509Certificate = keyStoreService.generateCertificate(subjectData, issuerData,
                Constants.CERT_TYPE.ROOT_CERT);

        keyStore.setKeyEntry(Constants.ROOT_ALIAS, kp.getPrivate(),
                applicationProperties.getPlatform().getKeystorePass().toCharArray(),
                new java.security.cert.Certificate[] { x509Certificate });

        Certificate certificate = new Certificate(subjectData.getSerialNumber(), null, null);
        certificate.setStartDate(new java.sql.Timestamp(subjectData.getStartDate().getTime()));
        certificate.setEndDate(new java.sql.Timestamp(subjectData.getEndDate().getTime()));
        certificate.setCertKeyStorePath(applicationProperties.getPlatform().getCertDir() + "/root.crt");

        return certificate;
    }
}
