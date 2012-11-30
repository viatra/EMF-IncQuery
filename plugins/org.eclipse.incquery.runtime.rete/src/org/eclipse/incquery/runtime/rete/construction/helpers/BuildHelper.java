/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.construction.helpers;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.incquery.runtime.rete.construction.Buildable;
import org.eclipse.incquery.runtime.rete.construction.Stub;
import org.eclipse.incquery.runtime.rete.construction.psystem.PVariable;
import org.eclipse.incquery.runtime.rete.tuple.TupleMask;

/**
 * @author Bergmann Gábor
 * 
 */
public class BuildHelper {

    /**
     * If two or more variables are the same in the variablesTuple of the stub, then a checker node is built to enforce
     * their equality.
     * 
     * @return the derived stub that contains the additional checkers, or the original if no action was neccessary.
     */
    public static <StubHandle> Stub<StubHandle> enforceVariableCoincidences(Buildable<?, StubHandle, ?> buildable,
            Stub<StubHandle> stub) {
        Map<Object, List<Integer>> indexWithMupliplicity = stub.getVariablesTuple().invertIndexWithMupliplicity();
        for (Map.Entry<Object, List<Integer>> pVariableIndices : indexWithMupliplicity.entrySet()) {
            List<Integer> indices = pVariableIndices.getValue();
            if (indices.size() > 1) {
                int[] indexArray = new int[indices.size()];
                int m = 0;
                for (Integer index : indices)
                    indexArray[m++] = index;
                stub = buildable.buildEqualityChecker(stub, indexArray);
                // TODO also trim here?
            }
        }
        return stub;

    }

    /**
     * Trims the results in the stub into a collector, by selecting exported variables in a particular order.
     * 
     * @return the derived stub that contains the additional checkers, or the original if no action was neccessary.
     */
    public static <StubHandle, Collector> void projectIntoCollector(Buildable<?, StubHandle, Collector> buildable,
            Stub<StubHandle> stub, Collector collector, PVariable[] selectedVariables) {
        int paramNum = selectedVariables.length;
        int[] tI = new int[paramNum];
        for (int i = 0; i < paramNum; i++) {
            tI[i] = stub.getVariablesIndex().get(selectedVariables[i]);
        }
        int tiW = stub.getVariablesTuple().getSize();
        TupleMask trim = new TupleMask(tI, tiW);
        Stub<StubHandle> trimmer = buildable.buildTrimmer(stub, trim);
        buildable.buildConnection(trimmer, collector);
    }

    /**
     * Calculated index mappings for a join, based on the common variables of the two parent stubs.
     * 
     * @author Bergmann Gábor
     * 
     */
    public static class JoinHelper<StubHandle> {
        private TupleMask primaryMask;
        private TupleMask secondaryMask;
        private TupleMask complementerMask;

        /**
         * @pre enforceVariableCoincidences() has been called on both sides.
         * @param primaryStub
         * @param secondaryStub
         */
        public JoinHelper(Stub<StubHandle> primaryStub, Stub<StubHandle> secondaryStub) {
            super();

            Set<PVariable> primaryVariables = primaryStub.getVariablesTuple().getDistinctElements();
            Set<PVariable> secondaryVariables = secondaryStub.getVariablesTuple().getDistinctElements();
            int oldNodes = 0;
            Set<Integer> introducingSecondaryIndices = new TreeSet<Integer>();
            for (PVariable var : secondaryVariables) {
                if (primaryVariables.contains(var))
                    oldNodes++;
                else
                    introducingSecondaryIndices.add(secondaryStub.getVariablesIndex().get(var));
            }
            int[] primaryIndices = new int[oldNodes];
            final int[] secondaryIndices = new int[oldNodes];
            int k = 0;
            for (PVariable var : secondaryVariables) {
                if (primaryVariables.contains(var)) {
                    primaryIndices[k] = primaryStub.getVariablesIndex().get(var);
                    secondaryIndices[k] = secondaryStub.getVariablesIndex().get(var);
                    k++;
                }
            }
            int[] complementerIndices = new int[introducingSecondaryIndices.size()];
            int l = 0;
            for (Integer integer : introducingSecondaryIndices) {
                complementerIndices[l++] = integer;
            }
            primaryMask = new TupleMask(primaryIndices, primaryStub.getVariablesTuple().getSize());
            secondaryMask = new TupleMask(secondaryIndices, secondaryStub.getVariablesTuple().getSize());
            complementerMask = new TupleMask(complementerIndices, secondaryStub.getVariablesTuple().getSize());

        }

        /**
         * @return the primaryMask
         */
        public TupleMask getPrimaryMask() {
            return primaryMask;
        }

        /**
         * @return the secondaryMask
         */
        public TupleMask getSecondaryMask() {
            return secondaryMask;
        }

        /**
         * @return the complementerMask
         */
        public TupleMask getComplementerMask() {
            return complementerMask;
        }

    }

    public static <StubHandle> Stub<StubHandle> naturalJoin(Buildable<?, StubHandle, ?> buildable,
            Stub<StubHandle> primaryStub, Stub<StubHandle> secondaryStub) {
        JoinHelper<StubHandle> joinHelper = new JoinHelper<StubHandle>(primaryStub, secondaryStub);
        return buildable.buildBetaNode(primaryStub, secondaryStub, joinHelper.getPrimaryMask(),
                joinHelper.getSecondaryMask(), joinHelper.getComplementerMask(), false);
    }

}
