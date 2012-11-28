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
import org.eclipse.incquery.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.patternlanguage.emf.core.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;

/**
 * Each IEditingDomainProvider will be associated a PatternMatcherRoot element in the tree viewer.
 * PatterMatcherRoots are indexed with a ViewerRootKey.
 * 
 * It's children element will be PatterMatchers.
 *  
 * @author Tamas Szabo
 *
 */
public class PatternMatcherRoot {

	private Map<String, PatternMatcher> matchers;
	private MatcherTreeViewerRootKey key;
	
	private ILog logger = IncQueryGUIPlugin.getDefault().getLog(); 
	
	public PatternMatcherRoot(MatcherTreeViewerRootKey key) {
		matchers = new HashMap<String, PatternMatcher>();
		this.key = key;
	}
	
	public void addMatcher(IncQueryMatcher<? extends IPatternMatch> matcher, String patternFqn, boolean generated) {
		PatternMatcher pm = new PatternMatcher(this, matcher, patternFqn, generated);
		this.matchers.put(patternFqn, pm);
		if (QueryExplorer.getInstance() != null) {
			QueryExplorer.getInstance().getMatcherTreeViewer().refresh(this);
		}
	}
	
	public void removeMatcher(String patternFqn) {
		this.matchers.get(patternFqn).dispose();
		this.matchers.remove(patternFqn);
		if (QueryExplorer.getInstance() != null) {
			QueryExplorer.getInstance().getMatcherTreeViewer().refresh(this);
		}
	}
	
	public static final String MATCHERS_ID = "matchers";
	
	public List<PatternMatcher> getMatchers() {
		return new ArrayList<PatternMatcher>(matchers.values());
	}
	
	public String getText() {
		return key.toString();
	}
	
	public void dispose() {
		for (PatternMatcher pm : this.matchers.values()) {
			pm.dispose();
		}
	}
	
	public IEditorPart getEditorPart() {
		return this.key.getEditorPart();
	}
	
//	public void registerPatternModelFromFile(IFile file, PatternModel pm) {	
//		if (!runtimeMatcherRegistry.containsKey(file)) {
//			Set<String> _patterns = new HashSet<String>();
//			EList<Pattern> patterns = pm.getPatterns();
//			IncQueryMatcher<GenericPatternMatch> matcher = null;
//				
//			for (Pattern pattern : patterns) {
//				try {
//					matcher = new GenericPatternMatcher(pattern, key.getNotifier());
//				}
//				catch (IncQueryRuntimeException e) {
//					logger.log(new Status(IStatus.ERROR,
//							IncQueryGUIPlugin.PLUGIN_ID,
//							"Cannot initialize pattern matcher for pattern "
//									+ pattern.getName(), e));
//					matcher = null;
//				}
//				_patterns.add(CorePatternLanguageHelper.getFullyQualifiedName(pattern));
//				addMatcher(matcher, CorePatternLanguageHelper.getFullyQualifiedName(pattern), false);
//			}
//				
//			runtimeMatcherRegistry.put(file, _patterns);
//		}
//	}
	
	public void registerPattern(Pattern pattern) {
		IncQueryMatcher<GenericPatternMatch> matcher = null;

		try {
			matcher = new GenericPatternMatcher(pattern, key.getEngine());
		}
		catch (IncQueryException e) {
			logger.log(new Status(IStatus.ERROR,
					IncQueryGUIPlugin.PLUGIN_ID,
					"Cannot initialize pattern matcher for pattern "
							+ pattern.getName(), e));
			matcher = null;
		}

		addMatcher(matcher, CorePatternLanguageHelper.getFullyQualifiedName(pattern), false);
	}
	
//	public void unregisterPatternModelFromFile(IFile file) {
//		Set<String> setTmp = runtimeMatcherRegistry.get(file);
//		if (setTmp != null) {
//			for (String pattern : setTmp) {
//				removeMatcher(pattern);
//			}
//			
//			runtimeMatcherRegistry.remove(file);
//		}
//	}
	
	public void unregisterPattern(Pattern pattern) {
		removeMatcher(CorePatternLanguageHelper.getFullyQualifiedName(pattern));
	}
}
