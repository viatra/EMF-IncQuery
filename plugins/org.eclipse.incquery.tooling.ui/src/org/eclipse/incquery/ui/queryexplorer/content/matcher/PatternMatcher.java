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

package org.eclipse.incquery.ui.queryexplorer.content.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.rete.misc.DeltaMonitor;
import org.eclipse.incquery.ui.queryexplorer.QueryExplorer;

/**
 * A PatternMatcher is associated to every IncQueryMatcher which is annotated with PatternUI annotation.
 * These elements will be the children of the top level elements in the treeviewer.
 * 
 * @author Tamas Szabo
 *
 */
public class PatternMatcher {
	
	private final List<PatternMatch> matches;
	private final IncQueryMatcher<? extends IPatternMatch> matcher;
	private DeltaMonitor<? extends IPatternMatch> deltaMonitor;
	private Runnable processMatchesRunnable;
	private Map<IPatternMatch, PatternMatch> sigMap;
	private final PatternMatcherRoot parent;
	private final boolean generated;
	private final String patternFqn;
	
	public PatternMatcher(PatternMatcherRoot parent, IncQueryMatcher<? extends IPatternMatch> matcher, String patternFqn, boolean generated) {
		this.parent = parent;
		this.patternFqn = patternFqn;
		this.matches = new ArrayList<PatternMatch>();
		this.matcher = matcher;
		this.generated = generated;
		
		if (matcher != null) {
			this.sigMap = new HashMap<IPatternMatch, PatternMatch>();
			this.deltaMonitor = this.matcher.newDeltaMonitor(true);
			this.processMatchesRunnable = new Runnable() {		
				@Override
				public void run() {
					processNewMatches(deltaMonitor.matchFoundEvents);
					processLostMatches(deltaMonitor.matchLostEvents);
					deltaMonitor.clear();
				}
			};
			
			this.matcher.addCallbackAfterUpdates(processMatchesRunnable);
			this.processMatchesRunnable.run();
		}
	}
	
	/**
	 * Call this method to remove the callback handler from the delta monitor of the matcher.
	 */
	public void dispose() {
		if (matcher != null) {
			for (PatternMatch pm : matches) {
				pm.dispose();
			}
			this.matcher.removeCallbackAfterUpdates(processMatchesRunnable);
			processMatchesRunnable = null;
		}
	}
	
	private void processNewMatches(Collection<? extends IPatternMatch> matches) {
		for (IPatternMatch s : matches) {
			addMatch(s);
		}
	}

	private void processLostMatches(Collection<? extends IPatternMatch> matches) {
		for (IPatternMatch s : matches) {
			removeMatch(s);
		}
	}
	
	private void addMatch(IPatternMatch match) {
		PatternMatch pm = new PatternMatch(this, match);
		this.sigMap.put(match, pm);
		this.matches.add(pm);
		if (QueryExplorer.getInstance() != null) {
			QueryExplorer.getInstance().getMatcherTreeViewer().refresh(this);
		}
	}
	
	private void removeMatch(IPatternMatch match) {
		this.matches.remove(this.sigMap.remove(match));
		if (QueryExplorer.getInstance() != null) {
			QueryExplorer.getInstance().getMatcherTreeViewer().refresh(this);
		}
	}

	public PatternMatcherRoot getParent() {
		return parent;
	}

	public String getText() {
		String isGeneratedString = isGenerated() ? " (Generated)" : " (Runtime)";
		if (matcher == null) {
			return String.format("Matcher could not be created for pattern '%s' %s", patternFqn, isGeneratedString);
		}
		else {
			String matchString;
			switch (matches.size()){
			case 0: 
				matchString = "No matches";
				break;
			case 1:
				matchString = "1 match";
				break;
			default:
				matchString = String.format("%d matches", matches.size());
			}
			//return this.matcher.getPatternName() + (isGeneratedString +" [size of matchset: "+matches.size()+"]");
			return String.format("%s - %s %s", matcher.getPatternName(), matchString, isGeneratedString);
		}
	}

	public static final String MATCHES_ID = "matches";
	public List<PatternMatch> getMatches() {
		return matches;
	}

	public boolean isGenerated() {
		return generated;
	}
	
	public boolean isCreated() {
		return matcher != null;
	}
}
