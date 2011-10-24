/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.patternlanguage.emf.matcherbuilder.runtime;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternSignature;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.BuilderRegistry;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.emf.matcherbuilder.internal.EPMStatelessReteBuilder;

/**
 * @author Bergmann Gábor
 *
 */
public class PatternRegistry {
	public static PatternRegistry INSTANCE = new PatternRegistry();
	
	private Map<String, Pattern> patternByFQN = new HashMap<String, Pattern>();
	private EPMStatelessReteBuilder builder = new EPMStatelessReteBuilder(this);
	
	

	public Pattern findPattern(String fqn) {
		return patternByFQN.get(fqn);
	}
	
	public IMatcherFactory<GenericPatternSignature, GenericPatternMatcher> getMatcherFactory(String fqn) {
		if (findPattern(fqn) == null) return null;
		return new GenericMatcherFactory(fqn);
	}
	public IMatcherFactory<GenericPatternSignature, GenericPatternMatcher> getMatcherFactory(Pattern pattern) {
		String fqn = fqnOf(pattern);
		if (findPattern(fqn) == null) registerSingle(pattern); // TODO handle dependencies?
		return new GenericMatcherFactory(fqn);
	}
	
	/**
	 * Dependencies are not resolved yet.
	 * @param pattern
	 */
	public void registerSingle(Pattern pattern) {
		String patternFQN = fqnOf(pattern);
		
		patternByFQN.put(patternFQN, pattern);
		BuilderRegistry.registerStatelessPatternBuilder(patternFQN, builder);
	}
	/**
	 * Dependencies are not resolved yet.
	 * @param pattern
	 */
	public void registerAllInModel(PatternModel patternModel) {
		for (Pattern pattern : patternModel.getPatterns()) {
			registerSingle(pattern);
		}
	}
	
	// TODO(bergmann) replace with FQN once available
	public static String fqnOf(Pattern pattern) {
		Resource eResource = pattern.eResource();
		String uriFragment = eResource.getURIFragment(pattern);
		URI uri = eResource.getURI().appendFragment(uriFragment);
		return uri.toString(); 
	}

}
