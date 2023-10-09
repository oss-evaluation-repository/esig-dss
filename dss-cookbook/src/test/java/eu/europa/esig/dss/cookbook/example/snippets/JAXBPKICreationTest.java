package eu.europa.esig.dss.cookbook.example.snippets;

import eu.europa.esig.dss.enumerations.CertificateStatus;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.TimestampBinary;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.pki.jaxb.builder.JAXBCertEntityBuilder;
import eu.europa.esig.dss.pki.jaxb.model.JAXBCertEntity;
import eu.europa.esig.dss.pki.jaxb.model.JAXBCertEntityRepository;
import eu.europa.esig.dss.pki.model.CertEntity;
import eu.europa.esig.dss.pki.model.CertEntityRepository;
import eu.europa.esig.dss.pki.x509.aia.PKIAIASource;
import eu.europa.esig.dss.pki.x509.revocation.crl.PKICRLSource;
import eu.europa.esig.dss.pki.x509.revocation.ocsp.PKIDelegatedOCSPSource;
import eu.europa.esig.dss.pki.x509.revocation.ocsp.PKIOCSPSource;
import eu.europa.esig.dss.pki.x509.tsp.PKITSPSource;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.x509.revocation.crl.CRLToken;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPToken;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JAXBPKICreationTest {

    @Test
    public void test() throws Exception {
        // tag::demo[]
        // import eu.europa.esig.dss.pki.jaxb.builder.JAXBCertEntityBuilder;
        // import eu.europa.esig.dss.pki.jaxb.model.JAXBCertEntity;
        // import eu.europa.esig.dss.pki.jaxb.model.JAXBCertEntityRepository;
        // import java.io.File;
        // import java.util.List;

        // Instantiate repository to contain the information about PKI
        JAXBCertEntityRepository jaxbRepository = new JAXBCertEntityRepository();

        // Load an XML file containing PKI configuration
        File pkiFile = new File("src/test/resources/pki/good-pki.xml");

        // Init a JAXBCertEntityBuilder to load PKI from XML file
        JAXBCertEntityBuilder builder = new JAXBCertEntityBuilder();

        // Initialize a content of the PKI from XML file and load created entries to the repository
        builder.persistPKI(jaxbRepository, pkiFile);
        // ... more than one XML file can be loaded within a repository

        // After you can work with the data inside the repository
        List<JAXBCertEntity> certEntities = jaxbRepository.getAll();
        // end::demo[]

        assertEquals(30, certEntities.size());

        CertificateToken userCertificate = jaxbRepository.getCertEntityBySubject("good-user").getCertificateToken();
        CertificateToken caCertificate = jaxbRepository.getCertEntityBySubject("good-ca").getCertificateToken();
        CertificateToken rootCertificate = jaxbRepository.getCertEntityBySubject("root-ca").getCertificateToken();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, 1);
        Date nextUpdate = calendar.getTime();

        // tag::crl-source[]
        // import eu.europa.esig.dss.enumerations.CertificateStatus;
        // import eu.europa.esig.dss.enumerations.DigestAlgorithm;
        // import eu.europa.esig.dss.pki.model.CertEntityRepository;
        // import eu.europa.esig.dss.pki.x509.revocation.crl.PKICRLSource;
        // import eu.europa.esig.dss.spi.x509.revocation.crl.CRLToken;
        // import java.util.Date;

        // Initialize a CertEntityRepository
        CertEntityRepository<?> repository = jaxbRepository;

        // Instantiate PKI CRL Source with the provided repository
        PKICRLSource crlSource = new PKICRLSource(repository);

        // Configure DigestAlgorithm to be used on CRL generation
        // Default: SHA256
        crlSource.setDigestAlgorithm(DigestAlgorithm.SHA256);

        // Configure thisUpdate
        crlSource.setThisUpdate(new Date());

        // Configure nextUpdate
        crlSource.setNextUpdate(nextUpdate);

        // Get revocation for caCertificate, with rootCertificate is an issuer of caCertificate and issuer of CRL
        CRLToken crlToken = crlSource.getRevocationToken(caCertificate, rootCertificate);
        // end::crl-source[]

        assertEquals(CertificateStatus.GOOD, crlToken.getStatus());

        JAXBCertEntity ocspResponder = jaxbRepository.getCertEntityBySubject("good-ca").getOcspResponder();

        // tag::ocsp-source[]
        // import eu.europa.esig.dss.enumerations.CertificateStatus;
        // import eu.europa.esig.dss.enumerations.DigestAlgorithm;
        // import eu.europa.esig.dss.pki.model.CertEntityRepository;
        // import eu.europa.esig.dss.pki.x509.revocation.ocsp.PKIOCSPSource;
        // import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPToken;
        // import java.util.Date;

        // Instantiate PKI OCSP Source with the provided repository
        PKIOCSPSource ocspSource = new PKIOCSPSource(repository);

        // (Optional) Provide a delegated OCSP responder
        // If not defined, the issuer of target certificate will be used as an OCSP responder certificate
        ocspSource.setOcspResponder(ocspResponder);

        // Configure DigestAlgorithm to be used on OCSP generation
        // Default: SHA256
        ocspSource.setDigestAlgorithm(DigestAlgorithm.SHA256);

        // Configure thisUpdate
        ocspSource.setThisUpdate(new Date());

        // Configure producedAt
        ocspSource.setProducedAtTime(new Date());

        // Configure nextUpdate
        ocspSource.setNextUpdate(nextUpdate);

        // Defines a way the ResponderID will be defined within OCSP response (by SKI or Name)
        // Default: TRUE (ResponderID is defined by SKI)
        ocspSource.setResponderIdByKey(true);

        // Get revocation for userCertificate, with caCertificate is an issuer of userCertificate
        OCSPToken ocspToken = ocspSource.getRevocationToken(userCertificate, caCertificate);
        // end::ocsp-source[]

        assertEquals(CertificateStatus.GOOD, ocspToken.getStatus());

        Map<CertEntity, CertEntity> delegatedOCSPRespondersMap = new HashMap<>();

        // tag::ocsp-delegated-source[]
        // import eu.europa.esig.dss.pki.x509.revocation.ocsp.PKIDelegatedOCSPSource;;
        // import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPToken;

        // Instantiate PKI OCSP Source with the provided repository
        PKIDelegatedOCSPSource delegatedOCSPSource = new PKIDelegatedOCSPSource(repository);

        // Provide a map between CA certificates and their delegated OCSP responders
        delegatedOCSPSource.setOcspResponders(delegatedOCSPRespondersMap);

        // .. configure

        // Get revocation for userCertificate, with caCertificate is an issuer of userCertificate
        OCSPToken delegatedOCSPToken = delegatedOCSPSource.getRevocationToken(userCertificate, caCertificate);
        // end::ocsp-delegated-source[]

        assertEquals(CertificateStatus.GOOD, delegatedOCSPToken.getStatus());

        // tag::aia-source[]
        // import eu.europa.esig.dss.model.x509.CertificateToken;
        // import eu.europa.esig.dss.pki.x509.aia.PKIAIASource;

        // Instantiate PKI AIA Source with the provided repository
        PKIAIASource aiaSource = new PKIAIASource(repository);

        // Sets whether a complete certificate chain should be returned by AIA request. If FALSE, returns only the certificate's issuer.
        // Default: TRUE (returns a complete certificate chain)
        aiaSource.setCompleteCertificateChain(true);

        // Get the certificate issuer for the given certificate token
        Set<CertificateToken> certificateTokens = aiaSource.getCertificatesByAIA(userCertificate);
        // end::aia-source[]

        assertEquals(2, certificateTokens.size());
    }

    @Test
    public void tstTest() throws Exception {
        // Instantiate repository to contain the information about PKI
        JAXBCertEntityRepository repository = new JAXBCertEntityRepository();

        // Load an XML file containing PKI configuration
        File pkiFile = new File("src/test/resources/pki/good-pki.xml");

        // Init a JAXBCertEntityBuilder to load PKI from XML file
        JAXBCertEntityBuilder builder = new JAXBCertEntityBuilder();

        // Initialize a content of the PKI from XML file and load created entries to the repository
        builder.persistPKI(repository, pkiFile);
        // ... more than one XML file can be loaded within a repository

        // tag::tsp-source[]
        // Extract the corresponding TSA CertEntity to issue a time-stamp from the repository
        JAXBCertEntity tsaCertEntity = repository.getCertEntityBySubject("good-tsa");

        // Instantiate PKITSPSource by providing the TSA CertEntity
        PKITSPSource pkiTspSource = new PKITSPSource(tsaCertEntity);

        final DigestAlgorithm digestAlgorithm = DigestAlgorithm.SHA256;
        final byte[] toDigest = "Hello world".getBytes(StandardCharsets.UTF_8);
        final byte[] digestValue = DSSUtils.digest(digestAlgorithm, toDigest);

        // DSS will request the tsp sources (one by one) until getting a valid token.
        // If none of them succeeds, a DSSException is thrown.
        final TimestampBinary timestampBinary = pkiTspSource.getTimeStampResponse(digestAlgorithm, digestValue);
        // end::tsp-source[]

        assertNotNull(timestampBinary);
    }

}
