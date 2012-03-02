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

package org.eclipse.viatra2.emf.incquery.runtime.api.impl;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.internal.Activator;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.StringInputStream;

import com.google.inject.Injector;

/**
 * Provides common functionality of pattern-specific generated matcher factories.
 * @author Bergmann Gábor
 *
 */
public abstract class BaseGeneratedMatcherFactory<Signature extends IPatternMatch, Matcher extends BaseGeneratedMatcher<Signature>>
		extends BaseMatcherFactory<Signature, Matcher> 
{
	private Pattern pattern;
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory#getPattern()
	 */
	@Override
	public Pattern getPattern() {
		if (pattern == null) 
			pattern = parsePattern();
		return pattern;
	}
	
	protected abstract String patternString();
	
	protected abstract String patternName();
	
	protected Pattern parsePattern() {
		PatternModel model = parseRoot(getAsStream(patternString()));
		return findPattern(model, patternName());
	}
	
	private PatternModel parseRoot(InputStream inputStream) {
		final Injector injector = Activator.getDefault().getInjector();
		final ResourceSet resourceSet = injector.getProvider(XtextResourceSet.class).get();
		final IResourceFactory resourceFactory = injector.getInstance(IResourceFactory.class);
		Resource resource = resourceFactory.createResource(computeUnusedUri(resourceSet));
		resourceSet.getResources().add(resource);
		try {
			resource.load(inputStream, null);
			final PatternModel root = (PatternModel) (resource.getContents().isEmpty() ? null : resource.getContents().get(0));
			return root;
		} catch (IOException e) {
			throw new WrappedException(e);
		}
	}

	private Pattern findPattern(PatternModel model, String patternName) {
		for (Pattern pattern : model.getPatterns()) {
			if (pattern.getName().equals(patternName)) {
				return pattern;
			}
		}
		return null;
	}

	protected InputStream getAsStream(CharSequence text) {
		return new StringInputStream(text == null ? "" : text.toString());
	}
	
	protected URI computeUnusedUri(ResourceSet resourceSet) {
		String name = "__patternRuntime";
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			URI syntheticUri = URI.createURI(name + i + ".eiq");
			if (resourceSet.getResource(syntheticUri, false) == null)
				return syntheticUri;
		}
		throw new IllegalStateException();
	}

}
