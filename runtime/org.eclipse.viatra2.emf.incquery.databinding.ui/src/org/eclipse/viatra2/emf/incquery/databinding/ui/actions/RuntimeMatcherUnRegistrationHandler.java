package org.eclipse.viatra2.emf.incquery.databinding.ui.actions;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.databinding.ui.MatchSetViewer;
import org.eclipse.viatra2.emf.incquery.databinding.ui.observable.ViewerRoot;
import org.eclipse.viatra2.emf.incquery.databinding.ui.observable.ViewerRootKey;
import org.eclipse.viatra2.emf.incquery.databinding.ui.util.PatternMemory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;

public class RuntimeMatcherUnRegistrationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getCurrentSelection(event);
		EvaluationJob job = new EvaluationJob("Runtime matcher unregistration",
				selection);
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
			} else {
				IFile iFile = (IFile) firstElement;
				ViewerRoot vr = MatchSetViewer.viewerRoot;

				PatternMemory.INSTANCE.unregisterFactories(iFile);

				Map<IncQueryMatcher<? extends IPatternSignature>, Set<ViewerRootKey>> matchersToRemove = 
						PatternMemory.INSTANCE.getMatchers(iFile);
				if (matchersToRemove != null) {
					for (IncQueryMatcher<? extends IPatternSignature> matcher : matchersToRemove.keySet()) {
						for (ViewerRootKey vRoot : matchersToRemove.get(matcher)) {
							vr.getRootsMap().get(vRoot).removeMatcher(matcher);
							PatternMemory.INSTANCE.unregisterPattern(iFile,	matcher, vRoot);
						}
					}
				}

				return Status.OK_STATUS;
			}
		}

	}
}
