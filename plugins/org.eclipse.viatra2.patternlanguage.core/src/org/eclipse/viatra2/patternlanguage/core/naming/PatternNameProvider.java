/*******************************************************************************
 * Copyright (c) 2011 Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.patternlanguage.core.naming;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.scoping.XbaseQualifiedNameProvider;

import com.google.inject.Inject;

public class PatternNameProvider extends XbaseQualifiedNameProvider {

	@Inject
	IQualifiedNameConverter nameConverter;
	
	@Override
	public QualifiedName getFullyQualifiedName(EObject obj) {
		if (obj instanceof Pattern) {
			Pattern pattern = (Pattern) obj;
			return nameConverter.toQualifiedName(CorePatternLanguageHelper.getFullyQualifiedName(pattern));
		} else if (obj instanceof PatternBody) {
			PatternBody patternBody = (PatternBody) obj;
			Pattern pattern = (Pattern) patternBody.eContainer();
			return getFullyQualifiedName(pattern).append(
					Integer.toString(pattern.getBodies().indexOf(patternBody)));
		}
		return super.getFullyQualifiedName(obj);
	}

}
