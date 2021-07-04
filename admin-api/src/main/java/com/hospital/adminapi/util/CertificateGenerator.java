package com.hospital.adminapi.util;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import com.hospital.adminapi.domain.IssuerData;
import com.hospital.adminapi.domain.SubjectData;
import com.hospital.adminapi.util.constants.Constants;

import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class CertificateGenerator {

        public static X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData,
                        Constants.CERT_TYPE type) {
                try {
                        // Posto klasa za generisanje sertifiakta ne moze da primi direktno privatni
                        // kljuc pravi se builder za objekat
                        // Ovaj objekat sadrzi privatni kljuc izdavaoca sertifikata i koristiti se za
                        // potpisivanje sertifikata
                        // Parametar koji se prosledjuje je algoritam koji se koristi za potpisivanje
                        // sertifiakta
                        Security.addProvider(new BouncyCastleProvider());
                        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
                        builder = builder.setProvider("BC");

                        // Formira se objekat koji ce sadrzati privatni kljuc i koji ce se koristiti za
                        // potpisivanje sertifikata
                        ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());

                        // Postavljaju se podaci za generisanje sertifiakta
                        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
                                        new BigInteger(subjectData.getSerialNumber()), subjectData.getStartDate(),
                                        subjectData.getEndDate(), subjectData.getX500name(),
                                        subjectData.getPublicKey());

                        // dodavanje EKSTENZIJA u zavisnosti od TIPA sertifikata
                        if (type.equals(Constants.CERT_TYPE.LEAF_CERT)) {
                                // certGen.addExtension(Extension.authorityInfoAccess, false, new
                                // AuthorityInformationAccess());
                                // certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new
                                // AuthorityKeyIdentifierStructure(caCert));
                                // certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, new
                                // SubjectKeyIdentifierStructure(entityKey));
                                certGen.addExtension(Extension.keyUsage, false,
                                                new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
                                certGen.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));

                                KeyPurposeId[] keyPurposeIds = new KeyPurposeId[2];
                                keyPurposeIds[0] = KeyPurposeId.id_kp_clientAuth;
                                keyPurposeIds[1] = KeyPurposeId.id_kp_serverAuth;

                                certGen.addExtension(Extension.extendedKeyUsage, false,
                                                new ExtendedKeyUsage(keyPurposeIds));
                        } else if (type.equals(Constants.CERT_TYPE.SERVER_CERT)) {
                                certGen.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));
                                certGen.addExtension(Extension.extendedKeyUsage, false,
                                                new ExtendedKeyUsage(KeyPurposeId.id_kp_serverAuth));

                                certGen.addExtension(Extension.keyUsage, false,
                                                new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));

                                byte[] subjectKeyIdentifier = new JcaX509ExtensionUtils()
                                                .createSubjectKeyIdentifier(subjectData.getPublicKey())
                                                .getKeyIdentifier();
                                certGen.addExtension(Extension.subjectKeyIdentifier, false,
                                                new SubjectKeyIdentifier(subjectKeyIdentifier));
                        } else {
                                certGen.addExtension(Extension.keyUsage, false, new KeyUsage(
                                                KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign));

                                certGen.addExtension(Extension.basicConstraints, false, new BasicConstraints(true));

                                byte[] subjectKeyIdentifier = new JcaX509ExtensionUtils()
                                                .createSubjectKeyIdentifier(subjectData.getPublicKey())
                                                .getKeyIdentifier();

                                certGen.addExtension(Extension.subjectKeyIdentifier, false,
                                                new SubjectKeyIdentifier(subjectKeyIdentifier));
                        }

                        // ovo je da zna kojim issuera public kljucem da proveri potpis sertifika
                        // sertifikate
                        // jer su potpisani privatnim, koristi se kad CA ima vise parova kljucava
                        // kojima potpisuje sertifikate..
                        byte[] authorityKeyIdentifer = new JcaX509ExtensionUtils()
                                        .createAuthorityKeyIdentifier(issuerData.getPublicKey()).getKeyIdentifier();
                        //
                        certGen.addExtension(Extension.authorityKeyIdentifier, false,
                                        new AuthorityKeyIdentifier(authorityKeyIdentifer));

                        //

                        GeneralName altName = new GeneralName(GeneralName.dNSName, "localhost");
                        GeneralNames subjectAltName = new GeneralNames(altName);
                        certGen.addExtension(Extension.subjectAlternativeName, false, subjectAltName);

                        // Generise se sertifikat
                        X509CertificateHolder certHolder = certGen.build(contentSigner);

                        // Builder generise sertifikat kao objekat klase X509CertificateHolder
                        // Nakon toga je potrebno certHolder konvertovati u sertifikat, za sta se
                        // koristi certConverter
                        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
                        certConverter = certConverter.setProvider("BC");

                        // Konvertuje objekat u sertifikat
                        return certConverter.getCertificate(certHolder);
                } catch (CertificateEncodingException e) {
                        e.printStackTrace();
                } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                } catch (IllegalStateException e) {
                        e.printStackTrace();
                } catch (OperatorCreationException e) {
                        e.printStackTrace();
                } catch (CertificateException e) {
                        e.printStackTrace();
                } catch (CertIOException e) {
                        e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                }
                return null;
        }
}
