/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.viatra2.emf.incquery.databinding.runtime.DatabindingAdapter;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRootKey;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.MatcherFactoryRegistry;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.AnnotationParameter;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ValueReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.impl.BoolValueImpl;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.impl.StringValueImpl;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The util contains several useful methods for the databinding operations.
 * 
 * @author Tamas Szabo
 *
 */
@Singleton
public class DatabindingUtil {

	private static Map<URI, AdapterFactoryLabelProvider> registeredItemProviders = new HashMap<URI, AdapterFactoryLabelProvider>();
	private static Map<URI, IConfigurationElement> uriConfElementMap = null;
	private static ILog logger = IncQueryGUIPlugin.getDefault().getLog(); 
	private static Map<String, IMarker> orderByPatternMarkers = new HashMap<String, IMarker>();
	private static List<Pattern> generatedPatterns;
	private static Map<Pattern, IMatcherFactory<IncQueryMatcher<? extends IPatternMatch>>> generatedMatcherFactories;
	
	public static final String QUERY_EXPLORER_ANNOTATION = "QueryExplorer";
	public static final String PATTERNUI_ANNOTATION = "PatternUI";
	public static final String ORDERBY_ANNOTATION = "OrderBy";
	public static final String OBSERVABLEVALUE_ANNOTATION = "ObservableValue";
	
	@Inject
	private IResourceSetProvider resSetProvider;
	
