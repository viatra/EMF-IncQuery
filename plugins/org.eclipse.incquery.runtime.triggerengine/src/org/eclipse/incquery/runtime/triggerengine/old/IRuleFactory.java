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

package org.eclipse.incquery.runtime.triggerengine.api;

import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;

/**
 * The {@link IRuleFactory} defines the way a {@link AbstractRule} instance is created.
 * 
 * @author Tamas Szabo
 * 
 */
public interface IRuleFactory {

    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> AbstractRule<Match> createRule(
            IncQueryEngine engine, IMatcherFactory<Matcher> factory, boolean upgradedStateUsed,
            boolean disappearedStateUsed);

}
