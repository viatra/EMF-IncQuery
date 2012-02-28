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
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.misc.DeltaMonitor;

/**
 * @author Abel Hegedus
 *
 * FIXME write AggregateHandler if any EDataType should be allowed 
 * TODO notifications could be static final? to ensure message ordering
 * TODO one delta monitor per matcher should be enough if only matches corresponding to the given object are removed 
 * 
 */
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
	
	private final List<ENotificationImpl> notifications = new ArrayList<ENotificationImpl>();
	
	/* could use EObjectEList or similar to have notifications handled by EMF,
	 * but notification sending should be delayed in order to avoid infinite notification loop
   */ 
	private final Collection<Object> manyRefMemory = new HashSet<Object>();
	
	public enum FeatureKind{
		AGGREGATE, COUNTER, SINGLE_REFERENCE, MANY_REFERENCE
	}
	
	/**
	 * 
	 */
	public IncqueryFeatureHandler(InternalEObject source, EStructuralFeature feature, IncQueryMatcher matcher, String sourceParamName, String targetParamName) {
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
		this.dm = matcher.newDeltaMonitor(true);
		this.processMatchesRunnable = new Runnable() {		
			@Override
			public void run() {
				// after each model update, check the delta monitor
				// FIXME should be: after each complete transaction, check the delta monitor
				try {
					System.out.print("");
					updateMemory = null;
					dm.matchFoundEvents.removeAll( processNewMatches(dm.matchFoundEvents) );
					dm.matchLostEvents.removeAll( processLostMatches(dm.matchLostEvents) );	
					checkUnhandledNewMatch();
					sendNextNotfication();
				} catch (CoreException e) {
					e.printStackTrace();
				}
				
			}
		};
	}
	
	private void sendNextNotfication() {
		while(!notifications.isEmpty()) {
			source.eNotify(notifications.remove(0));
		}
	}
	
	/** 
	 * 
	 * @param source
	 * @param feature
	 * @param matcher
	 * @param sourceParamName
	 */
	public IncqueryFeatureHandler(InternalEObject source, EStructuralFeature feature, IncQueryMatcher matcher, String sourceParamName, String targetParamName, FeatureKind kind) {
		this(source, feature, matcher, sourceParamName, targetParamName);
		this.kind = kind;
		if((targetParamName == null) != (kind == FeatureKind.COUNTER)) {
				System.err.println("Invalid configuration (no targetParamName needed for Counter)!");
				return;
		}
		if(kind == FeatureKind.AGGREGATE && !(feature instanceof EAttribute)) {
			System.err.println("Invalid configuration (Aggregate can be used only with EAttribute)!");
		}
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
			case AGGREGATE:  // fall-through
			case COUNTER:
				return counterMemory;
			case SINGLE_REFERENCE:
				return singleRefMemory;
			case MANY_REFERENCE:
				return manyRefMemory;
		}
		return null;
	}
	
	public int getIntValue() {
		return counterMemory;
	}
	
	public Object getSingleReferenceValue() {
		return singleRefMemory;
	}
	
	public Collection<Object> getManyReferenceValue(){
		return manyRefMemory;
	}
	
	private Collection<IPatternMatch> processNewMatches(Collection<IPatternMatch> signatures) throws CoreException {
		Vector<IPatternMatch> processed = new Vector<IPatternMatch>();
		for (IPatternMatch signature : signatures) {
			if(source.equals(signature.get(sourceParamName))) {
				Object target = signature.get(targetParamName);
				if(target != null) {
					if(feature.isMany()) {
						if(kind == FeatureKind.AGGREGATE) {
							//if(aggregateMemory != null) {
								// TODO update Aggregate
							increaseCounter((Integer) target);
							//}
						} else {
							//source.eNotify(
							notifications.add(
									new ENotificationImpl((InternalEObject) source, Notification.ADD,	feature, null, target));
							manyRefMemory.add(target);
						}
					} else {
						if(updateMemory != null) {
							System.err.println("Space-time continuum breached (should never happen)");
							//source.eNotify(new ENotificationImpl((InternalEObject) source, Notification.SET,
							//		feature, updateMemory, signature.get(targetParamName)));
						} else {
							// must handle later (either in lost matches or after that)
							updateMemory = target;
						}
					}
				} else {
					if(kind == FeatureKind.COUNTER) {
						increaseCounter(1);
					}
				}
			}
			processed.add(signature);
		}
		return processed;
	}

	/**
	 * @throws CoreException
	 */
	private void increaseCounter(int delta) throws CoreException {
		if(counterMemory <= Integer.MAX_VALUE-delta) {
			int tempMemory = counterMemory+delta;
			//source.eNotify(
			notifications.add(
					new ENotificationImpl((InternalEObject) source, Notification.SET,	feature, counterMemory, tempMemory));
			counterMemory = tempMemory;
		} else {
			throw new CoreException(new Status(IStatus.ERROR, null, "Counter reached maximum value of Long"));
		}
	}

	private Collection<IPatternMatch> processLostMatches(Collection<IPatternMatch> signatures) throws CoreException {
		Vector<IPatternMatch> processed = new Vector<IPatternMatch>();
		for (IPatternMatch signature : signatures) {
			if(source.equals(signature.get(sourceParamName))) {
				Object target = signature.get(targetParamName);
				if(target != null) {
					if(feature.isMany()) {
						if(kind == FeatureKind.AGGREGATE) {
							//if(aggregateMemory != null) {
								// TODO update Aggregate
							decreaseCounter((Integer) target);
							//}
						} else {
							//source.eNotify(
							notifications.add(
									new ENotificationImpl((InternalEObject) source, Notification.REMOVE,
									feature, target, null));
							manyRefMemory.remove(target);
						}
					} else {
						if(updateMemory != null) {
							//source.eNotify(
							notifications.add(
									new ENotificationImpl((InternalEObject) source, Notification.SET,
									feature, target, updateMemory));
							singleRefMemory = updateMemory;
							updateMemory = null;
						} else {
							//source.eNotify(
							notifications.add(
									new ENotificationImpl((InternalEObject) source, Notification.SET,
									feature, target, null));
							singleRefMemory = null;
							//updateMemory = signature.get(targetParamName);
						}
					}
				} else {
					if(kind == FeatureKind.COUNTER) {
						decreaseCounter(1);
					}
				}
			}
			processed.add(signature);
		}
		return processed;
	}

	/**
	 * @throws CoreException
	 */
	private void decreaseCounter(int delta) throws CoreException {
		if(counterMemory >= delta) {
			int tempMemory = counterMemory-delta;
			//source.eNotify(
			notifications.add(
					new ENotificationImpl((InternalEObject) source, Notification.SET,
					feature, counterMemory, tempMemory));
			counterMemory = tempMemory;
		} else {
			throw new CoreException(new Status(IStatus.ERROR, null, "Counter cannot go below zero"));
		}
	}
	
	private void checkUnhandledNewMatch() {
		if(updateMemory != null) {
			//source.eNotify(
			notifications.add(
					new ENotificationImpl((InternalEObject) source, Notification.SET,
					feature, null, updateMemory));
			singleRefMemory = updateMemory;
		}
	}
}
