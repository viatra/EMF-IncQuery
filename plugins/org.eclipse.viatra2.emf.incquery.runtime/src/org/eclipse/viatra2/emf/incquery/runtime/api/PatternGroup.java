/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.runtime.api;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra2.emf.incquery.runtime.extensibility.MatcherFactoryRegistry;

/**
 * @author Abel Hegedus
 *
 */
@SuppressWarnings("rawtypes")
public class PatternGroup {
	
	private final Set<IMatcherFactory> matcherFactories = new HashSet<IMatcherFactory>();
	private final String packageName;
	private final boolean includeSubpackages;
	
	/**
	 * 
	 */
	public PatternGroup(String packageName) {
		this(packageName, false);
	}
	
	/**
	 * 
	 */
	public PatternGroup(String packageName, boolean includeSubpackages) {
		this.includeSubpackages = includeSubpackages;
		this.packageName = packageName;
		refreshMatcherFactories();
	}

	/**
	 * @return the matcherFactories
	 */
	public Set<IMatcherFactory> getMatcherFactories() {
		return matcherFactories;
	}

	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * @return the includeSubpackages
	 */
	public boolean isIncludeSubpackages() {
		return includeSubpackages;
	}

	public void refreshMatcherFactories() {
		if(includeSubpackages) {
			matcherFactories.addAll(MatcherFactoryRegistry.getPatternSubTree(packageName));
		} else {
			matcherFactories.addAll(MatcherFactoryRegistry.getPatternGroup(packageName));
		}
	}
}
