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
package org.eclipse.viatra2.patternlanguage.validation;

import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;

import com.google.common.collect.Sets;

/**
 * Stateless validator for a set of patterns.
 * @author Zoltan Ujhelyi
 *
 */
public final class PatternSetValidationDiagnostics implements
		DiagnosticChain {
	Set<Diagnostic> foundErrors = Sets.newHashSet();
	Set<Diagnostic> foundWarnings = Sets.newHashSet();

	@Override
	public void merge(Diagnostic diagnostic) {
		if (diagnostic.getChildren().size() > 0) {
			addAll(diagnostic);
		} else {
			add(diagnostic);
		}
		
	}

	@Override
	public void addAll(Diagnostic diagnostic) {
		for (Diagnostic child : diagnostic.getChildren()) {
			add(child);
		}
	}

	@Override
	public void add(Diagnostic diagnostic) {
		switch(diagnostic.getSeverity()) {
		case Diagnostic.ERROR:
			foundErrors.add(diagnostic);
			break;
		case Diagnostic.WARNING:
			foundWarnings.add(diagnostic);
			break;
		default:
			break;
		}
		
	}
	
	public PatternValidationStatus getStatus() {
		if (!foundErrors.isEmpty()) {
			return PatternValidationStatus.ERROR;
		} else if (!foundWarnings.isEmpty()) {
			return PatternValidationStatus.WARNING;
		} else {
			return PatternValidationStatus.OK;
		}
	}
	
	public Set<Diagnostic> getAllErrors() {
		return Sets.newHashSet(foundErrors);
	}
	public Set<Diagnostic> getAllWarnings() {
		return Sets.newHashSet(foundWarnings);
	}
	
	public void logErrors(Logger logger) {
		for (Diagnostic diag : foundErrors) {
			logger.error(stringRepresentation(diag));
		}
	}
	
	public void logAllMessages(Logger logger) {
		logErrors(logger);
		for (Diagnostic diag : foundWarnings) {
			logger.warn(stringRepresentation(diag));
		}
	}
	
	private String stringRepresentation(Diagnostic diag) {
		return String.format("%s (%s)", diag.getMessage(), diag.getSource());
	}
}