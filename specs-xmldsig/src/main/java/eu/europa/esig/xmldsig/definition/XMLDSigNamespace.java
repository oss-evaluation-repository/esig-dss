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
package eu.europa.esig.xmldsig.definition;

import eu.europa.esig.dss.xml.common.definition.DSSNamespace;

/**
 * Defines the namespace corresponding to the
 * {@code <a href="https://www.w3.org/TR/xmldsig-core/xmldsig-core-schema.xsd">XMLDSig schema</a>}
 *
 */
public final class XMLDSigNamespace {

	/** Default XMLDSig namespace definition */
	public static final DSSNamespace NS = new DSSNamespace("http://www.w3.org/2000/09/xmldsig#", "ds");

	/**
	 * Empty constructor
	 */
	private XMLDSigNamespace() {
		// empty
	}

}
