package com.hospital.adminapi.configuration.keystore;

import static com.hospital.adminapi.util.constants.Constants.ROOT_ALIAS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import com.hospital.adminapi.configuration.ApplicationProperties;
import com.hospital.adminapi.domain.IssuerData;
import com.hospital.adminapi.domain.SubjectData;
import com.hospital.adminapi.util.CertificateGenerator;
import com.hospital.adminapi.util.IssuerDataGenerator;
import com.hospital.adminapi.util.KeyPairGenerator;
import com.hospital.adminapi.util.SubjectDataGenerator;
import com.hospital.adminapi.util.constants.Constants;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyStoreConfiguration {

    @Autowired
    public ApplicationProperties applicationProperties;

    @Bean(name = "keyStore")
    public KeyStore getKeystore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS", "SUN");
            File f = new File(applicationProperties.getKeyStore().getFilepath());
            if (f.exists()) {
                keyStore.load(new FileInputStream(f), applicationProperties.getKeyStore().getPassword().toCharArray());
            } else {
                keyStore.load(null, applicationProperties.getKeyStore().getPassword().toCharArray());
                // trustStore.load(null,
                // applicationProperties.getTrustStore().getPassword().toCharArray());

                generateRoot(keyStore);
                this.writeCertToFile(keyStore, ROOT_ALIAS);
                // trustStore.setCertificateEntry(ROOT_ALIAS, root_cert);

                // X509Certificate pki_cert = generatePKICert(keyStore, root_cert);
                // this.writeCertToFile(keyStore, PKI_ALIAS);
                // trustStore.setCertificateEntry(PKI_ALIAS, pki_cert);

                // trustStore.store(new
                // FileOutputStream(applicationProperties.getTrustStore().getFilepath()),
                // applicationProperties.getTrustStore().getPassword().toCharArray());
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

        IssuerData issuerData = IssuerDataGenerator.generateIssuerData(keyPair.getPrivate(), builder.build(),
                keyPair.getPublic());
        SubjectData subjectData = SubjectDataGenerator.generateSubjectData(keyPair.getPublic(), builder.build());

        subjectData.setSerialNumber(ROOT_ALIAS);
        Certificate certificate = CertificateGenerator.generateCertificate(subjectData, issuerData,
                Constants.CERT_TYPE.ROOT_CERT);

        keyStore.setKeyEntry(ROOT_ALIAS, keyPair.getPrivate(),
                applicationProperties.keyStore.getPassword().toCharArray(), new Certificate[] { certificate });

        return (X509Certificate) certificate;
    }

    private void writeCertToFile(KeyStore keyStore, String alias) throws Exception {

        Certificate[] chain = keyStore.getCertificateChain(alias);

        StringWriter sw = new StringWriter();
        JcaPEMWriter pm = new JcaPEMWriter(sw);
        for (Certificate certificate : chain) {
            X509Certificate a = (X509Certificate) certificate;
            pm.writeObject(a);
        }
        pm.close();

        String fileName = "cert_" + alias + ".crt";
        String path = applicationProperties.certificates.getFilepath() + "/" + fileName;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(sw.toString());
        }
    }
}
