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
package org.eclipse.viatra2.patternlanguage.core.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.AggregatedValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.AnnotationParameter;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.CompareConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Modifiers;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ParameterRef;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionHead;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCall;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCompositionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguageFactory;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ValueReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableValue;
import org.eclipse.xtext.xbase.XExpression;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;

public final class CorePatternLanguageHelper {

    private CorePatternLanguageHelper() {
    }

    /**
     * Returns the name of the pattern, qualified by package name.
     */
    public static String getFullyQualifiedName(Pattern p) {
        if (p == null) {
            throw new IllegalArgumentException("No pattern specified for getFullyQualifiedName");
        }
        PatternModel patternModel = (PatternModel) p.eContainer();

        String packageName = (patternModel == null) ? null : patternModel.getPackageName();
        if (packageName == null || packageName.isEmpty()) {
            return p.getName();
        } else {
            return packageName + "." + p.getName();
        }
        // TODO ("local pattern?")
    }

    /**
     * Returns true if the pattern has a private modifier, false otherwise.
     * 
     * @param pattern
     * @return
     */
    public static boolean isPrivate(Pattern pattern) {
        boolean isPrivate = false;
        for (Modifiers mod : pattern.getModifiers()) {
            if (mod.isPrivate()) {
                isPrivate = true;
            }
        }
        return isPrivate;
    }

    /** Compiles a map for name-based lookup of symbolic parameter positions. */
    public static Map<String, Integer> getParameterPositionsByName(Pattern pattern) {
        HashMap<String, Integer> posMapping = new HashMap<String, Integer>();
        int parameterPosition = 0;
        for (Variable parameter : pattern.getParameters()) {
            posMapping.put(parameter.getName(), parameterPosition++);
        }
        return posMapping;
    }

    /** Finds all pattern variables referenced from the given XExpression. */
    public static Set<Variable> getReferencedPatternVariablesOfXExpression(XExpression xExpression) {
        Set<Variable> result = new HashSet<Variable>();
        if (xExpression != null) {
            TreeIterator<EObject> eAllContents = xExpression.eAllContents();
            while (eAllContents.hasNext()) {
                EObject expression = eAllContents.next();
                EList<EObject> eCrossReferences = expression.eCrossReferences();
                for (EObject eObject : eCrossReferences) {
                    if (eObject instanceof Variable && !EcoreUtil.isAncestor(xExpression, eObject)) {
                        result.add((Variable) eObject);
                    }
                }
            }
        }
        return result;
    }

    public static EList<Variable> getAllVariablesInBody(PatternBody body, EList<Variable> previous) {
        EList<Variable> variables = previous;

        HashMap<String, Variable> parameterMap = new HashMap<String, Variable>();

        EList<Variable> parameters = ((Pattern) body.eContainer()).getParameters();
        for (Variable var : variables) {
            parameterMap.put(var.getName(), var);
        }
        for (Variable var : parameters) {
            if (!parameterMap.containsKey(var.getName())) {
                // Creating a new paramater ref variable
                ParameterRef refVar = initializeParameterRef(var);
                parameterMap.put(var.getName(), refVar);
                variables.add(refVar);
            }
        }
        int unnamedCounter = 0;
        for (Constraint constraint : body.getConstraints()) {
            Iterator<EObject> it = constraint.eAllContents();
            while (it.hasNext()) {
                EObject obj = it.next();
                if (obj instanceof VariableReference) {
                    VariableReference varRef = (VariableReference) obj;
                    String varName = varRef.getVar();
                    if ("_".equals(varName)) {
                        varName = String.format("_<%d>", unnamedCounter);
                        unnamedCounter++;
                    }
                    Variable var;
                    if (parameterMap.containsKey(varName)) {
                        var = parameterMap.get(varName);
                    } else {
                        var = initializeLocalVariable(varName);
                        variables.add(var);
                        parameterMap.put(varName, var);
                    }
                    varRef.setVariable(var);
                }
            }
        }

        return variables;
    }

    /**
     * @param varName
     * @return
     */
    private static Variable initializeLocalVariable(String varName) {
        Variable decl;
        decl = PatternLanguageFactory.eINSTANCE.createVariable();
        decl.setName(varName);
        return decl;
    }

    /**
     * @param var
     * @return
     */
    private static ParameterRef initializeParameterRef(Variable var) {
        ParameterRef refVar = PatternLanguageFactory.eINSTANCE.createParameterRef();
        refVar.setName(var.getName());
        // refVar.setType(var.getType());
        refVar.setReferredParam(var);
        return refVar;
    }

    /** Finds all patterns referenced from the given pattern. */
    public static Set<Pattern> getReferencedPatterns(Pattern sourcePattern) {
        Set<Pattern> result = new HashSet<Pattern>();
        TreeIterator<EObject> eAllContents = sourcePattern.eAllContents();
        while (eAllContents.hasNext()) {
            EObject element = eAllContents.next();
            if (element instanceof PatternCall) {
                PatternCall call = (PatternCall) element;
                final Pattern patternRef = call.getPatternRef();
                if (patternRef != null) {
                    result.add(patternRef);
                }
            }
        }
        return result;
    }

    private static class AnnotationNameFilter implements Predicate<Annotation> {

