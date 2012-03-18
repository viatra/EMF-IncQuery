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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel;

/**
 * Provides common functionality of pattern-specific generated matcher factories.
 * @author Bergmann Gábor
 * @author Mark Czotter
 *
 */
public abstract class BaseGeneratedMatcherFactory<Signature extends IPatternMatch, Matcher extends BaseGeneratedMatcher<Signature>>
		extends BaseMatcherFactory<Signature, Matcher> 
{
	public static final String XMI_OUTPUT_FOLDER = "queries";
	public static final String GLOBAL_EIQ_FILENAME = "globalEiqModel.xmi";
	
	private static Resource globalXmiResource;
	private static PatternModel modelRoot;
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
	
//	protected abstract String patternString();
	/**
	 * Returns the bundleName (plug-in name).
	 * @return
	 */
	protected abstract String getBundleName();
	
	/**
	 * Returns the fully qualified name of the pattern.
	 * @return
	 */
	protected abstract String patternName();
	
	protected Pattern parsePattern() {
//		PatternModel model = parseRoot(getAsStream(patternString()));
		PatternModel model = getModelRoot(getBundleName());
		return findPattern(model, patternName());
	}
	
	/**
	 * 
	 * @param bundleName
	 * @return
	 */
	public static PatternModel getModelRoot(String bundleName) {
		if (bundleName == null || bundleName.isEmpty()) return null;
		if (modelRoot == null) {
			Resource res = getGlobalXmiResource(bundleName);
			if (res != null) {
				modelRoot = (PatternModel) res.getContents().get(0);	
			}	
		}
		return modelRoot;
	}
	
	/**
	 * Returns the global EIQ resource (XMI), that is hosted in the given bundle.
	 * If no resource is found, null is returned.
	 * @param bundleName, cant be null
	 * @return
	 */
	private static Resource getGlobalXmiResource(String bundleName) {
		if (globalXmiResource == null) {
			ResourceSet set = new ResourceSetImpl();
			try { 
				globalXmiResource = set.getResource(getGlobalEiqModelUri(bundleName), true);
			} catch (Exception e) {
				System.err.println("Exception during Global XMi Resource load: " + e.getMessage());
				globalXmiResource = null;
			}
		}
		return globalXmiResource;
	}
	
	/**
	 * Creates a platformplugin URI from bundleName and default location of the global EIQ model file path
	 * @param bundleName
	 * @return
	 */
	private static URI getGlobalEiqModelUri(String bundleName) {
		return URI.createPlatformPluginURI(String.format("%s/%s/%s",
				bundleName, XMI_OUTPUT_FOLDER, GLOBAL_EIQ_FILENAME), true);
	}
	
	/**
	 * Returns the pattern with the given patternName.
	 * @param model
	 * @param patternName
	 * @return
	 */
	private Pattern findPattern(PatternModel model, String patternName) {
		if (model == null) return null;
		for (Pattern pattern : model.getPatterns()) {
			if (pattern.getName().equals(patternName)) {
				return pattern;
			}
		}
		return null;
	}
	
//	private PatternModel parseRoot(InputStream inputStream) {
//		final Injector injector = IncQueryRuntimePlugin.getDefault().getInjector();
//		final ResourceSet resourceSet = injector.getProvider(XtextResourceSet.class).get();
//		final IResourceFactory resourceFactory = injector.getInstance(IResourceFactory.class);
//		Resource resource = resourceFactory.createResource(computeUnusedUri(resourceSet));
//		resourceSet.getResources().add(resource);
//		try {
//			resource.load(inputStream, null);
//			final PatternModel root = (PatternModel) (resource.getContents().isEmpty() ? null : resource.getContents().get(0));
//			return root;
//		} catch (IOException e) {
//			throw new WrappedException(e);
//		}
//	}

//	protected InputStream getAsStream(CharSequence text) {
//		return new StringInputStream(text == null ? "" : text.toString());
//	}
	
//	protected URI computeUnusedUri(ResourceSet resourceSet) {
//		String name = "__patternRuntime";
//		for (int i = 0; i < Integer.MAX_VALUE; i++) {
//			URI syntheticUri = URI.createURI(name + i + ".eiq");
//			if (resourceSet.getResource(syntheticUri, false) == null)
//				return syntheticUri;
//		}
//		throw new IllegalStateException();
//	}

}
