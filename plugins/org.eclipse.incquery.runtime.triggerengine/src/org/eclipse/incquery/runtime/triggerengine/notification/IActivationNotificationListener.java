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
import org.eclipse.incquery.runtime.triggerengine.api.Activation;

/**
 * The interface is used to observe the changes in the collection of activations. <br/>
 * <br/>
 * An implementing class is for example the {@link Agenda} which is called back by the {@link AbstractRule} instances
 * when those have updated the activations after an EMF operation.
 * 
 * @author Tamas Szabo
 * 
 */
public interface IActivationNotificationListener {

    public void activationAppeared(Activation<? extends IPatternMatch> activation);

    public void activationDisappeared(Activation<? extends IPatternMatch> activation);

}