        private final String name;

        public AnnotationNameFilter(String name) {
            this.name = name;
        }

        @Override
        public boolean apply(Annotation annotation) {
            return name.equals(annotation.getName());
        }
    }

    /**
     * Returns the first annotation of a given name from a pattern. This method ignores multiple defined annotations by
     * the same name. For getting a filtered collections of annotations, see
     * {@link #getAnnotationsByName(Pattern, String)}
     * 
     * @param pattern
     *            the pattern instance
     * @param name
     *            the name of the annotation to return
     * @returns the first annotation or null if no such annotation exists
     */
    public static Annotation getFirstAnnotationByName(Pattern pattern, String name) {
        return Iterables.find(pattern.getAnnotations(), new AnnotationNameFilter(name), null);
    }

    /**
     * Returns the collection of annotations of a pattern by a name. For getting the first annotations by name, see
     * {@link #getAnnotationByName(Pattern, String)}
     * 
     * @param pattern
     *            the pattern instance
     * @param name
     *            the name of the annotation to return
     * @returns a non-null, but possibly empty collection of annotations
     */
    public static Collection<Annotation> getAnnotationsByName(Pattern pattern, String name) {
        return Collections2.filter(pattern.getAnnotations(), new AnnotationNameFilter(name));
    }

    public static ListMultimap<String, ValueReference> getAnnotationParameters(Annotation annotation) {
        ListMultimap<String, ValueReference> parameterMap = ArrayListMultimap.create();
        for (AnnotationParameter param : annotation.getParameters()) {
            parameterMap.put(param.getName(), param.getValue());
        }
        return parameterMap;
    }

    /**
     * @param valueReference
     * @return all variables from the ValueReference object. (Either referenced directly, or referenced throught an
     *         AggregatedValue.)
     */
    public static Set<Variable> getVariablesFromValueReference(ValueReference valueReference) {
        Set<Variable> resultSet = new HashSet<Variable>();
        if (valueReference != null) {
            if (valueReference instanceof VariableValue) {
                resultSet.add(((VariableValue) valueReference).getValue().getVariable());
            } else if (valueReference instanceof AggregatedValue) {
                AggregatedValue aggregatedValue = (AggregatedValue) valueReference;
                for (ValueReference valueReferenceInner : aggregatedValue.getCall().getParameters()) {
                    for (Variable variable : getVariablesFromValueReference(valueReferenceInner)) {
                        resultSet.add(variable);
                    }
                }
            }
        }
        return resultSet;
    }

    /**
     * @param patternBody
     * @return A list of variables, which are running/unnamed variables in the pattern body. These variables' name
     *         starts with the "_" prefix, and can be found in find, count find calls.
     */
    public static List<Variable> getUnnamedRunningVariables(PatternBody patternBody) {
        List<Variable> resultList = new ArrayList<Variable>();
        for (Constraint constraint : patternBody.getConstraints()) {
            if (constraint instanceof CompareConstraint) {
                // Just from aggregated elements
                CompareConstraint compareConstraint = (CompareConstraint) constraint;
                ValueReference leftValueReference = compareConstraint.getLeftOperand();
                ValueReference rightValueReference = compareConstraint.getRightOperand();
                resultList.addAll(getUnnamedVariablesFromValueReference(leftValueReference, true));
                resultList.addAll(getUnnamedVariablesFromValueReference(rightValueReference, true));
            } else if (constraint instanceof PatternCompositionConstraint) {
                // All from here, aggregates and normal running variables
                PatternCompositionConstraint patternCompositionConstraint = (PatternCompositionConstraint) constraint;
                for (ValueReference valueReference : patternCompositionConstraint.getCall().getParameters()) {
                    resultList.addAll(getUnnamedVariablesFromValueReference(valueReference, false));
                }
            } else if (constraint instanceof PathExpressionConstraint) {
                // Just from aggregated elements
                PathExpressionConstraint pathExpressionConstraint = (PathExpressionConstraint) constraint;
                PathExpressionHead pathExpressionHead = pathExpressionConstraint.getHead();
                ValueReference valueReference = pathExpressionHead.getDst();
                resultList.addAll(getUnnamedVariablesFromValueReference(valueReference, true));
            }
        }
        return resultList;
    }

    private static Set<Variable> getUnnamedVariablesFromValueReference(ValueReference valueReference,
            boolean onlyFromAggregatedValues) {
        Set<Variable> resultSet = new HashSet<Variable>();
        if (valueReference != null) {
            if (valueReference instanceof VariableValue) {
                Variable variable = ((VariableValue) valueReference).getValue().getVariable();
                if (variable.getName().startsWith("_") && !onlyFromAggregatedValues) {
                    resultSet.add(variable);
                }
            } else if (valueReference instanceof AggregatedValue) {
                AggregatedValue aggregatedValue = (AggregatedValue) valueReference;
                for (ValueReference valueReferenceInner : aggregatedValue.getCall().getParameters()) {
                    for (Variable variable : getUnnamedVariablesFromValueReference(valueReferenceInner, false)) {
                        if (variable.getName().startsWith("_")) {
                            resultSet.add(variable);
                        }
                    }
                }
            }
        }
        return resultSet;
    }

}
