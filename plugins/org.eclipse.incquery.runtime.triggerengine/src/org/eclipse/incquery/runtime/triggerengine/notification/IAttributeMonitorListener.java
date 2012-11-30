package org.eclipse.incquery.runtime.triggerengine.notification;

import org.eclipse.incquery.runtime.api.IPatternMatch;

/**
 * The interface exposes the {@link #notifyUpdate(IPatternMatch)} method to receive notifications when the attributes of
 * the match objects have changed.
 * 
 * @author Tamas Szabo
 * 
 * @param <MatchType>
 */
public interface IAttributeMonitorListener<MatchType extends IPatternMatch> {

    public void notifyUpdate(MatchType match);

}
