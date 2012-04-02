package org.eclipse.viatra2.emf.incquery.validation.runtime;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.misc.DeltaMonitor;

public class ConstraintAdapter<T extends IPatternMatch> {

	private Map<T, ConstraintViolation<T>> constraintViolations;
	private IncQueryMatcher<T> matcher;
	private Runnable processMatchesRunnable;
	private DeltaMonitor<T> deltaMonitor;
	private Constraint<T> constraint;
	
	public ConstraintAdapter(Constraint<T> constraint, Notifier notifier) {
		this.constraint = constraint;
		this.matcher = constraint.getMatcherFactory().getMatcher(notifier);
		this.constraintViolations = new HashMap<T, ConstraintViolation<T>>();
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

	protected void processLostMatches(Collection<T> matchLostEvents) {
		for (T match : matchLostEvents) {
			ConstraintViolation<T> cv = constraintViolations.remove(match);
			if (cv != null) {
				cv.dispose();
			}
		}
	}

	protected void processNewMatches(Collection<T> matchFoundEvents) {
		for (T match : matchFoundEvents) {
			ConstraintViolation<T> cv = new ConstraintViolation<T>(this, match);
			constraintViolations.put(match, cv);
		}
	}
	
	public Constraint<T> getConstraint() {
		return this.constraint;
	}
	
}
