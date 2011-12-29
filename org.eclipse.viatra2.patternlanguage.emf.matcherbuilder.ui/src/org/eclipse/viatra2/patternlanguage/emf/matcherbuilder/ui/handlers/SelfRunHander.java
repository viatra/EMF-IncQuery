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

package org.eclipse.viatra2.patternlanguage.emf.matcherbuilder.ui.handlers;

import java.util.Collection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternSignature;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.emf.matcherbuilder.runtime.PatternRegistry;
import org.eclipse.viatra2.patternlanguage.emf.matcherbuilder.ui.Activator;

/**
 * @author Bergmann Gábor
 *
 */
public class SelfRunHander extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		EvaluationJob job = new EvaluationJob("IncQuery self-evaluation", selection);
		job.setUser(true);
		job.schedule();
		return null;
	}
	
	private class EvaluationJob extends Job{

		public EvaluationJob(String name, IStructuredSelection selection) {
			super(name);
			this.selection = selection;
		}
		IStructuredSelection selection;
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			Object firstElement = selection.getFirstElement();
			if  (!(firstElement instanceof IFile)) return null;
			IFile iFile = (IFile)firstElement;

			try {
				PatternModel parsedEPM = parseEPM(iFile);
				PatternRegistry.INSTANCE.registerAllInModel(parsedEPM);
				ResourceSet resourceSet = parsedEPM.eResource().getResourceSet();
				
				EList<Pattern> patterns = parsedEPM.getPatterns();
				for (Pattern pattern : patterns) {
					System.out.println();
					System.out.println("*** " + pattern.getName() + " (" + PatternRegistry.fqnOf(pattern)+")");
					IMatcherFactory<GenericPatternSignature, GenericPatternMatcher> matcherFactory = 
							PatternRegistry.INSTANCE.getMatcherFactory(pattern);
					GenericPatternMatcher matcher = matcherFactory.getMatcher(resourceSet);
					Collection<GenericPatternSignature> allMatches = matcher.getAllMatchesAsSignature();
					for (GenericPatternSignature signature : allMatches) {
						System.out.println("\t\t" + signature.prettyPrint());
					}
				}				
			} catch (RuntimeException e) {
				return reportException(e);
			} catch (IncQueryRuntimeException e) {
				return reportException(e);
			}
			return Status.OK_STATUS;
		}
		

	}
	
	static PatternModel parseEPM(IFile file) {
		if (file == null) return null;
		ResourceSet resourceSet = new ResourceSetImpl();
		URI fileURI = URI.createPlatformResourceURI(file.getFullPath()
				.toString(), false);
		Resource resource = resourceSet.getResource(fileURI, true);
		if (resource != null && resource.getContents().size() == 1) {
			EObject topElement = resource.getContents().get(0);
			return topElement instanceof PatternModel ? (PatternModel) topElement
					: null;
		} else
			return null;
	}
	
	/**
	 * @param e
	 */
	private static Status reportException(Exception e) {
		String errorMessage = "An error occurred during EMF-IncQuery self-run evaluation. "
							//+ "See also error log. "
							+ "\n Error message: " + e.getMessage()
							+ "\n Error class: " + e.getClass().getCanonicalName()
							+ "\n\t (see Error Log for further details.)";
		Status status = new Status(Status.ERROR, Activator.PLUGIN_ID, errorMessage, e);
		//Activator.log(status);
		return status;
	}


}
