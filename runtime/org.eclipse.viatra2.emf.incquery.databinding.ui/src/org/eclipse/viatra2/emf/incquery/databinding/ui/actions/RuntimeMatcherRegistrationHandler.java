package org.eclipse.viatra2.emf.incquery.databinding.ui.actions;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.databinding.ui.DatabindingUIPluginActivator;
import org.eclipse.viatra2.emf.incquery.databinding.ui.MatchSetViewer;
import org.eclipse.viatra2.emf.incquery.databinding.ui.observable.PatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.databinding.ui.observable.ViewerRoot;
import org.eclipse.viatra2.emf.incquery.databinding.ui.observable.ViewerRootKey;
import org.eclipse.viatra2.emf.incquery.databinding.ui.util.PatternMemory;
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternSignature;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.emf.matcherbuilder.runtime.PatternRegistry;

import com.google.inject.Injector;

public class RuntimeMatcherRegistrationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		EvaluationJob job = new EvaluationJob("Runtime matcher registration", selection);
		job.setUser(true);
		job.schedule();
		return null;
	}

	private class EvaluationJob extends Job {

		private IStructuredSelection selection;
		
		public EvaluationJob(String name, IStructuredSelection selection) {
			super(name);
			this.selection = selection;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			Object firstElement = selection.getFirstElement();
			if (!(firstElement instanceof IFile)) {
				return null;
			}
			else {
				IFile iFile = (IFile) firstElement;
				ViewerRoot vr = MatchSetViewer.viewerRoot;
				
				try {
					PatternModel parsedEPM = parseEPM(iFile);
					PatternRegistry.INSTANCE.registerAllInModel(parsedEPM);
					PatternMemory.INSTANCE.unregisterFactories(iFile);
					
					Map<IncQueryMatcher<? extends IPatternSignature>, Set<ViewerRootKey>> matchersToRemove = PatternMemory.INSTANCE.getMatchers(iFile);
					if (matchersToRemove != null) {
						for (IncQueryMatcher<? extends IPatternSignature> matcher : matchersToRemove.keySet()) {
							for (ViewerRootKey vRoot : matchersToRemove.get(matcher)) {
								vr.getRootsMap().get(vRoot).removeMatcher(matcher);
								PatternMemory.INSTANCE.unregisterPattern(iFile, matcher, vRoot);
							}
						}
					}
					
					EList<Pattern> patterns = parsedEPM.getPatterns();
					for (Pattern pattern : patterns) {
						
						IMatcherFactory<GenericPatternSignature, GenericPatternMatcher> matcherFactory = 
								PatternRegistry.INSTANCE.getMatcherFactory(pattern);
						PatternMemory.INSTANCE.registerFactory(iFile, matcherFactory);
						
						for (Entry<ViewerRootKey, PatternMatcherRoot> mroot : vr.getRootsMap().entrySet()) {
							Notifier notifier = mroot.getKey().getNotifier();
							IncQueryMatcher<GenericPatternSignature> matcher = matcherFactory.getMatcher(notifier);
							
							PatternMemory.INSTANCE.registerPattern(iFile, matcher, mroot.getKey());
							mroot.getValue().addMatcher(matcher);
						}
					}
				} 
				catch (RuntimeException e) {
					return reportException(e);
				} 
				catch (IncQueryRuntimeException e) {
					return reportException(e);
				}
				return Status.OK_STATUS;
			}
		}

	}

	static PatternModel parseEPM(IFile file) {
		Injector injector = new EMFPatternLanguageStandaloneSetup().createInjectorAndDoEMFRegistration();
		if (file == null) {
			return null;
		}

		ResourceSet resourceSet = injector.getInstance(ResourceSet.class);
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

	private static Status reportException(Exception e) {
		String errorMessage = "An error occurred during runtime matcher registration. "
				+ "\n Error message: "
				+ e.getMessage()
				+ "\n Error class: "
				+ e.getClass().getCanonicalName()
				+ "\n\t (see Error Log for further details.)";
		Status status = new Status(Status.ERROR, DatabindingUIPluginActivator.PLUGIN_ID, errorMessage, e);
		return status;
	}

}
