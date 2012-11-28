/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.emf.validation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;

public class ClassifiedVariableReferences {

    private final Variable referredVariable;

    private final boolean isLocalVariable;

    private final Map<ClassifiedVariableReferenceEnum, Integer> classifiedReferenceCount;

    private final Set<Variable> equalsVariables;

    public Variable getReferredVariable() {
        return referredVariable;
    }

    public int getReferenceCount(ClassifiedVariableReferenceEnum forClass) {
        Integer count = classifiedReferenceCount.get(forClass);
        return count == null ? 0 : count;
    }

    public int getReferenceCountSum() {
        int sum = 0;

        for (Integer val : classifiedReferenceCount.values()) {
            sum += val;
        }

        return sum;
    }

    public boolean isVariableLocal() {
        return isLocalVariable;
    }

    /**
     * @return true if the variable is single-use a named variable
     */
    public boolean isNamedSingleUse() {
        String name = referredVariable.getName();
        return name != null && name.startsWith("_") && !name.contains("<");
    }

    public Set<Variable> getEqualsVariables() {
        return equalsVariables;
    }

    /**
     * @return true if the variable is an unnamed single-use variable
     */
    public boolean isUnnamedSingleUse() {
        String name = referredVariable.getName();
        return name != null && name.startsWith("_") && name.contains("<");
    }

    public ClassifiedVariableReferences(Variable referredVariable, boolean isLocal) {
        this.referredVariable = referredVariable;
        this.isLocalVariable = isLocal;

        classifiedReferenceCount = new HashMap<ClassifiedVariableReferenceEnum, Integer>();
        equalsVariables = new HashSet<Variable>();
    }

    public void incrementCounter(ClassifiedVariableReferenceEnum forClass) {
        Integer count = classifiedReferenceCount.get(forClass);
        classifiedReferenceCount.put(forClass, count == null ? 1 : count + 1);
    }

    public void addEqualsVariable(Variable var) {
        equalsVariables.add(var);
    }

}
