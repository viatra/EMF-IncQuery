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

package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.flyout.FlyoutControlComposite;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.flyout.IFlyoutPreferences;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatcherRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternComponent;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DatabindingUtil;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.PatternRegistry;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.PatternsViewerModel;
import org.eclipse.viatra2.emf.incquery.runtime.api.EngineManager;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

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

	private IFile file;
	
	@Inject
	DatabindingUtil dbUtil;
	
	public RuntimeMatcherRegistrator(IFile file) {
		this.file = file;
	}

	@Override
	public void run() {
		if (QueryExplorer.getInstance() != null) {	
			MatcherTreeViewerRoot vr = QueryExplorer.getInstance().getMatcherTreeViewerRoot();
			PatternsViewerModel viewerInput = QueryExplorer.getInstance().getPatternsViewerModel();
			PatternModel oldParsedModel = PatternRegistry.getInstance().getPatternModelForFile(file);
			PatternModel newParsedModel = dbUtil.parseEPM(file);
			
			//if no patterns were registered before, open the patterns viewer
			if (PatternRegistry.getInstance().getPatterns().isEmpty()) {
				FlyoutControlComposite flyout = QueryExplorer.getInstance().getPatternsViewerFlyout();
				flyout.getPreferences().setState(IFlyoutPreferences.STATE_OPEN);
				//redraw();
				flyout.layout();
			}
			
			//UNREGISTERING PATTERNS
			
			List<Pattern> allActivePatterns = PatternRegistry.getInstance().getActivePatterns();
			//deactivate patterns within the given file
			PatternRegistry.getInstance().unregisterPatternModel(file);
			
			//unregister all active patterns from the roots and dispose the appropriate iq engine
			for (ObservablePatternMatcherRoot root : vr.getRoots()) {
				for (Pattern pattern : allActivePatterns) {
					root.unregisterPattern(pattern);
				}
				EngineManager.getInstance().getIncQueryEngine(root.getNotifier()).dispose();
			}
			
			//remove labels from pattern registry for the corresponding pattern model
			if (oldParsedModel != null) {
				for (Pattern pattern : oldParsedModel.getPatterns()) {
					viewerInput.removeComponent(CorePatternLanguageHelper.getFullyQualifiedName(pattern), false);
				}
			}
			
			QueryExplorer.getInstance().getPatternsViewer().refresh();
	
			//REGISTERING PATTERNS
			
			//registering patterns from file
			Set<Pattern> newPatterns = PatternRegistry.getInstance().registerPatternModel(file, newParsedModel);
			allActivePatterns = PatternRegistry.getInstance().getActivePatterns();
			
			//now the active patterns also contain of the new patterns
			for (Pattern pattern : allActivePatterns) {
				for (ObservablePatternMatcherRoot root : vr.getRoots()) {
					root.registerPattern(pattern);
				}
			}
			
			//setting check states
			List<PatternComponent> components = new ArrayList<PatternComponent>();
			for (Pattern pattern : newPatterns) {
				PatternComponent component = viewerInput.addComponent(CorePatternLanguageHelper.getFullyQualifiedName(pattern));
				components.add(component);
			}
			//note that after insertion a refresh is necessary otherwise setting check state will not work
			QueryExplorer.getInstance().getPatternsViewer().refresh();
			
			for (PatternComponent component : components) {
				QueryExplorer.getInstance().getPatternsViewer().setChecked(component, true);
			}
			
			//it is enough to just call selection propagation for one pattern
			if (components.size() > 0) {
				components.get(0).getParent().propagateSelectionToTop(components.get(0));
			}
		}
	}
}
