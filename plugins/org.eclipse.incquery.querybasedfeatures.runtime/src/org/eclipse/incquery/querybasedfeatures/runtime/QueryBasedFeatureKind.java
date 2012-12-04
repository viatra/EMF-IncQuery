/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.querybasedfeatures.runtime;

/**
 * 
 * @author Abel Hegedus
 *
 */
public enum QueryBasedFeatureKind {
    SINGLE_REFERENCE, MANY_REFERENCE, SUM, COUNTER, ITERATION;
    
    public static String getStringValue(QueryBasedFeatureKind kind) {
        if(SINGLE_REFERENCE.equals(kind)) {
            return "single";
        } else if(MANY_REFERENCE.equals(kind)) {
            return "many";
        } else if(SUM.equals(kind)) {
            return "sum";
        } else if(COUNTER.equals(kind)) {
            return "counter";
        } else if(ITERATION.equals(kind)) {
            return "iteration";
        } else return null;
    }
}