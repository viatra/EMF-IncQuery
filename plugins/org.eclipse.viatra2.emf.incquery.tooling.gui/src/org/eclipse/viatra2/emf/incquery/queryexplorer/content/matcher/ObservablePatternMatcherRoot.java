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

package org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Each IEditingDomainProvider will be associated a PatternMatcherRoot element in the tree viewer.
 * PatterMatcherRoots are indexed with a ViewerRootKey.
 * 
 * It's children element will be PatterMatchers.
 *  
 * @author Tamas Szabo
 *
 */
public class ObservablePatternMatcherRoot {

	private Map<String, ObservablePatternMatcher> matchers;
	private MatcherTreeViewerRootKey key;
	
	private ILog logger = IncQueryGUIPlugin.getDefault().getLog(); 
	
	public ObservablePatternMatcherRoot(MatcherTreeViewerRootKey key) {
		matchers = new HashMap<String, ObservablePatternMatcher>();
		this.key = key;
	}
	
	public void addMatcher(IncQueryMatcher<? extends IPatternMatch> matcher, String patternFqn, boolean generated) {
		//This cast could not be avoided because later the filtered delta monitor will need the base IPatternMatch
		@SuppressWarnings("unchecked")
		ObservablePatternMatcher pm = new ObservablePatternMatcher(this, (IncQueryMatcher<IPatternMatch>) matcher, patternFqn, generated);
		this.matchers.put(patternFqn, pm);
		QueryExplorer.getInstance().getMatcherTreeViewer().refresh(this);
	}
	
	public void removeMatcher(String patternFqn) {
		//if the pattern is first deactivated then removed, than the matcher corresponding matcher is disposed
		ObservablePatternMatcher matcher = this.matchers.get(patternFqn);
		if (matcher != null) {
			this.matchers.get(patternFqn).dispose();
			this.matchers.remove(patternFqn);		
			QueryExplorer.getInstance().getMatcherTreeViewer().refresh(this);
		}
	}
	
	public static final String MATCHERS_ID = "matchers";
	
	public List<ObservablePatternMatcher> getMatchers() {
		return new ArrayList<ObservablePatternMatcher>(matchers.values());
	}
	
	public String getText() {
		return key.toString();
	}
	
	public void dispose() {
		for (ObservablePatternMatcher pm : this.matchers.values()) {
			pm.dispose();
		}
	}
	
	public MatcherTreeViewerRootKey getKey() {
		return key;
	}
	
	public IEditorPart getEditorPart() {
		return this.key.getEditor();
	}
	
	public Notifier getNotifier() {
		return this.key.getNotifier();
	}
	
	public void registerPattern(Pattern pattern) {
		IncQueryMatcher<GenericPatternMatch> matcher = null;

		try {
			matcher = new GenericPatternMatcher(pattern, key.getNotifier());
		}
		catch (IncQueryRuntimeException e) {
			logger.log(new Status(IStatus.ERROR,
					IncQueryGUIPlugin.PLUGIN_ID,
					"Cannot initialize pattern matcher for pattern "
							+ pattern.getName(), e));
			matcher = null;
		}

		addMatcher(matcher, CorePatternLanguageHelper.getFullyQualifiedName(pattern), false);
	}
	
	public void unregisterPattern(Pattern pattern) {
		removeMatcher(CorePatternLanguageHelper.getFullyQualifiedName(pattern));
	}
}
