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
package org.eclipse.incquery.patternlanguage.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;

/**
 * Just a basic Union-Find algorithm implementation for a validation problem.
 */
public class UnionFindForVariables {

    private final List<Variable> inputVariables;
    private final int[] unionIdArray;

    /**
     * @param inputVariables
     *            which are assumed to be disjoint at the start
     */
    public UnionFindForVariables(List<Variable> inputVariables) {
        this.inputVariables = new ArrayList<Variable>(inputVariables);
        unionIdArray = new int[inputVariables.size()];
        for (int i = 0; i < inputVariables.size(); i++) {
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
                    if (inputVariables.contains(variable) && !isSameUnion(current, variable)) {
                        return false;
                    }
                } else {
                    if (inputVariables.contains(variable)) {
                        current = variable;
                    }
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
                    if (inputVariables.contains(variable)) {
                        unite(current, variable);
                    }
                } else {
                    if (inputVariables.contains(variable)) {
                        current = variable;
                    }
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
        // Should not be called with out of the scope variables!
        return unionIdArray[inputVariables.indexOf(variable1)] == unionIdArray[inputVariables.indexOf(variable2)];
    }

    private void unite(Variable variable1, Variable variable2) {
        // Should not be called with out of the scope variables!
        if (!isSameUnion(variable1, variable2)) {
            int variable1ID = unionIdArray[inputVariables.indexOf(variable1)];
            int variable2ID = unionIdArray[inputVariables.indexOf(variable2)];
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
        for (Variable variableOuter : inputVariables) {
            int currentID = unionIdArray[inputVariables.indexOf(variableOuter)];
            if (!previousKeys.contains(currentID)) {
                previousKeys.add(currentID);
                Set<Variable> currentSet = new HashSet<Variable>();
                for (Variable variableInner : inputVariables) {
                    if (unionIdArray[inputVariables.indexOf(variableInner)] == currentID) {
                        currentSet.add(variableInner);
                    }
                }
                resultList.add(currentSet);
            }
        }
        return resultList;
    }

    /**
     * 
     * @param var
     * @return the set of variables in the partition of a selected variable
     */
    public Set<Variable> getPartitionOfVariable(Variable var) {
        Set<Variable> set = new HashSet<Variable>();
        set.add(var);
        if (inputVariables.contains(var)) {
            int id = unionIdArray[inputVariables.indexOf(var)];
            for (Variable inner : inputVariables) {
                if (id == unionIdArray[inputVariables.indexOf(inner)]) {
                    set.add(inner);
                }
            }
        }
        return set;
    }
}
