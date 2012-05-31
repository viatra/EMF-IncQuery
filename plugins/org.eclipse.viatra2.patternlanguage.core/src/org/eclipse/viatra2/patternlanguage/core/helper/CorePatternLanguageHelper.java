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
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguageFactory;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.xtext.xbase.XExpression;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class CorePatternLanguageHelper {
	
	private CorePatternLanguageHelper(){}
	/**
	 * Returns the name of the pattern, qualified by package name.
	 */
	public static String getFullyQualifiedName(Pattern p) {
		if (p == null) {
			throw new IllegalArgumentException("No pattern specified for getFullyQualifiedName");
		}
		PatternModel patternModel = (PatternModel) p.eContainer();
		
		String packageName = patternModel.getPackageName();
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

		EList<Variable> parameters = ((Pattern) body.eContainer())
				.getParameters();
		Multimap<String, VariableReference> varRefs = HashMultimap.create();
		HashMap<String, Variable> parameterMap = new HashMap<String, Variable>();
		for (Constraint constraint : body.getConstraints()){
			Iterator<EObject> it = constraint.eAllContents();			
			while (it.hasNext()) {
				EObject obj = it.next();
				if (obj instanceof VariableReference) {
					String varName = ((VariableReference) obj).getVar();
					varRefs.put(varName, (VariableReference) obj);
				}
			}
		}
		for (Variable var : parameters) {
			parameterMap.put(var.getName(), var);
		}
		for(Variable var : variables) {
			parameterMap.put(var.getName(), var);
		}
		for (String varName : varRefs.keySet()) {
			Variable decl;
			if (parameterMap.containsKey(varName)) {
				decl = parameterMap.get(varName);
			} else {
				decl = PatternLanguageFactory.eINSTANCE
						.createVariable();
				decl.setName(varName);
				variables.add(decl);
			}
			for (VariableReference ref : varRefs.get(varName)) {
				ref.setVariable(decl);
			}
		}

		return variables;
	}
}
