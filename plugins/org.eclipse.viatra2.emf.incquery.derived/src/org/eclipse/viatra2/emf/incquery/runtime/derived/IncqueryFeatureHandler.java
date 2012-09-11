/*******************************************************************************
 * Copyright (c) 2004-2011 Abel Hegedus and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.runtime.derived;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.misc.DeltaMonitor;

/**
 * @author Abel Hegedus
 *
 * FIXME write AggregateHandler if any EDataType should be allowed 
 * TODO notifications could be static final? to ensure message ordering
 * TODO one delta monitor per matcher should be enough if only matches corresponding to the given object are removed 
 * 
 */
@SuppressWarnings("rawtypes")
public class IncqueryFeatureHandler {

	private final IncQueryMatcher<IPatternMatch> matcher;
	private final DeltaMonitor<IPatternMatch> dm;
	private final Runnable processMatchesRunnable;
	private final InternalEObject source;
	private final EStructuralFeature feature;
	private final String sourceParamName;
	private final String targetParamName;
	private FeatureKind kind;
	
	private Object updateMemory;
	private int counterMemory = 0;
	private Object singleRefMemory = null;
	private boolean keepCache = true;
	
	private final List<ENotificationImpl> notifications = new ArrayList<ENotificationImpl>();
	
	/* could use EObjectEList or similar to have notifications handled by EMF,
	 * but notification sending must be delayed in order to avoid infinite notification loop
   */ 
	private final Collection<Object> manyRefMemory = new HashSet<Object>();
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public IncqueryFeatureHandler(InternalEObject source, EStructuralFeature feature, final IncQueryMatcher matcher, String sourceParamName, String targetParamName) {
		this.source = source;
		this.feature = feature;
		if(feature.isMany() && targetParamName != null) {
			kind = FeatureKind.MANY_REFERENCE;
		} else {
			kind = FeatureKind.SINGLE_REFERENCE;
		}
		this.matcher = matcher;
		this.sourceParamName = sourceParamName;
		this.targetParamName = targetParamName;
		if(matcher.getPositionOfParameter(sourceParamName) == null) {
			matcher.getEngine().getLogger().error("[IncqueryFeatureHandler] Source parameter " + sourceParamName + " not found!");
		}
		if(targetParamName != null && matcher.getPositionOfParameter(targetParamName) == null) {
			matcher.getEngine().getLogger().error("[IncqueryFeatureHandler] Target parameter " + targetParamName + " not found!");
		}
		IPatternMatch partialMatch = matcher.newEmptyMatch();
		partialMatch.set(sourceParamName, source);
		this.dm = matcher.newFilteredDeltaMonitor(true, partialMatch);
		this.processMatchesRunnable = new Runnable() {		
			@Override
			public void run() {
				// after each model update, check the delta monitor
				// FIXME should be: after each complete transaction, check the delta monitor
				try {
					updateMemory = null;
					dm.matchFoundEvents.removeAll( processNewMatches(dm.matchFoundEvents) );
					dm.matchLostEvents.removeAll( processLostMatches(dm.matchLostEvents) );	
					checkUnhandledNewMatch();
					sendNextNotfication();
				} catch (IncQueryException e) {
					matcher.getEngine().getLogger().error("[IncqueryFeatureHandler] Exception during update: " + e.getMessage(), e);
				}
				
			}
		};
	}
	
	private void sendNextNotfication() {
		while(!notifications.isEmpty()) {
			ENotificationImpl remove = notifications.remove(0);
			//matcher.getEngine().getLogger().logError(this + " : " +remove.toString());
			source.eNotify(remove);
		}
	}
	
