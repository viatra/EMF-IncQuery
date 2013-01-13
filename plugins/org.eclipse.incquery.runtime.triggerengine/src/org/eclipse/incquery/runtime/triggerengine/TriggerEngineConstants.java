/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.triggerengine;

/**
 * @author Abel Hegedus
 *
 */
public class TriggerEngineConstants {

    /**
     * If true, activation collections returned by methods are modifiable
     * (addition and deletion is allowed).
     */
    public static boolean MODIFIABLE_ACTIVATION_COLLECTIONS = false;
    
    /**
     * If true, activation collections returned by methods are mutable
     * (they present a live view).
     */
    public static boolean MUTABLE_ACTIVATION_COLLECTIONS = false;
    
    /**
     * If true, the lifecycle of 
     */
    public static boolean ALLOW_RUNTIME_LIFECYCLE_CHANGES = false;
    
    public static boolean MUTABLE_JOBLISTS = false;
    
}
