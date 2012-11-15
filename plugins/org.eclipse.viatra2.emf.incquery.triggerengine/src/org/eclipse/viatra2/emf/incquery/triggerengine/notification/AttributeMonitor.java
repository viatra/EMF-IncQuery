package org.eclipse.viatra2.emf.incquery.triggerengine.notification;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

/**
 * The class defines the operations that are required to observ 
 * the EMF attribute changes on pattern match objects. 
 * 
 * @author Tamas Szabo
 *
 * @param <MatchType>
 */
public abstract class AttributeMonitor<MatchType extends IPatternMatch> {

	private List<IAttributeMonitorListener<MatchType>> listeners;
	
	public AttributeMonitor() {
		this.listeners = new ArrayList<IAttributeMonitorListener<MatchType>>();
	}
	
	public void addCallbackOnMatchUpdate(IAttributeMonitorListener<MatchType> listener) {
		this.listeners.add(listener);
	}
	
	public void removeCallbackOnMatchUpdate(IAttributeMonitorListener<MatchType> listener) {
		this.listeners.remove(listener);
	}
	
	public abstract void registerFor(MatchType match);
	
	public abstract void unregisterForAll();
	
	public abstract void unregisterFor(MatchType match);
	
	protected void notifyListeners(MatchType match) {
		for (IAttributeMonitorListener<MatchType> listener : listeners) {
			listener.notifyUpdate(match);
		}
	}
	
	public void dispose() {
		this.unregisterForAll();
	}
}
