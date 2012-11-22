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
package org.eclipse.viatra2.emf.incquery.testing.ui.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.ui.dialogs.WorkspaceResourceDialog;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatcher;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotFactory;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.IncQuerySnapshot;
import org.eclipse.viatra2.emf.incquery.testing.core.ModelLoadHelper;
import org.eclipse.viatra2.emf.incquery.testing.core.SnapshotHelper;

import com.google.inject.Inject;

/**
 * @author Abel Hegedus
 *
 */
public class SaveSnapshotHandler extends AbstractHandler {

	@Inject
	SnapshotHelper helper;
	@Inject
	ModelLoadHelper loader;
	@Inject
	private Logger logger;
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TreeSelection) {
			saveSnapshot((TreeSelection) selection, event);
		}
		return null;
	}

	
	private void saveSnapshot(TreeSelection selection, ExecutionEvent event) {
		Object obj = selection.getFirstElement();
		
		IEditorPart editor = null;
		List<ObservablePatternMatcher> matchers = new ArrayList<ObservablePatternMatcher>();
		IncQueryEngine engine = null;
		if(obj instanceof ObservablePatternMatcher) {
			ObservablePatternMatcher observablePatternMatcher = (ObservablePatternMatcher) obj;
			editor = observablePatternMatcher.getParent().getEditorPart();
			matchers.add(observablePatternMatcher);
			IncQueryMatcher<?> matcher = observablePatternMatcher.getMatcher();
			if(matcher != null) {
			    engine = matcher.getEngine();
			}
			
		} else if(obj instanceof ObservablePatternMatcherRoot) {
			ObservablePatternMatcherRoot matcherRoot = (ObservablePatternMatcherRoot) obj;
			editor = matcherRoot.getEditorPart();
			if(matcherRoot.getMatchers().size() > 0) {
				matchers.addAll(matcherRoot.getMatchers());
				for (ObservablePatternMatcher obsMatcher : matcherRoot.getMatchers()) {
                    IncQueryMatcher<?> matcher = obsMatcher.getMatcher();
                    if(matcher != null && matcher.getEngine() != null) {
                        engine = matcher.getEngine();
                        break;
                    }
                }
			}
		}
		if(engine == null) {
			logger.error("Cannot save snapshot without IncQueryEngine!");
			return;
		}
		ResourceSet resourceSet = getResourceSetForNotifier(engine.getEmfRoot());
		if(resourceSet == null) {
			engine.getLogger().error("Cannot save snapshot, models not in ResourceSet!");
			return;
		}
		IFile snapshotFile = null;
		IFile[] files = WorkspaceResourceDialog.openFileSelection(HandlerUtil.getActiveShell(event), "Existing snapshot", "Select existing EMF-IncQuery snapshot file (Cancel for new file)", false, null, null);
		IncQuerySnapshot snapshot = null;
			
		if(files.length == 0) {
			snapshotFile = WorkspaceResourceDialog.openNewFile(HandlerUtil.getActiveShell(event), "New snapshot", "Select EMF-IncQuery snapshot target file (.eiqsnapshot extension)", null, null);
			if(snapshotFile != null && !snapshotFile.exists()) {
				snapshot = EIQSnapshotFactory.eINSTANCE.createIncQuerySnapshot();
				Resource res = resourceSet.createResource(URI.createPlatformResourceURI(snapshotFile.getFullPath().toString(),true));
				res.getContents().add(snapshot);
			} else {
				engine.getLogger().error("Selected file name must use .eiqsnapshot extension!");
				return;
			}
		} else {
			snapshotFile = files[0];
			if(snapshotFile != null && snapshotFile.getFileExtension().equals("eiqsnapshot")) {
			
				snapshot = loader.loadExpectedResultsFromFile(resourceSet,snapshotFile);
				
				if(snapshot != null) {
					if(!validateInputSpecification(engine, snapshot)) {
						return;
					}
				} else {
					engine.getLogger().error("Selected file does not contain snapshot!");
					return;
				}
			} else {
				engine.getLogger().error("Selected file not .eiqsnapshot!");
				return;
			}
		} 
		for (ObservablePatternMatcher matcher : matchers) {
			IPatternMatch filter = matcher.getMatcher().arrayToMatch(matcher.getFilter());
			if(matcher.getMatcher() != null) {
			    helper.saveMatchesToSnapshot(matcher.getMatcher(), filter, snapshot);
			}
		}
		
		if(editor != null) {
			editor.doSave(new NullProgressMonitor());
		} else {
			try {
				snapshot.eResource().save(null);
			} catch(IOException e) {
				engine.getLogger().error("Error during saving snapshot into file!",e);
			}
		}
	}


	/**
	 * @param engine
	 * @param snapshot
	 */
	private boolean validateInputSpecification(IncQueryEngine engine, IncQuerySnapshot snapshot) {
		if(snapshot.getInputSpecification() != null) {
			Notifier root = helper.getEMFRootForSnapshot(snapshot);
			Notifier matcherRoot = engine.getEmfRoot();
			if(matcherRoot != root) {
				engine.getLogger().error("Existing snapshot model root (" + root + ") not equal to selected input (" + matcherRoot + ")!");
				return false;
			}
			return true;
			/*switch(snapshot.getInputSpecification()) {
				case EOBJECT:
					if(matcherRoot instanceof EObject && root instanceof EObject) {
						if(matcherRoot != root) {
							engine.getLogger().logError("Existing snapshot model root (" + root + ") not equal to selected input (" + matcherRoot + ")!");
						}
					}
					break;
				case RESOURCE:
					if(matcherRoot instanceof Resource && root instanceof Resource) {
						Resource res = (Resource) matcherRoot;
						for (EObject eobj : res.getContents()) {
							if(!snapshot.getModelRoots().contains(eobj)) {
								engine.getLogger().logError("Existing snapshot model root not equal to selected input! Missing model root: " + eobj);
							}
						}
						for(EObject eobj : snapshot.getModelRoots()) {
							if(!res.getContents().contains(eobj)) {
								engine.getLogger().logError("Existing snapshot model root not equal to selected input! Missing snapshot root: " + eobj);
							}
						}
					}
					break;
				case RESOURCE_SET:
					if(matcherRoot instanceof ResourceSet && root instanceof ResourceSet) {
						ResourceSet set = (ResourceSet) matcherRoot;
						for (Resource res : set.getResources()) {
							for (EObject eobj : res.getContents()) {
								if(!snapshot.getModelRoots().contains(eobj)) {
									engine.getLogger().logError("Existing snapshot model root not equal to selected input!");
								}
							}
						}
					}
					break;
			}*/
		}
		return true;
	}
	
	private ResourceSet getResourceSetForNotifier(Notifier notifier) {
		if(notifier instanceof EObject) {
			Resource resource = ((EObject) notifier).eResource();
			if(resource != null) {
				return resource.getResourceSet();
			}
		} else if(notifier instanceof Resource) {
			return ((Resource) notifier).getResourceSet();
		} else if(notifier instanceof ResourceSet) {
			return (ResourceSet) notifier;
		}
		return null;
	}
}
