package org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.misc.DeltaMonitor;

/**
 * A PatternMatcher is associated to every IncQueryMatcher which is annotated with PatternUI annotation.
 * These elements will be the children of the top level elements in the treeviewer.
 * 
 * @author Tamas Szabo
 *
 */
public class ObservablePatternMatcher {
	
	private List<ObservablePatternMatch> matches;
	private IncQueryMatcher<? extends IPatternMatch> matcher;
	private DeltaMonitor<? extends IPatternMatch> deltaMonitor;
	private Runnable processMatchesRunnable;
	private Map<IPatternMatch, ObservablePatternMatch> sigMap;
	private ObservablePatternMatcherRoot parent;
	private boolean generated;
	private String patternFqn;
	
	public ObservablePatternMatcher(ObservablePatternMatcherRoot parent, IncQueryMatcher<? extends IPatternMatch> matcher, String patternFqn, boolean generated) {
		this.parent = parent;
		this.patternFqn = patternFqn;
		this.matches = new ArrayList<ObservablePatternMatch>();
		this.matcher = matcher;
		this.generated = generated;
		
		if (matcher != null) {
			this.sigMap = new HashMap<IPatternMatch, ObservablePatternMatch>();
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
			for (ObservablePatternMatch pm : matches) {
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
		ObservablePatternMatch pm = new ObservablePatternMatch(this, match);
		this.sigMap.put(match, pm);
		this.matches.add(pm);
		QueryExplorer.getInstance().getMatcherTreeViewer().refresh(this);
	}
	
	private void removeMatch(IPatternMatch match) {
		this.matches.remove(this.sigMap.remove(match));
		QueryExplorer.getInstance().getMatcherTreeViewer().refresh(this);
	}

	public ObservablePatternMatcherRoot getParent() {
		return parent;
	}
	
	public IncQueryMatcher<? extends IPatternMatch> getMatcher() {
		return matcher;
	}
	
	public String getPatternName() {
		return patternFqn;
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
	public List<ObservablePatternMatch> getMatches() {
		return matches;
	}

	public boolean isGenerated() {
		return generated;
	}
	
	public boolean isCreated() {
		return matcher != null;
	}
}
