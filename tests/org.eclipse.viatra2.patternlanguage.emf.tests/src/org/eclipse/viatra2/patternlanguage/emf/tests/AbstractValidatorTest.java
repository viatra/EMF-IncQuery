package org.eclipse.viatra2.patternlanguage.emf.tests;

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