	/** 
	 * 
	 * @param source
	 * @param feature
	 * @param matcher
	 * @param sourceParamName
	 */
	public IncqueryFeatureHandler(InternalEObject source, EStructuralFeature feature, final IncQueryMatcher matcher, String sourceParamName, String targetParamName, FeatureKind kind) {
		this(source, feature, matcher, sourceParamName, targetParamName);
		this.kind = kind;
		if((targetParamName == null) != (kind == FeatureKind.COUNTER)) {
			matcher.getEngine().getLogger().error("[IncqueryFeatureHandler] Invalid configuration (no targetParamName needed for Counter)!");
				return;
		}
		if(kind == FeatureKind.SUM && !(feature instanceof EAttribute)) {
			matcher.getEngine().getLogger().error("[IncqueryFeatureHandler] Invalid configuration (Aggregate can be used only with EAttribute)!");
		}
	}
	
	/**
	 * 
	 */
	public IncqueryFeatureHandler(InternalEObject source, EStructuralFeature feature, IncQueryMatcher matcher, String sourceParamName, String targetParamName, FeatureKind kind, boolean keepCache) {
		this(source, feature, matcher, sourceParamName, targetParamName,kind);
		this.keepCache = keepCache;
	}

	
	/**
	 * Call this once to start monitoring validation problems.
	 */
	public void startMonitoring() {
		matcher.addCallbackAfterUpdates(processMatchesRunnable);
		processMatchesRunnable.run();
	}
	
	public Object getValue() {
		switch(kind) {
			case SUM:  // fall-through
			case COUNTER:
				return getIntValue();
			case SINGLE_REFERENCE:
				return getSingleReferenceValue();
			case MANY_REFERENCE:
				return getManyReferenceValue();
			case ITERATION:
				return getValueIteration();
		}
		return null;
	}
	
	public int getIntValue() {
		return counterMemory;			
	}
	
	public Object getSingleReferenceValue() {
		if(keepCache) {
			return singleRefMemory;
		} else {
			IPatternMatch match = matcher.newEmptyMatch();
			match.set(sourceParamName, source);
			if(matcher.countMatches(match) > 1) {
				matcher.getEngine().getLogger().warn("[IncqueryFeatureHandler] Single reference derived feature has multiple possible values, returning one arbitrary value");
			}
			IPatternMatch patternMatch = matcher.getOneArbitraryMatch(match);
			if(patternMatch != null) {
				return patternMatch.get(targetParamName);
			} else {
				return null;
			}
		}
	}
	
	public Collection<Object> getManyReferenceValue(){
		if(keepCache) {
			return manyRefMemory;
		} else {
			IPatternMatch match = matcher.newEmptyMatch();
			match.set(sourceParamName, source);
			return matcher.getAllValues(targetParamName, match);
		}
	}

	/**
	 * @param singleRefMemory the singleRefMemory to set
	 */
	private void setSingleRefMemory(Object singleRefMemory) {
		if(keepCache) {
			this.singleRefMemory = singleRefMemory;
		}
	}
	
	private void addToManyRefMemory(Object added) {
		if(keepCache) {
			manyRefMemory.add(added);
		}
	}
	
	private void removeFromManyRefMemory(Object removed) {
		if(keepCache) {
			manyRefMemory.remove(removed);
		}
	}

	public EList getManyReferenceValueAsEList() {
		Collection<Object> values = getManyReferenceValue();
		if (values.size() > 0) {
			return new EcoreEList.UnmodifiableEList(source,
					feature,
					values.size(),
					values.toArray());
		}
		else {
			return new EcoreEList.UnmodifiableEList(source,
					feature,
					0,
					null);
		}
	}
	
	private Collection<IPatternMatch> processNewMatches(Collection<IPatternMatch> signatures) throws IncQueryException {
		List<IPatternMatch> processed = new ArrayList<IPatternMatch>();
	    for (IPatternMatch signature : signatures) {
	      if (kind == FeatureKind.ITERATION) {
	        ENotificationImpl notification = newMatchIteration(signature);
	        if (notification != null) {
	          notifications.add(notification);
	        }
	      } else {
	        Object target = signature.get(targetParamName);
	        if (target != null) {
	          if (kind == FeatureKind.SUM) {
	            increaseCounter((Integer) target);
	          } else {
	            if (feature.isMany()) {
	              notifications
	                  .add(new ENotificationImpl(source, Notification.ADD, feature, null, target));
	              addToManyRefMemory(target);
	            } else {
	              if (updateMemory != null) {
	                matcher.getEngine().getLogger().error(
	                    "[IncqueryFeatureHandler] Space-time continuum breached (should never happen)");
	              } else {
	                // must handle later (either in lost matches or after that)
	                updateMemory = target;
	              }
	            }
	          }
	        } else if (kind == FeatureKind.COUNTER) {
	          increaseCounter(1);
	        }
	      }
	      processed.add(signature);
	    }
		return processed;
	}
	
