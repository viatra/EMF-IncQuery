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
package org.eclipse.viatra2.patternlanguage.core;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

public class PatternLanguageClassResolver {

	public static EClass getVariableType() {
		EPackage corePackage = EPackage.Registry.INSTANCE.getEPackage("http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage");
		return (EClass) corePackage.getEClassifier("Variable");
	}
	public static EClass getVariableReferenceType() {
		EPackage corePackage = EPackage.Registry.INSTANCE.getEPackage("http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage");
		return (EClass) corePackage.getEClassifier("VariableReference");
	}
}
