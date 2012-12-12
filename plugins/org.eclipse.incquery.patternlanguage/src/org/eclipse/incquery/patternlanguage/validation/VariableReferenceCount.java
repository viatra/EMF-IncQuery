/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.validation;

import java.util.Hashtable;
import java.util.Map;

import org.eclipse.incquery.patternlanguage.patternLanguage.ParameterRef;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;

/**
 * Helper class for counting variable references
 * 
 * @author Zoltan Ujhelyi
 * 
 */
class VariableReferenceCount {

    public enum ReferenceType {
        POSITIVE, NEGATIVE, READ_ONLY
    }

    private Variable variable;
    private Map<ReferenceType, Integer> counters = new Hashtable<ReferenceType, Integer>();
    private int size = 0;
    private boolean parameter;

    /**
     * @param variable
     */
    public VariableReferenceCount(Variable variable, boolean parameter) {
        this.variable = variable;
        this.parameter = parameter;
        for (ReferenceType type : ReferenceType.values()) {
            counters.put(type, 0);
        }
        if (variable instanceof ParameterRef && ((ParameterRef) variable).getReferredParam().getType() != null) {
            counters.put(ReferenceType.POSITIVE, 1);
        }
    }

    /**
     * @return the variable
     */
    public Variable getVariable() {
        return variable;
    }

    /**
     * @return the number of references stored regardless of reference type
     */
    public int getReferenceCount() {
        return size;
    }

    /**
     * @return the parameter
     */
    public boolean isParameter() {
        return parameter;
    }

    /**
     * @param type
     * @return the number of references stored of a selected reference type
     */
    public int getReferenceCount(ReferenceType type) {
        return counters.get(type);
    }

    public void incrementCounter(ReferenceType type) {
        size++;
        counters.put(type, counters.get(type) + 1);
    }
}
