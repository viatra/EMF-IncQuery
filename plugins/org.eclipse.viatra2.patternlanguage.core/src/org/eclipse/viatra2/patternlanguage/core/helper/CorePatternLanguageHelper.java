package org.eclipse.viatra2.patternlanguage.core.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.xtext.xbase.XExpression;

public class CorePatternLanguageHelper {
	/**
	 * Returns the name of the pattern, qualified by package name.
	 */
	public static String getFullyQualifiedName(Pattern p) {
		if (p == null) return null;
		PatternModel patternModel = (PatternModel) p.eContainer();

		String packageName = patternModel.getPackageName();
		if (packageName == null || packageName.isEmpty()) {
			return p.getName();
		} else {
			return packageName + "." + p.getName();
		}
		// TODO ("local pattern?")
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
			EList<EObject> eCrossReferences = eAllContents.next()
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

}