	/**
	 * @throws CoreException
	 */
	private void increaseCounter(int delta) throws IncQueryException {
		if(counterMemory <= Integer.MAX_VALUE-delta) {
			int tempMemory = counterMemory+delta;
			notifications.add(
					new ENotificationImpl(source, Notification.SET,	feature, counterMemory, tempMemory));
			counterMemory = tempMemory;
		} else {
		  throw new IncQueryException(String.format("The counter of %s for feature %s reached the maximum value of int!",source, feature),"Counter reached maximum value of int");
		}
	}

  private Collection<IPatternMatch> processLostMatches(Collection<IPatternMatch> signatures) throws IncQueryException {
    List<IPatternMatch> processed = new ArrayList<IPatternMatch>();
    for (IPatternMatch signature : signatures) {
      if (kind == FeatureKind.ITERATION) {
        ENotificationImpl notification = lostMatchIteration(signature);
        if (notification != null) {
          notifications.add(notification);
        }
      } else {
        Object target = signature.get(targetParamName);
        if (target != null) {
          if (kind == FeatureKind.SUM) {
            decreaseCounter((Integer) target);
          } else {
            if (feature.isMany()) {
              notifications.add(new ENotificationImpl(source, Notification.REMOVE, feature, target,
                  null));
              removeFromManyRefMemory(target);
            } else {
              if (updateMemory != null) {
                notifications.add(new ENotificationImpl(source, Notification.SET, feature, target,
                    updateMemory));
                setSingleRefMemory(updateMemory);
                updateMemory = null;
              } else {
                notifications.add(new ENotificationImpl(source, Notification.SET, feature, target,
                    null));
                setSingleRefMemory(null);
              }
            }
          }
        } else {
          if (kind == FeatureKind.COUNTER) {
            decreaseCounter(1);
          }
        }
      }
      processed.add(signature);
    }
    return processed;
  }
	
	/**
	 * Called each time when a new match is found for Iteration kind
	 * 
	 * @param signature
	 * @return notification to be sent, if one is necessary
	 */
	protected ENotificationImpl newMatchIteration(IPatternMatch signature) {
		throw new UnsupportedOperationException("Iteration derived feature handlers must override newMatchIteration");
	}

	/**
	 * Called each time when a match is lost for Iteration kind
	 * 
	 * @param signature
	 * @return notification to be sent, if one is necessary
	 */
	protected ENotificationImpl lostMatchIteration(IPatternMatch signature) {
		throw new UnsupportedOperationException("Iteration derived feature handlers must override oldMatchIteration");
	}
	/**
	 * Called when getValue method is called for Iteration kind
	 * 
	 * @return the value of the feature
	 */
	protected Object getValueIteration() {
		throw new UnsupportedOperationException("Iteration derived feature handlers must override getValueIteration");
	}
	
	/**
	 * @throws CoreException
	 */
	private void decreaseCounter(int delta) throws IncQueryException {
		if(counterMemory >= delta) {
			int tempMemory = counterMemory-delta;
			notifications.add(
					new ENotificationImpl(source, Notification.SET,
					feature, counterMemory, tempMemory));
			counterMemory = tempMemory;
		} else {
		  throw new IncQueryException(String.format("The counter of %s for feature %s cannot go below zero!",source, feature), "Counter cannot go below zero");
		}
	}
	
	private void checkUnhandledNewMatch() {
		if(updateMemory != null) {
			notifications.add(
					new ENotificationImpl(source, Notification.SET,
					feature, null, updateMemory));
			setSingleRefMemory(updateMemory);
		}
	}
}
