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

package org.eclipse.viatra2.patternlanguage.emf.tests;

import java.util.Collection;

import org.eclipse.emf.ecore.EValidator;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageInjectorProvider;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguagePackage;
import org.eclipse.xtext.junit.AbstractXtextTests;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

@RunWith(XtextRunner.class)
@InjectWith(EMFPatternLanguageInjectorProvider.class)
public abstract class AbstractEMFPatternLanguageTest extends AbstractXtextTests {

	static final ImmutableSet<String> defaultPackages = ImmutableSet
			.of("http://www.eclipse.org/viatra2/patternlanguage/core/PatternLanguage");
	
	@Inject
	EValidator.Registry validationRegistry;
	@Inject
	EMFPatternLanguagePackage languagePackage;

	public String addImports(String content, Collection<String> packages) {
		StringBuilder sb = new StringBuilder();
		for (String pack : packages) {
			sb.append("import \"" + pack + "\"\n");
		}
		sb.append(content);
		return sb.toString();
	}

	public String addDefaultImports(String content) {
		return addImports(content, defaultPackages);
	}

}
