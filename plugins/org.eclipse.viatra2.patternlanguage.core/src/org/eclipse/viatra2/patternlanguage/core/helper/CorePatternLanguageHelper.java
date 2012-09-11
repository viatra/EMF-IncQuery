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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Modifiers;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ParameterRef;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCall;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguageFactory;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.xtext.xbase.XExpression;

public final class CorePatternLanguageHelper {
	
	private CorePatternLanguageHelper(){}
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
	public static Map<String, Integer> getParameterPositionsByName(
			Pattern pattern) {
		HashMap<String, Integer> posMapping = new HashMap<String, Integer>();
		int parameterPosition = 0;
		for (Variable parameter : pattern.getParameters()) {
			posMapping.put(parameter.getName(), parameterPosition++);
		}
		return posMapping;
	}

	/** Finds all pattern variables referenced from the given XExpression. */
	public static Set<Variable> getReferencedPatternVariablesOfXExpression(
			XExpression xExpression) {
		Set<Variable> result = new HashSet<Variable>();
		TreeIterator<EObject> eAllContents = xExpression.eAllContents();
		while (eAllContents.hasNext()) {
			EObject expression = eAllContents.next();
			EList<EObject> eCrossReferences = expression
					.eCrossReferences();
			for (EObject eObject : eCrossReferences) {
				if (eObject instanceof Variable
						&& !EcoreUtil.isAncestor(xExpression, eObject)) {
					result.add((Variable) eObject);
				}
			}
		}
		return result;
	}

	
	public static EList<Variable> getAllVariablesInBody(PatternBody body,
			EList<Variable> previous) {
		EList<Variable> variables = previous;

		HashMap<String, Variable> parameterMap = new HashMap<String, Variable>();

		EList<Variable> parameters = ((Pattern) body.eContainer())
				.getParameters();
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
		for (Constraint constraint : body.getConstraints()){
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
		ParameterRef refVar = PatternLanguageFactory.eINSTANCE
				.createParameterRef();
		refVar.setName(var.getName());
		refVar.setType(var.getType());
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
				PatternCall call = (PatternCall)element;
				final Pattern patternRef = call.getPatternRef();
				if (patternRef != null) {
					result.add(patternRef);
				}
			}
		}
		return result;
	}

}
