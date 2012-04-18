package org.eclipse.viatra2.emf.incquery.queryexplorer.observable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class PatternMatcher {
	
	private List<PatternMatch> matches;
	private IncQueryMatcher<? extends IPatternMatch> matcher;
	private DeltaMonitor<? extends IPatternMatch> deltaMonitor;
	private Runnable processMatchesRunnable;
	private Map<IPatternMatch, PatternMatch> sigMap;
	private PatternMatcherRoot parent;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this); 
	private boolean generated;
	private String patternFqn;
	
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
	
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
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
		List<PatternMatch> oldValue = new ArrayList<PatternMatch>(matches);
		PatternMatch pm = new PatternMatch(this, match);
		this.sigMap.put(match, pm);
		this.matches.add(pm);
		this.propertyChangeSupport.firePropertyChange(MATCHES_ID, oldValue, matches);
	}
	
	private void removeMatch(IPatternMatch match) {
		List<PatternMatch> oldValue = new ArrayList<PatternMatch>(matches);
		this.matches.remove(this.sigMap.remove(match));
		this.propertyChangeSupport.firePropertyChange(MATCHES_ID, oldValue, matches);
	}

	public PatternMatcherRoot getParent() {
		return parent;
	}

	public String getText() {
		if (matcher == null) {
			return "Matcher could not be created for "+patternFqn+ (isGenerated() ? " (Generated)" : " (Runtime)");
		}
		else {
			return this.matcher.getPatternName() + (isGenerated() ? " (Generated)" : " (Runtime)");
		}
	}

	public static final String MATCHES_ID = "matches";
	public List<PatternMatch> getMatches() {
		return matches;
	}

	public void setMatches(List<PatternMatch> matches) {
		this.matches = matches;
	}

	public boolean isGenerated() {
		return generated;
	}
}
