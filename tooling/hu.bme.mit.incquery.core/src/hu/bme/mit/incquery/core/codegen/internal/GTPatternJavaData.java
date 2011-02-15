/*******************************************************************************
 * Copyright (c) 2004-2010 Akos Horvath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath - initial API and implementation
 *******************************************************************************/


/*******************************************************************************
 * Copyright (c) 2004-2010 Akos Horvath, Gergely Varro and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath - initial API and implementation
 *******************************************************************************/

package hu.bme.mit.incquery.core.codegen.internal;

/**
 * Represents all the necessary information of the GTPatterns's Java reprsentation (matcherPackage, matcherName, signaturePackage, signatureName)
 * @author Akos Horvath
 *
 */
public class GTPatternJavaData {
	
	String matcherName, matcherPackage, signatureName, signaturePackage, patternName;

	public String getMatcherName() {
		return matcherName;
	}

	public void setMatcherName(String matcherName) {
		this.matcherName = matcherName;
	}

	public String getMatcherPackage() {
		return matcherPackage;
	}

	public void setMatcherPackage(String matcherPackage) {
		this.matcherPackage = matcherPackage;
	}

	public String getSignatureName() {
		return signatureName;
	}

	public void setSignatureName(String signatureName) {
		this.signatureName = signatureName;
	}

	public String getSignaturePackage() {
		return signaturePackage;
	}

	public void setSignaturePackage(String signaturePackage) {
		this.signaturePackage = signaturePackage;
	}

	public String getPatternName() {
		return patternName;
	}

	public void setPatternName(String patternName) {
		this.patternName = patternName;
	}

}