	/**
	 * Creates a marker with a warning for the given pattern. 
	 * The marker's message will be set to the given message parameter.
	 * 
	 * @param patternFqn the fully qualified name of the pattern
	 * @param message the warning message for the marker
	 */
	public static void addOrderByPatternWarning(String patternFqn, String message) {
		if (orderByPatternMarkers.get(patternFqn) == null) {
			Pattern pattern = PatternRegistry.getInstance().getPatternByFqn(patternFqn);
			URI uri = pattern.eResource().getURI();
			String platformString = uri.toPlatformString(true);
			IResource markerLoc = ResourcesPlugin.getWorkspace().getRoot().findMember(platformString);
			try {
				IMarker marker = markerLoc.createMarker(EValidator.MARKER);
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
				marker.setAttribute(IMarker.TRANSIENT, true);
				marker.setAttribute(IMarker.LOCATION, pattern.getName());
				marker.setAttribute(EValidator.URI_ATTRIBUTE, EcoreUtil.getURI(pattern).toString());
				marker.setAttribute(IMarker.MESSAGE, message);
				orderByPatternMarkers.put(patternFqn, marker);
			} catch (CoreException e) {
				logger.log(new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, "Marker could not be created for pattern: " + patternFqn, e));
			}
		}
	}
	
	private static Map<Pattern, IMatcherFactory<IncQueryMatcher<? extends IPatternMatch>>> collectGeneratedMatcherFactories() {
		Map<Pattern, IMatcherFactory<IncQueryMatcher<? extends IPatternMatch>>> factories = new HashMap<Pattern, IMatcherFactory<IncQueryMatcher<? extends IPatternMatch>>>();
		for (IMatcherFactory<IncQueryMatcher<? extends IPatternMatch>> factory : MatcherFactoryRegistry.getContributedMatcherFactories()) {
			Pattern pattern = factory.getPattern();
			Boolean annotationValue = getValueOfQueryExplorerAnnotation(pattern);
			if (annotationValue != null && annotationValue) {
				factories.put(pattern, factory);
			}
		}
		return factories;
	}

	public static Boolean getValueOfQueryExplorerAnnotation(Pattern pattern) {
		Annotation annotation = getAnnotation(pattern, QUERY_EXPLORER_ANNOTATION);
		if (annotation == null) {
			return null;
		}
		else {
			for (AnnotationParameter ap : annotation.getParameters()) {
				if (ap.getName().equalsIgnoreCase("display")) {
					return Boolean.valueOf(((BoolValueImpl) ap.getValue()).isValue());
				}
			}
			return Boolean.TRUE;
		}
	}
	
	public static synchronized Collection<IMatcherFactory<IncQueryMatcher<? extends IPatternMatch>>> getGeneratedMatcherFactories() {
		if (generatedMatcherFactories == null) {
			generatedMatcherFactories = collectGeneratedMatcherFactories();
		}
		return Collections.unmodifiableCollection(generatedMatcherFactories.values());
	}
	
	public static synchronized List<Pattern> getGeneratedPatterns() {
		if (generatedPatterns == null) {
			generatedPatterns = collectGeneratedPatterns();
		}
		return Collections.unmodifiableList(generatedPatterns);
	}
	
	private static List<Pattern> collectGeneratedPatterns() {
		List<Pattern> patterns = new ArrayList<Pattern>();
		for (IMatcherFactory<IncQueryMatcher<? extends IPatternMatch>> factory : getGeneratedMatcherFactories()) {
			patterns.add(factory.getPattern());
		}
		return patterns;
	}

	/**
	 * Removes the marker for the given pattern if it is present. 
	 * 
	 * @param patternFqn the fully qualified name of the pattern
	 */
	public static void removeOrderByPatternWarning(String patternFqn) {
		IMarker marker = orderByPatternMarkers.remove(patternFqn);
		if (marker != null) {
			try {
				marker.delete();
			} 
			catch (CoreException e) {
				logger.log(new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, "Marker could not be deleted: " + marker.toString(), e));
			}
		}
	}
	
	/**
	 * Returns the {@link AdapterFactoryLabelProvider} instance for the given uri.
	 * 
	 * @param uri the uri
	 * @return the {@link AdapterFactoryLabelProvider} instance
	 */
	public static AdapterFactoryLabelProvider getAdapterFactoryLabelProvider(URI uri) {
		if (uriConfElementMap == null) {
			uriConfElementMap = collectItemProviders();
		}
		AdapterFactoryLabelProvider af = registeredItemProviders.get(uri);
		if (af != null) {
			return af;
		}
		else {
			IConfigurationElement ce = uriConfElementMap.get(uri);
			try {
				if (ce != null) {
					Object obj = ce.createExecutableExtension("class");
					AdapterFactoryLabelProvider lp = new AdapterFactoryLabelProvider((AdapterFactory) obj);
					registeredItemProviders.put(uri, lp);
					return lp;
				}
			} catch (CoreException e) {
				logger.log(new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, "AdapterFactory could not be created for uri: " + uri.toString(), e));
			}
			return null;
		}
	}
	
	private static Map<URI, IConfigurationElement> collectItemProviders() {
		Map<URI, IConfigurationElement> result = new HashMap<URI, IConfigurationElement>();
		try {
			IExtensionRegistry reg = Platform.getExtensionRegistry();
			IExtensionPoint ep = reg.getExtensionPoint("org.eclipse.emf.edit.itemProviderAdapterFactories");
			for (IExtension e : ep.getExtensions()) {
				for (IConfigurationElement ce : e.getConfigurationElements()) {
					if (ce.getName().matches("factory")) {
						URI uri = URI.createURI(ce.getAttribute("uri"));
						result.put(uri, ce);
					}
				}
			}
		} catch (Exception e) {
			logger.log(new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, "Collecting item providers failed.", e));
		}
		return result;
	}
	
	/**
	 * Returns a text message for a generated, not filtered matcher about the current match size.
	 * @param matcher
	 * @param matchesSize
	 * @param patternFqn
	 * @return
	 */
	public static String getMessage(
			IncQueryMatcher<? extends IPatternMatch> matcher, int matchesSize,
			String patternFqn) {
		return getMessage(matcher, matchesSize, patternFqn, true, false, null);
	}
	
	/**
	 * Returns a text message about the matches size for the given matcher.
	 * 
	 * @param matcher the {@link IncQueryMatcher} instance
	 * @param matchesSize the size of the matchset
	 * @param patternFqn the pattern fqn
	 * @param isGenerated true, if the matcher is generated, false if generic
	 * @param isFiltered true, if the matcher is filtered, false otherwise
	 * @return the label associated to the matcher
	 */
	public static String getMessage(
			IncQueryMatcher<? extends IPatternMatch> matcher, 
			int matchesSize, String patternFqn, 
			boolean isGenerated, boolean isFiltered, String exceptionMessage) {
		String isGeneratedString = isGenerated ? " (Generated)" : " (Runtime)";
		if (matcher == null) {
			return String.format("%s - %s (see Error Log)", patternFqn, exceptionMessage);
		} else {
			String matchString;
			switch (matchesSize){
			case 0: 
				matchString = "No matches";
				break;
			case 1:
				matchString = "1 match";
				break;
			default:
				matchString = String.format("%d matches", matchesSize);
			}
			
			String filtered = isFiltered ? " - Filtered" : "";
			
			//return this.matcher.getPatternName() + (isGeneratedString +" [size of matchset: "+matches.size()+"]");
			return String.format("%s - %s %s %s", matcher.getPatternName(), matchString, filtered, isGeneratedString);
		}
	}
	
	/**
	 * Get the value of the PatternUI annotation's message attribute for the pattern which name is patternName. 
	 * 
	 * @param patternName the name of the pattern
	 * @return the content of the message attribute
	 */
	public static String getMessage(IPatternMatch match, boolean generatedMatcher) {
		if (generatedMatcher) {
			return getMessageForGeneratedMatcher(match);
		}
		else {
			return getMessageForGenericMatcher(match);
		}
	}
	
	private static String getMessageForGeneratedMatcher(IPatternMatch match) {
		String patternName = match.patternName();
		try {
			IExtensionRegistry reg = Platform.getExtensionRegistry();
			IExtensionPoint ep = reg
					.getExtensionPoint("org.eclipse.viatra2.emf.incquery.databinding.runtime.databinding");
			for (IExtension e : ep.getExtensions()) {
				for (IConfigurationElement ce : e.getConfigurationElements()) {
					String[] tokens = patternName.split("\\.");
					String pattern = tokens[tokens.length - 1];

					if (ce.getName().equals("databinding") && ce.getAttribute("patternName").equalsIgnoreCase(
									pattern)) {
						return ce.getAttribute("message");
					}
				}
			}
		} catch (Exception e) {
			logger.log(new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, "Message could not be retrieved for generated matcher.", e));
		}
		
		return null;
	}
	
	private static String getMessageForGenericMatcher(IPatternMatch match) {
		String patternName = match.patternName();
		Pattern pattern = null;
		
		//find PatternUI annotation
		for (Pattern p : PatternRegistry.getInstance().getPatterns()) {
			if (CorePatternLanguageHelper.getFullyQualifiedName(p).matches(patternName)) {
				pattern = p;
				for (Annotation a : p.getAnnotations()) {
					if (a.getName().matches("PatternUI")) {							
						for (AnnotationParameter ap : a.getParameters()) {
							if (ap.getName().matches("message")) {
								ValueReference valRef = ap.getValue();
								if (valRef instanceof StringValueImpl) {
									return ((StringValueImpl) valRef).getValue();
								}
							}
						}
					}
				}
			}
		}
		
		//PatternUI annotation was not found
		if (pattern != null) {
			StringBuilder message = new StringBuilder();
			if (pattern.getParameters().size() == 0) {
				message.append("(Match)");
			}
			else {
				int i = 0;
				for (Variable v : pattern.getParameters()) {
					if (i > 0) {
						message.append(", ");
					}
					//message += v.getName()+"=$"+v.getName()+"$";
					message.append(String.format("%s=$%s$", v.getName(), v.getName()));
					i++;
				}
			}
			return message.toString();
		}
		
		return null;
	}
	
	/**
	 * Get the DatabindingAdapter generated for the pattern whose name is patternName
	 * 
	 * @param patternName the name of the pattern
	 * @return an instance of the DatabindingAdapter class generated for the pattern
	 */
	public static DatabindingAdapter<IPatternMatch> getDatabindingAdapter(String patternName, boolean generatedMatcher) {
		if (generatedMatcher) {
			return getDatabindingAdapterForGeneratedMatcher(patternName);
		}
		else {
			return getDatabindingAdapterForGenericMatcher(patternName);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static DatabindingAdapter<IPatternMatch> getDatabindingAdapterForGeneratedMatcher(String patternName) {
		try {
			IExtensionRegistry reg = Platform.getExtensionRegistry();
			IExtensionPoint ep = reg.getExtensionPoint("org.eclipse.viatra2.emf.incquery.databinding.runtime.databinding");
			for (IExtension e : ep.getExtensions()) {
				for (IConfigurationElement ce : e.getConfigurationElements()) {
					String[] tokens = patternName.split("\\.");
					String pattern = tokens[tokens.length - 1];
					
					if (ce.getName().equals("databinding") && ce.getAttribute("patternName").equalsIgnoreCase(pattern)) {
						Object obj = ce.createExecutableExtension("class");

						if (obj instanceof DatabindingAdapter) {
							return (DatabindingAdapter<IPatternMatch>) obj;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.log(new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, "Could not find DatabindableMatcher for pattern named: "+patternName, e));
		}
		
		return null;
	}
	
	private static DatabindingAdapter<IPatternMatch> getDatabindingAdapterForGenericMatcher(String patternName) {
		RuntimeDatabindingAdapter adapter = new RuntimeDatabindingAdapter();
		boolean annotationFound = false;
		Pattern pattern = null;
		
		//process annotations if present

		for (Pattern p : PatternRegistry.getInstance().getPatterns()) {
			if (CorePatternLanguageHelper.getFullyQualifiedName(p).matches(patternName)) {
				pattern = p;

				for (Annotation a : p.getAnnotations()) {
					if (a.getName().matches("ObservableValue")) {
						annotationFound = true;
						String key = null, value = null;

						for (AnnotationParameter ap : a.getParameters()) {
							if (ap.getName().matches("name")) {
								ValueReference valRef = ap.getValue();
								if (valRef instanceof StringValueImpl) {
									key = ((StringValueImpl) valRef).getValue();
								}
							}

							if (ap.getName().matches("expression")) {
								ValueReference valRef = ap.getValue();
								if (valRef instanceof StringValueImpl) {
									value = ((StringValueImpl) valRef).getValue();
								}
							}
						}

						if (key != null && value != null) {
							adapter.putToParameterMap(key, value);
						}
					}
				}
			}
		}
		
		
		//try to show parameters with a name attribute
		if (!annotationFound && pattern != null) {
			for (Variable v : pattern.getParameters()) {
				adapter.putToParameterMap(v.getName(),v.getName());
			}
		}
		
		return adapter;
	}
	
	/**
	 * Returns the generated matcher factory for the given generated pattern.
	 * 
	 * @param pattern the pattern instance
	 * @return the matcher factory for the given pattern
	 */
	public static IMatcherFactory<IncQueryMatcher<? extends IPatternMatch>> getMatcherFactoryForGeneratedPattern(Pattern pattern) {
		return generatedMatcherFactories.get(pattern);
	}

	/**
	 * Create a PatternMatcher root for the given key element.
	 * 
	 * @param key the key element (editorpart + notifier)
	 * @return the PatternMatcherRoot element
	 */
	public static ObservablePatternMatcherRoot createPatternMatcherRoot(MatcherTreeViewerRootKey key) {
		ObservablePatternMatcherRoot root = new ObservablePatternMatcherRoot(key);
		
		//runtime & generated matchers
		for (Pattern pattern : PatternRegistry.getInstance().getActivePatterns()) {
			root.registerPattern(pattern);
		}

		return root;
	}
	
	/**
	 * Returns the annotation of the given pattern, whose name is the given literal parameter. 
	 *  
	 * @param pattern the pattern instance
	 * @param literal the name of the annotation
	 * @return the annotation instance
	 */
	public static Annotation getAnnotation(Pattern pattern, String literal) {
		for (Annotation a : pattern.getAnnotations()) {
			if (a.getName().equalsIgnoreCase(literal)) {
				return a;
			}
		}
		return null;
	}
	
	/**
	 * Parses the given .eiq file into a {@link PatternModel}.
	 * 
	 * @param file the .eiq file instance
	 * @return the parsed pattern model
	 */
	public PatternModel parseEPM(IFile file) {
		if (file == null) {
			return null;
		}
		ResourceSet resourceSet = resSetProvider.get(file.getProject());
		URI fileURI = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
		Resource resource = resourceSet.getResource(fileURI, true);

		if (resource != null) {
			if (resource.getErrors().size() > 0) {
				return null;
			}
			if (resource.getContents().size() >= 1) {
				EObject topElement = resource.getContents().get(0);
				return topElement instanceof PatternModel ? (PatternModel) topElement : null;
			} 
		}
		return null;
	}
}
