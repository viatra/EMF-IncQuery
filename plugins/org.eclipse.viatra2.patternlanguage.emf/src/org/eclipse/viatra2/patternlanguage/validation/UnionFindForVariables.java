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
package org.eclipse.viatra2.patternlanguage.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;

/**
 * Just a basic Union-Find algorithm implementation for a validation problem.
 */
public class UnionFindForVariables {

    private final List<Variable> variables;
    private final int[] unionIdArray;

    public UnionFindForVariables(List<Variable> variables) {
        this.variables = variables;
        unionIdArray = new int[variables.size()];
        for (int i = 0; i < variables.size(); i++) {
            unionIdArray[i] = i;
        }
    }

    public String getCurrentPartitionsFormatted() {
        String result = "";
        Set<Integer> previousKeys = new HashSet<Integer>(); 
        for (int i = 0; i < variables.size(); i++) {
           Integer currentID = unionIdArray[i];
           if (!previousKeys.contains(currentID)) {
               previousKeys.add(currentID);
               result = result.concat("[");
               for (Variable variable : variables) {
                   if (unionIdArray[variables.indexOf(variable)] == currentID) {
                       result = result.concat(variable.getName() + " ");
                   }
               }
               result = result.concat("]");
           }
        }
        return result;
    }

    private boolean isSameUnion(Variable variable1, Variable variable2) {
        return unionIdArray[variables.indexOf(variable1)] == unionIdArray[variables.indexOf(variable2)];
    }

    public boolean isSameUnion(Set<Variable> variables) {
        if (variables.size() > 1) {
            Variable current = null;
            for (Variable variable : variables) {
                if (current != null) {
                    if (!isSameUnion(current, variable)) {
                        return false;
                    }
                    current = variable;
                } else {
                    current = variable;
                }
            }
        }
        return true;
    }

    private void unite(Variable variable1, Variable variable2) {
        if (!isSameUnion(variable1, variable2)) {
            int variable1ID = unionIdArray[variables.indexOf(variable1)];
            int variable2ID = unionIdArray[variables.indexOf(variable2)];
            for (int i = 0; i < unionIdArray.length; i++) {
                if (unionIdArray[i] == variable1ID) {
                    unionIdArray[i] = variable2ID;
                }
            }
        }
    }

    public void unite(Set<Variable> variables) {
        if (variables.size() > 1) {
            Variable current = null;
            for (Variable variable : variables) {
                if (current != null) {
                    unite(current, variable);
                    current = variable;
                } else {
                    current = variable;
                }
            }
        }
    }

    public boolean isMoreThanOneUnion() {
        Integer firstId = null;
        for (int i = 0; i < variables.size(); i++) {
            if (firstId != null) {
                if (firstId != unionIdArray[i]) {
                    return true;
                }
            } else {
                firstId = unionIdArray[i];
            }
        }
        return false;
    }

}
