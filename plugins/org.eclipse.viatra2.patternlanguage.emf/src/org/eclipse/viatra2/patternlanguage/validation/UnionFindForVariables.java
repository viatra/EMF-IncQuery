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

import java.util.ArrayList;
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

    /**
     * @param variables
     *            which are assumed to be disjoint at the start
     */
    public UnionFindForVariables(List<Variable> variables) {
        this.variables = variables;
        unionIdArray = new int[variables.size()];
        for (int i = 0; i < variables.size(); i++) {
            unionIdArray[i] = i;
        }
    }

    /**
     * @param variables
     * @return true if the variables are in the same union/set
     */
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

    /**
     * @param variables
     *            places the input variables in to the same union/set
     */
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

    /**
     * @return a human readable string input formatted based on the underlying unions/sets
     */
    public String getCurrentPartitionsFormatted() {
        String result = "";
        for (Set<Variable> unionSet : getPartitions()) {
            result = result.concat("[");
            for (Variable variable : unionSet) {
                result = result.concat(variable.getName() + " ");
            }
            result = result.concat("]");
        }
        return result;
    }

    /**
     * @return true if there is more than one real union/set
     */
    public boolean isMoreThanOneUnion() {
        if (getPartitions().size() > 1) {
            return true;
        }
        return false;
    }

    private boolean isSameUnion(Variable variable1, Variable variable2) {
        // We need to check the indexes, as it might be called with variables out of our scope.
        if (variables.contains(variable1) && variables.contains(variable2)) {
            return unionIdArray[variables.indexOf(variable1)] == unionIdArray[variables.indexOf(variable2)];
        } else {
            // We return true if one or both variables are out of scope.
            // If it would be false it would be weird, because it would stay false even after a unite call.
            return true;
        }
    }

    private void unite(Variable variable1, Variable variable2) {
        if (!isSameUnion(variable1, variable2) && variables.contains(variable1) && variables.contains(variable2)) {
            int variable1ID = unionIdArray[variables.indexOf(variable1)];
            int variable2ID = unionIdArray[variables.indexOf(variable2)];
            for (int i = 0; i < unionIdArray.length; i++) {
                if (unionIdArray[i] == variable1ID) {
                    unionIdArray[i] = variable2ID;
                }
            }
        }
    }

    private List<Set<Variable>> getPartitions() {
        List<Set<Variable>> resultList = new ArrayList<Set<Variable>>();
        Set<Integer> previousKeys = new HashSet<Integer>();
        for (int i = 0; i < variables.size(); i++) {
            Integer currentID = unionIdArray[i];
            if (!previousKeys.contains(currentID)) {
                previousKeys.add(currentID);
                Set<Variable> currentSet = new HashSet<Variable>();
                for (Variable variable : variables) {
                    if (unionIdArray[variables.indexOf(variable)] == currentID) {
                        currentSet.add(variable);
                    }
                }
                resultList.add(currentSet);
            }
        }
        return resultList;
    }

}
