package com.hospital.adminapi.configuration.keystore;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import com.hospital.adminapi.configuration.ApplicationProperties;
import com.hospital.adminapi.domain.IssuerData;
import com.hospital.adminapi.util.KeyPairGenerator;

public class KeyStoreConfiguration {

    @Autowired
    public ApplicationProperties applicationProperties;

    @Bean(name = "keyStore")
    public KeyStore getKeystore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS", "SUN");
            KeyStore trustStore = KeyStore.getInstance("JKS", "SUN");
            File f = new File(applicationProperties.getKeyStore().getFilepath());
            if (f.exists()) {
                keyStore.load(new FileInputStream(f), applicationProperties.getKeyStore().getPassword().toCharArray());
            } else {
                keyStore.load(null, applicationProperties.getKeyStore().getPassword().toCharArray());
                trustStore.load(null, applicationProperties.getTrustStore().getPassword().toCharArray());

                X509Certificate root_cert = generateRoot(keyStore);
                this.writeCertToFile(keyStore, "root");
                trustStore.setCertificateEntry(ROOT_ALIAS, root_cert);

                X509Certificate pki_cert = generatePKICert(keyStore, root_cert);
                this.writeCertToFile(keyStore, PKI_ALIAS);
                // trustStore.setCertificateEntry(PKI_ALIAS, pki_cert);

                trustStore.store(new FileOutputStream(applicationProperties.getTrustStore().getFilepath()),
                        applicationProperties.getTrustStore().getPassword().toCharArray());
                keyStore.store(new FileOutputStream(applicationProperties.getKeyStore().getFilepath()),
                        applicationProperties.getKeyStore().getPassword().toCharArray());
            }
            return keyStore;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private X500NameBuilder generateX500Name(String cn) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, cn);
        builder.addRDN(BCStyle.O, "Hospital");
        builder.addRDN(BCStyle.OU, "Admin");
        builder.addRDN(BCStyle.L, "Novi Sad");
        builder.addRDN(BCStyle.C, "RS");
        return builder;
    }

    private X509Certificate generateRoot(KeyStore keyStore) throws KeyStoreException {
        KeyPair keyPair = KeyPairGenerator.generateKeyPair();

        X500NameBuilder builder = generateX500Name("ROOT");

        IssuerData issuerData = certificateGeneratorService.generateIssuerData(kp.getPrivate(), builder.build(),
                kp.getPublic());
        SubjectData subjectData = certificateGeneratorService.generateSubjectData(kp.getPublic(), builder.build(),
                Constants.CERT_TYPE.ROOT_CERT);

        subjectData.setSerialNumber(ROOT_ALIAS);
        Certificate certificate = certificateGeneratorService.generateCertificate(subjectData, issuerData,
                Constants.CERT_TYPE.ROOT_CERT);

        keyStore.setKeyEntry(ROOT_ALIAS, kp.getPrivate(), KEYSTORE_PASSWORD.toCharArray(),
                new Certificate[] { certificate });

        return (X509Certificate) certificate;
    }
}
