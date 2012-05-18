package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.viatra2.emf.incquery.databinding.runtime.DatabindingAdapter;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRootKey;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.AnnotationParameter;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ValueReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
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
				logger.log(new Status(IStatus.INFO, IncQueryGUIPlugin.PLUGIN_ID, "AdapterFactory could not be created for uri: " + uri.toString(), e));
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
			e.printStackTrace();
		}
		return result;
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
			e.printStackTrace();
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
		
		
//		Object tmp = match.get(0);
//		if (tmp instanceof EObject) {
//			EObject eObj = (EObject) tmp;
//			URI uri = URI.createURI(eObj.eClass().getEPackage().getNsURI());
//			AdapterFactory af = registeredItemProviders.get(uri);
//			if (af != null) {
//				AdapterFactoryLabelProvider aflp = new AdapterFactoryLabelProvider(af);
//				System.out.println(aflp.getText(eObj));
//			}
//		}
		
		//PatternUI annotation was not found
		if (pattern != null) {
			String message = ""; 
			if (pattern.getParameters().size() == 0) {
				message = "(Match)";
			}
			else {
				int i = 0;
				for (Variable v : pattern.getParameters()) {
					if (i > 0) {
						message += ", ";
					}
					message += v.getName()+"=$"+v.getName()+"$";
					i++;
				}
			}
			return message;
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

						if (obj != null && obj instanceof DatabindingAdapter) {
							return (DatabindingAdapter<IPatternMatch>) obj;
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Could not find DatabindableMatcher for pattern named: "+patternName);
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
	 * Create a PatternMatcher root for the given key element.
	 * 
	 * @param key the key element (editorpart + resource set)
	 * @return the PatternMatcherRoot element
	 */
	@SuppressWarnings({ "unchecked" })
	public static ObservablePatternMatcherRoot createPatternMatcherRoot(MatcherTreeViewerRootKey key) {
		ObservablePatternMatcherRoot root = new ObservablePatternMatcherRoot(key);

		//generated matchers
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint("org.eclipse.viatra2.emf.incquery.databinding.runtime.databinding");
		for (IExtension e : ep.getExtensions()) {
			for (IConfigurationElement ce : e.getConfigurationElements()) {
				try {
					Object obj = ce.createExecutableExtension("matcherFactoryClass");

					if (obj instanceof IMatcherFactory<?, ?>) {
						IMatcherFactory<IPatternMatch, IncQueryMatcher<IPatternMatch>> factory = (IMatcherFactory<IPatternMatch, IncQueryMatcher<IPatternMatch>>) obj;
						IncQueryMatcher<IPatternMatch> matcher = factory.getMatcher(key.getNotifier());
						String patternFqn = factory.getPatternFullyQualifiedName();
						root.addMatcher(matcher, patternFqn, true);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		
		//runtime matchers
		for (Pattern pattern : PatternRegistry.getInstance().getActivePatterns()) {
			root.registerPattern(pattern);
		}

		return root;
	}
	
	public static boolean hasOffAnnotation(Pattern pattern) {
		for (Annotation a : pattern.getAnnotations()) {
			if (a.getName().equalsIgnoreCase("Off")) {
				return true;
			}
		}
		return false;
	}

	@Inject
	IResourceSetProvider resSetProvider;
	
	public PatternModel parseEPM(IFile file) {
		if (file == null) {
			return null;
		}
		ResourceSet resourceSet = resSetProvider.get(file.getProject());
		URI fileURI = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
		Resource resource = resourceSet.getResource(fileURI, true);
		if (resource != null && resource.getContents().size() >= 1) {
			EObject topElement = resource.getContents().get(0);
			return topElement instanceof PatternModel ? (PatternModel) topElement : null;
		} 
		else {
			return null;
		}
	}
}
