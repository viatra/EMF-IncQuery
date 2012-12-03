/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Abel Hegedus - initial API and implementation
 *******************************************************************************/

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
