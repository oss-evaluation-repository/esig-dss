/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * 
 * This file is part of the "DSS - Digital Signature Services" project.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.validation.process.bbb.xcv.sub;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.europa.esig.dss.detailedreport.jaxb.XmlConstraint;
import eu.europa.esig.dss.detailedreport.jaxb.XmlStatus;
import eu.europa.esig.dss.detailedreport.jaxb.XmlSubXCV;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlCertificate;
import eu.europa.esig.dss.diagnostic.jaxb.XmlCertificatePolicy;
import eu.europa.esig.dss.enumerations.CertificatePolicy;
import eu.europa.esig.dss.validation.process.bbb.xcv.sub.checks.CertificateIssuedToLegalPersonCheck;
import eu.europa.esig.jaxb.policy.Level;
import eu.europa.esig.jaxb.policy.LevelConstraint;

public class CertificateIssuedToLegalPersonCheckTest {

	@Test
	public void certificateIssuedToLegalPersonWithPolicyIdentifierCheck() throws Exception {
		LevelConstraint constraint = new LevelConstraint();
		constraint.setLevel(Level.FAIL);

		List<XmlCertificatePolicy> qualifiers = new ArrayList<XmlCertificatePolicy>();
		XmlCertificatePolicy oid = new XmlCertificatePolicy();
		oid.setValue(CertificatePolicy.QCP_LEGAL.getOid());
		qualifiers.add(oid);

		XmlCertificate xc = new XmlCertificate();
		xc.setCertificatePolicies(qualifiers);

		XmlSubXCV result = new XmlSubXCV();
		CertificateIssuedToLegalPersonCheck citlp = new CertificateIssuedToLegalPersonCheck(result, new CertificateWrapper(xc), constraint);
		citlp.execute();

		List<XmlConstraint> constraints = result.getConstraint();
		assertEquals(1, constraints.size());
		assertEquals(XmlStatus.OK, constraints.get(0).getStatus());
	}

	@Test
	public void failedCertificateIssuedToLegalPersonWithPolicyIdentifierCheck() throws Exception {
		LevelConstraint constraint = new LevelConstraint();
		constraint.setLevel(Level.FAIL);

		List<XmlCertificatePolicy> qualifiers = new ArrayList<XmlCertificatePolicy>();
		XmlCertificatePolicy oid = new XmlCertificatePolicy();
		oid.setValue(CertificatePolicy.QCP_NATURAL.getOid());
		qualifiers.add(oid);

		XmlCertificate xc = new XmlCertificate();
		xc.setCertificatePolicies(qualifiers);

		XmlSubXCV result = new XmlSubXCV();
		CertificateIssuedToLegalPersonCheck citlp = new CertificateIssuedToLegalPersonCheck(result, new CertificateWrapper(xc), constraint);
		citlp.execute();

		List<XmlConstraint> constraints = result.getConstraint();
		assertEquals(1, constraints.size());
		assertEquals(XmlStatus.NOT_OK, constraints.get(0).getStatus());
	}

}
