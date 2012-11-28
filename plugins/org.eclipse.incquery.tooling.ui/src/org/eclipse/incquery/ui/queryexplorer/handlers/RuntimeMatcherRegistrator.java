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

package org.eclipse.incquery.ui.queryexplorer.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.ui.queryexplorer.QueryExplorer;
import org.eclipse.incquery.ui.queryexplorer.content.flyout.FlyoutControlComposite;
import org.eclipse.incquery.ui.queryexplorer.content.flyout.IFlyoutPreferences;
import org.eclipse.incquery.ui.queryexplorer.content.matcher.MatcherTreeViewerRoot;
import org.eclipse.incquery.ui.queryexplorer.content.matcher.ObservablePatternMatcherRoot;
import org.eclipse.incquery.ui.queryexplorer.content.patternsviewer.PatternComponent;
import org.eclipse.incquery.ui.queryexplorer.content.patternsviewer.PatternComposite;
import org.eclipse.incquery.ui.queryexplorer.util.DatabindingUtil;
import org.eclipse.incquery.ui.queryexplorer.util.PatternRegistry;

import com.google.inject.Inject;

/**
 * Runnable unit of registering patterns in given file.
 * 
 * Note that if the work is implemented as a job, 
 * NullPointerException will occur when creating observables as the default realm will be null 
 * (because of non-ui thread).
 * 
 * @author Tamas Szabo
 *
 */
public class RuntimeMatcherRegistrator implements Runnable {

	private final IFile file;
	
	@Inject
	DatabindingUtil dbUtil;
	
	public RuntimeMatcherRegistrator(IFile file) {
		this.file = file;
	}

	@Override
	public void run() {
		QueryExplorer queryExplorerInstance = QueryExplorer.getInstance();
		if (queryExplorerInstance != null) {	
			MatcherTreeViewerRoot vr = queryExplorerInstance.getMatcherTreeViewerRoot();
			PatternComposite viewerInput = queryExplorerInstance.getPatternsViewerInput().getGenericPatternsRoot();
			List<Pattern> oldParsedModel = PatternRegistry.getInstance().getRegisteredPatternsForFile(file);
			PatternModel newParsedModel = dbUtil.parseEPM(file);
			
			//if no patterns were registered before, open the patterns viewer
			if (PatternRegistry.getInstance().isEmpty()) {
				FlyoutControlComposite flyout = queryExplorerInstance.getPatternsViewerFlyout();
				flyout.getPreferences().setState(IFlyoutPreferences.STATE_OPEN);
				//redraw();
				flyout.layout();
			}
			
			//UNREGISTERING PATTERNS
			
			List<Pattern> allActivePatterns = PatternRegistry.getInstance().getActivePatterns();
			//deactivate patterns within the given file
			PatternRegistry.getInstance().unregisterPatternModel(file);
			
			//unregister all active patterns from the roots and wipe the appropriate iq engine
			for (ObservablePatternMatcherRoot root : vr.getRoots()) {
				for (Pattern pattern : allActivePatterns) {
					root.unregisterPattern(pattern);
				}
				//final IncQueryEngine engine = EngineManager.getInstance().getIncQueryEngineIfExists(root.getNotifier());
				final IncQueryEngine engine = root.getKey().getEngine();
				if (engine!=null) {
					engine.wipe();
				}
			}
			
			//remove labels from pattern registry for the corresponding pattern model
			if (oldParsedModel != null) {
				for (Pattern pattern : oldParsedModel) {
					viewerInput.removeComponent(CorePatternLanguageHelper.getFullyQualifiedName(pattern));
				}
			}
			
			queryExplorerInstance.getPatternsViewerInput().getGenericPatternsRoot().purge();
			queryExplorerInstance.getPatternsViewer().refresh();
	
			//REGISTERING PATTERNS
			
			//registering patterns from file
			List<Pattern> newPatterns = PatternRegistry.getInstance().registerPatternModel(file, newParsedModel);
			allActivePatterns = PatternRegistry.getInstance().getActivePatterns();
			
			//now the active patterns also contain of the new patterns
			for (ObservablePatternMatcherRoot root : vr.getRoots()) {
				root.registerPattern(allActivePatterns.toArray(new Pattern[allActivePatterns.size()]));
			}
			
			//setting check states
			List<PatternComponent> components = new ArrayList<PatternComponent>();
			for (Pattern pattern : newPatterns) {
				PatternComponent component = viewerInput.addComponent(CorePatternLanguageHelper.getFullyQualifiedName(pattern));
				components.add(component);
			}
			//note that after insertion a refresh is necessary otherwise setting check state will not work
			queryExplorerInstance.getPatternsViewer().refresh();
			
			for (PatternComponent component : components) {
				queryExplorerInstance.getPatternsViewer().setChecked(component, true);
			}
			
			//it is enough to just call selection propagation for one pattern
			if (components.size() > 0) {
				components.get(0).getParent().propagateSelectionToTop(components.get(0));
			}
		}
	}
}
