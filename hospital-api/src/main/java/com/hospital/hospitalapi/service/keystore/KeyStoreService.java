package com.hospital.hospitalapi.service.keystore;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import com.hospital.hospitalapi.configuration.constants.Constants;
import com.hospital.hospitalapi.configuration.constants.Constants.CERT_TYPE;
import com.hospital.hospitalapi.domain.cert.Certificate;
import com.hospital.hospitalapi.domain.cert.IssuerData;
import com.hospital.hospitalapi.domain.cert.SubjectData;
import com.hospital.hospitalapi.repository.KeyStoreRepository;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeyStoreService {

    @Autowired
    private KeyStoreRepository keyStoreRepository;

    public Certificate save(Certificate certificate) {
        return keyStoreRepository.save(certificate);
    }

    public void writeCertificateToFile(KeyStore keyStore, String alias, String certDirectory)
            throws KeyStoreException, IOException {
        java.security.cert.Certificate[] chain = keyStore.getCertificateChain(alias);

        StringWriter sw = new StringWriter();
        JcaPEMWriter pm = new JcaPEMWriter(sw);
        for (java.security.cert.Certificate certificate : chain) {
            X509Certificate a = (X509Certificate) certificate;
            pm.writeObject(a);
        }
        pm.close();

        String fileName = "cert_" + alias + ".crt";
        String path = certDirectory + "/" + fileName;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(sw.toString());
        }
    }

    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    public IssuerData generateIssuerData(PrivateKey privateKey, X500Name name) {
        return new IssuerData(privateKey, name);
    }

    public SubjectData generateSubjectData(PublicKey publicKey, X500Name build, CERT_TYPE certType, String rootAlias) {
        SubjectData subjectData = new SubjectData(publicKey, build);
        subjectData.setSerialNumber(UUID.randomUUID().toString());
        generateDate(subjectData, certType);

        return subjectData;
    }

    public X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData, CERT_TYPE rootCert) {
        return null;
    }

    public void generateDate(SubjectData subjectData, Constants.CERT_TYPE certType) {
        Calendar calendarLater = Calendar.getInstance();
        calendarLater.setTime(new Date());

        if (certType.equals(Constants.CERT_TYPE.ROOT_CERT)) {
            calendarLater.add(Calendar.DAY_OF_MONTH, Constants.ROOT_CERT_DURATION);
        } else {
            calendarLater.add(Calendar.DAY_OF_MONTH, Constants.LEAF_CERT_DURATION);
        }

        subjectData.setStartDate(new Date());
        subjectData.setEndDate(calendarLater.getTime());
    }
}
