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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.incquery.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.patternlanguage.emf.core.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.emf.core.patternLanguage.PatternModel;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.util.XmiModelUtil;
import org.eclipse.viatra2.emf.incquery.runtime.util.XmiModelUtilRunningOptionEnum;

/**
 * Provides common functionality of pattern-specific generated matcher factories.
 * 
 * @author Bergmann Gábor
 * @author Mark Czotter
 */
public abstract class BaseGeneratedMatcherFactory<Matcher extends IncQueryMatcher<? extends IPatternMatch>> extends
        BaseMatcherFactory<Matcher> {

    private static Map<String, PatternModel> bundleNameToPatternModelMap = new HashMap<String, PatternModel>();
    
    private static Map<String, Resource> bundleNameToResourceMap = new HashMap<String, Resource>();
    
    private final Pattern pattern;

    public BaseGeneratedMatcherFactory() throws IncQueryException {
        pattern = parsePattern();
        // if (pattern == null)
        // throw new IncQueryException(
        // "Unable to parse the definition of generated pattern " + patternName() +
        // " (see log for any errors that could have caused this)",
        // "Could not parse pattern definition.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory#getPattern()
     */
    @Override
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * Returns the bundleName (plug-in name).
     * 
     * @return
     */
    protected abstract String getBundleName();

    /**
     * Returns the fully qualified name of the pattern.
     * 
     * @return
     */
    protected abstract String patternName();

    protected Pattern parsePattern() throws IncQueryException {
        if (!Platform.isRunning()) {
            throw new IncQueryException(
                    "Generated EMF-IncQuery patterns cannot be used in standalone Java applications, for now we need the Eclipse Platform running",
                    "Eclipse Platform not running");
        }
        PatternModel model = getModelRoot(getBundleName());
        try {
            return findPattern(model, patternName());
        } catch (Exception ex) {
            throw new IncQueryException("Unable to parse the definition of generated pattern " + patternName()
                    + " (see log for any errors that could have caused this)", "Could not parse pattern definition.",
                    ex);
        }
    }

    /**
     * Returns the pattern with the given patternName.
     * 
     * @param model
     * @param patternName
     * @return {@link Pattern} instance or null if not found.
     */
    private Pattern findPattern(PatternModel model, String patternName) {
        if (model == null)
            return null;
        for (Pattern pattern : model.getPatterns()) {
            if (pattern.getName().equals(patternName)) {
                return pattern;
            }
        }
        return null;
    }

    /**
     * Returns the global Xmi model Root from the given bundle.
     * 
     * @param bundleName
     * @return
     * @throws IncQueryException
     *             if model loading was unsuccessful
     */
    public static PatternModel getModelRoot(String bundleName) throws IncQueryException {
        if (bundleName == null || bundleName.isEmpty())
            return null;
        if (bundleNameToPatternModelMap.get(bundleName) == null) {
            Resource bundleResource = bundleNameToResourceMap.get(bundleName);
            if (bundleResource == null) {
                bundleResource = XmiModelUtil.getGlobalXmiResource(XmiModelUtilRunningOptionEnum.JUST_PLUGIN,
                        bundleName);
                if (bundleResource == null) {
                    return null;
                }
                bundleNameToResourceMap.put(bundleName, bundleResource);
            }
            bundleNameToPatternModelMap.put(bundleName, (PatternModel) bundleResource.getContents().get(0));
        }
        return bundleNameToPatternModelMap.get(bundleName);
    }

    protected static void processInitializerError(ExceptionInInitializerError err) throws IncQueryException {
        Throwable cause1 = err.getCause();
        if (cause1 != null && cause1 instanceof RuntimeException) {
            Throwable cause2 = ((RuntimeException) cause1).getCause();
            if (cause2 != null && cause2 instanceof IncQueryException) {
                throw (IncQueryException) cause2;
            }
        }
    }

    // private PatternModel parseRoot(InputStream inputStream) {
    // final Injector injector = IncQueryRuntimePlugin.getDefault().getInjector();
    // final ResourceSet resourceSet = injector.getProvider(XtextResourceSet.class).get();
    // final IResourceFactory resourceFactory = injector.getInstance(IResourceFactory.class);
    // Resource resource = resourceFactory.createResource(computeUnusedUri(resourceSet));
    // resourceSet.getResources().add(resource);
    // try {
    // resource.load(inputStream, null);
    // final PatternModel root = (PatternModel) (resource.getContents().isEmpty() ? null :
    // resource.getContents().get(0));
    // return root;
    // } catch (IOException e) {
    // throw new WrappedException(e);
    // }
    // }

    // protected InputStream getAsStream(CharSequence text) {
    // return new StringInputStream(text == null ? "" : text.toString());
    // }

    // protected URI computeUnusedUri(ResourceSet resourceSet) {
    // String name = "__patternRuntime";
    // for (int i = 0; i < Integer.MAX_VALUE; i++) {
    // URI syntheticUri = URI.createURI(name + i + ".eiq");
    // if (resourceSet.getResource(syntheticUri, false) == null)
    // return syntheticUri;
    // }
    // throw new IllegalStateException();
    // }

}
