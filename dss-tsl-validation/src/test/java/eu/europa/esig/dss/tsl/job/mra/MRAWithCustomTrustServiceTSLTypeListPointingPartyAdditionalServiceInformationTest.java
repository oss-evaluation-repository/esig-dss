package eu.europa.esig.dss.tsl.job.mra;

import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SignatureQualification;

public class MRAWithCustomTrustServiceTSLTypeListPointingPartyAdditionalServiceInformationTest extends AbstractMRALOTLTest {

    @Override
    protected String getTrustServiceTSLTypeListPointingPartyAdditionalServiceInformation() {
        return "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForeSeals";
    }

    @Override
    protected Indication getFinalIndication() {
        return Indication.TOTAL_PASSED;
    }

    @Override
    protected SignatureQualification getFinalSignatureQualification() {
        return SignatureQualification.ADESIG;
    }

    @Override
    protected boolean isEnactedMRA() {
        return true;
    }

    @Override
    protected String getMRAEnactedTrustServiceLegalIdentifier() {
        return null;
    }

}
