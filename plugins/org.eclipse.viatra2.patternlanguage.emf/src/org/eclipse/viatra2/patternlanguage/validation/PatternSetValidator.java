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

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class PatternSetValidator {

	@Inject
	private EMFPatternLanguageJavaValidator validator;
	@Inject
	private Injector injector;
	
	public PatternSetValidationDiagnostics validate(Collection<Pattern> patternSet) {
		PatternSetValidationDiagnostics chain = new PatternSetValidationDiagnostics();
		injector.injectMembers(chain);
		for (Pattern pattern : patternSet) {
			validator.validate(pattern, chain, null);
			TreeIterator<EObject> it = pattern.eAllContents();
			while(it.hasNext()) {
				validator.validate(it.next(), chain, null);
			}
		}
		return chain;
	}
	
}
