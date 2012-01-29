package org.eclipse.viatra2.emf.incquery.databinding.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.databinding.ui.MatchSetViewer;
import org.eclipse.viatra2.emf.incquery.databinding.ui.observable.PatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.databinding.ui.observable.ViewerRoot;
import org.eclipse.viatra2.emf.incquery.databinding.ui.util.DatabindingUtil;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageStandaloneSetup;
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

				PatternModel parsedEPM = parseEPM(iFile);
				PatternRegistry.INSTANCE.registerAllInModel(parsedEPM);
				
				DatabindingUtil.registeredPatterModels.remove(iFile);
				
				for (PatternMatcherRoot root : vr.getRoots()) {
					root.unregisterPatternsFromFile(iFile);
				}

				DatabindingUtil.registeredPatterModels.put(iFile, parsedEPM);
				
				for (PatternMatcherRoot root : vr.getRoots()) {
					root.registerPatternsFromFile(iFile, parsedEPM);
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
}
