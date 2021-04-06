package com.hospital.hospitalapi.service.keystore;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import com.hospital.hospitalapi.configuration.constants.Constants;
import com.hospital.hospitalapi.configuration.constants.Constants.CERT_TYPE;
import com.hospital.hospitalapi.domain.cert.Certificate;
import com.hospital.hospitalapi.domain.cert.IssuerData;
import com.hospital.hospitalapi.domain.cert.SubjectData;
import com.hospital.hospitalapi.repository.CertificateRepository;

import org.bouncycastle.asn1.x500.X500Name;
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
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeyStoreService {

    @Autowired
    private CertificateRepository certificateRepository;

    public Certificate save(Certificate certificate) {
        return certificateRepository.save(certificate);
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

    public X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData, CERT_TYPE certType) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            builder = builder.setProvider("BC");

            ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());

            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
                    new BigInteger(subjectData.getSerialNumber()), subjectData.getStartDate(), subjectData.getEndDate(),
                    subjectData.getX500name(), subjectData.getPublicKey());

            if (certType == Constants.CERT_TYPE.ROOT_CERT) {
                certGen.addExtension(Extension.keyUsage, false,
                        new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign));

                certGen.addExtension(Extension.basicConstraints, false, new BasicConstraints(true));

                byte[] subjectKeyIdentifier = new JcaX509ExtensionUtils()
                        .createSubjectKeyIdentifier(subjectData.getPublicKey()).getKeyIdentifier();

                certGen.addExtension(Extension.subjectKeyIdentifier, false,
                        new SubjectKeyIdentifier(subjectKeyIdentifier));
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
