/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.patternlanguage.emf.tests.util;

import org.eclipse.xtext.junit4.validation.AssertableDiagnostics;
import org.eclipse.xtext.junit4.validation.AssertableDiagnostics.DiagnosticPredicate;

public abstract class AbstractValidatorTest{

	protected DiagnosticPredicate getErrorCode(String issueId) {
		return AssertableDiagnostics.errorCode(issueId);
	}
	protected DiagnosticPredicate getWarningCode(String issueId) {
		return AssertableDiagnostics.warningCode(issueId);
	}
}
