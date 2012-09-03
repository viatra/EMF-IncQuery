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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.preference.PreferenceConstants;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DatabindingUtil;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.PatternRegistry;
import org.eclipse.viatra2.emf.incquery.runtime.api.EngineManager;
import org.eclipse.viatra2.emf.incquery.runtime.api.GenericPatternMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.EngineTaintListener;
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
public class ObservablePatternMatcherRoot extends EngineTaintListener {

	private Map<String, ObservablePatternMatcher> matchers;
	private List<ObservablePatternMatcher> sortedMatchers;
	private MatcherTreeViewerRootKey key;
	
	private ILog logger = IncQueryGUIPlugin.getDefault().getLog(); 
	
	public ObservablePatternMatcherRoot(MatcherTreeViewerRootKey key) {
		matchers = new HashMap<String, ObservablePatternMatcher>();
		sortedMatchers = new LinkedList<ObservablePatternMatcher>();
		this.key = key;
		
		IncQueryEngine engine = key.getEngine();
		if(engine == null) {
		  key.setEngine(createEngine());
		}
		if (engine != null) {
			engine.getLogger().addAppender(this);
		}
	}
	
	private IncQueryEngine createEngine() {
		try {
			IncQueryEngine engine = EngineManager.getInstance().createUnmanagedIncQueryEngine(key.getNotifier());
			return engine;
		} catch (IncQueryException e) {
			logger.log(new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, "Could not retrieve IncQueryEngine for "+key.getNotifier(), e));
			return null;
		}
	}
	
	public void addMatcher(IncQueryMatcher<? extends IPatternMatch> matcher, String patternFqn, boolean generated, String exceptionMessage) {
		//This cast could not be avoided because later the filtered delta monitor will need the base IPatternMatch
		@SuppressWarnings("unchecked")
		ObservablePatternMatcher pm = new ObservablePatternMatcher(this, (IncQueryMatcher<IPatternMatch>) matcher, patternFqn, generated, exceptionMessage);
		this.matchers.put(patternFqn, pm);
		
		//generated matchers are inserted in front of the list
		if (generated) {
			this.sortedMatchers.add(0, pm);
		}
		//generic matchers are inserted in the list according to the order in the eiq file
		else {
			this.sortedMatchers.add(pm);
		}
		QueryExplorer.getInstance().getMatcherTreeViewer().refresh(this);
	}
	
	public void removeMatcher(String patternFqn) {
		//if the pattern is first deactivated then removed, than the matcher corresponding matcher is disposed
		ObservablePatternMatcher matcher = this.matchers.get(patternFqn);
		if (matcher != null) {
			this.sortedMatchers.remove(matcher);
			matcher.dispose();
			this.matchers.remove(patternFqn);
			QueryExplorer.getInstance().getMatcherTreeViewer().refresh(this);
		}
	}
	
	public static final String MATCHERS_ID = "matchers";
	
	public List<ObservablePatternMatcher> getMatchers() {
		return sortedMatchers;
	}
	
	public String getText() {
		return key.toString();
	}
	
	public void dispose() {
		for (ObservablePatternMatcher pm : this.matchers.values()) {
			pm.dispose();
		}
		IncQueryEngine engine = key.getEngine();//createEngine();
		if (engine != null) {
			engine.getLogger().removeAppender(this);
		}
	}
	
	public boolean isTainted() {
		IncQueryEngine engine = key.getEngine();//createEngine();
		return (engine == null) ? true : engine.isTainted();
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
	
	@Override
	public void engineBecameTainted() {
		for (ObservablePatternMatcher matcher : this.matchers.values()) {
			matcher.stopMonitoring();
		}
	}

	public void registerPattern(final Pattern... patterns) {
		boolean wildcardMode = IncQueryGUIPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.WILDCARD_MODE);
		IncQueryEngine engine;
		try {
			//engine = EngineManager.getInstance().getIncQueryEngine(getNotifier());
		  engine = key.getEngine();
			try {
				engine.setWildcardMode(wildcardMode);
			} catch (IllegalStateException ex) {
				// could not set wildcard mode
			}
			
			if (engine.getBaseIndex().isInWildcardMode()) {
				addMatchersForPatterns(patterns);
			} else {
				engine.getBaseIndex().coalesceTraversals(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						addMatchersForPatterns(patterns);
						return null;
					}
				});
			}	
			
		} catch (IncQueryException ex) {
			logger.log(new Status(IStatus.ERROR,
					IncQueryGUIPlugin.PLUGIN_ID,
					"Cannot initialize pattern matcher engine.", ex));
		} catch (InvocationTargetException e) {
			logger.log(new Status(IStatus.ERROR,
					IncQueryGUIPlugin.PLUGIN_ID,
					"Error during pattern matcher construction: " + e.getCause().getMessage(), e.getCause()));
		}
	}

	private void addMatchersForPatterns(Pattern... patterns) {
		for (Pattern pattern : patterns) {
			IncQueryMatcher<? extends IPatternMatch> matcher = null;
			boolean isGenerated = PatternRegistry.getInstance().isGenerated(pattern);
			String message = null;
			try {
				if (isGenerated) {
					matcher = DatabindingUtil.getMatcherFactoryForGeneratedPattern(pattern).getMatcher(key.getEngine());
				}
				else {
					matcher = new GenericPatternMatcher(pattern, key.getEngine());
				}
			} catch (Exception e) {
				logger.log(new Status(IStatus.ERROR,
						IncQueryGUIPlugin.PLUGIN_ID,
						"Cannot initialize pattern matcher for pattern "
								+ CorePatternLanguageHelper.getFullyQualifiedName(pattern), e));
				matcher = null;
				message = (e instanceof IncQueryException) ? 
					((IncQueryException)e).getShortMessage() : e.getMessage();
			} 

			addMatcher(matcher, CorePatternLanguageHelper.getFullyQualifiedName(pattern), isGenerated, message);
		}
	}
	
	public void unregisterPattern(Pattern pattern) {
		removeMatcher(CorePatternLanguageHelper.getFullyQualifiedName(pattern));
	}
}
