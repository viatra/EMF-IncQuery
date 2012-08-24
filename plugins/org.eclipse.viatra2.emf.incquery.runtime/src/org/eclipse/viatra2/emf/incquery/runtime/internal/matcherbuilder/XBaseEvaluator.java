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

package org.eclipse.viatra2.emf.incquery.runtime.internal.matcherbuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.viatra2.emf.incquery.runtime.IExtensions;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchChecker;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.runtime.internal.XtextInjectorProvider;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.AbstractEvaluator;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.CheckConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.interpreter.IEvaluationContext;
import org.eclipse.xtext.xbase.interpreter.IEvaluationResult;
import org.eclipse.xtext.xbase.interpreter.IExpressionInterpreter;

import com.google.inject.Injector;
import com.google.inject.Provider;

/**
 * Evaluates an XBase XExpression inside Rete.
 * 
 * @author Bergmann Gábor
 * 
 */
public class XBaseEvaluator extends AbstractEvaluator {
	private final XExpression xExpression;
	private final Map<String, Integer> tupleNameMap;
	private final Pattern pattern;

	private IMatchChecker matchChecker;

	private IExpressionInterpreter interpreter;
	private Provider<IEvaluationContext> contextProvider;
	private Map<QualifiedName, Integer> qualifiedMapping;

	/**
	 * @param xExpression
	 *            the expression to evaluate
	 * @param qualifiedMapping
	 *            maps variable qualified names to positions.
	 * @param pattern
	 */
	public XBaseEvaluator(XExpression xExpression,
			Map<String, Integer> tupleNameMapping, Pattern pattern) {
		super();
		this.xExpression = xExpression;
		this.tupleNameMap = tupleNameMapping;
		this.pattern = pattern;

		// First try to setup the generated code from the extension point
		IConfigurationElement[] configurationElements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						IExtensions.XEXPRESSIONEVALUATOR_EXTENSION_POINT_ID);
		for (IConfigurationElement configurationElement : configurationElements) {
			String id = configurationElement.getAttribute("id");
			if (id.equals(getExpressionID(pattern, xExpression))) {
				Object object = null;
				try {
					object = configurationElement
							.createExecutableExtension("evaluatorClass");
				} catch (CoreException e) {
					e.printStackTrace();
				}
				if (object != null && object instanceof IMatchChecker) {
					matchChecker = (IMatchChecker) object;
				}
			}
		}
		
		// Second option, setup the attributes for the interpreted approach
		if (matchChecker == null) {
			Injector injector = XtextInjectorProvider.INSTANCE.getInjector();
			interpreter = injector.getInstance(IExpressionInterpreter.class);
			contextProvider = injector.getProvider(IEvaluationContext.class);
			qualifiedMapping = new HashMap<QualifiedName, Integer>();
			for (Entry<String, Integer> entry : tupleNameMap.entrySet()) {
				qualifiedMapping.put(QualifiedName.create(entry.getKey()),
						entry.getValue());
			}
		}
	}

	private static String getExpressionID(Pattern pattern,
			XExpression xExpression) {
		int patternBodyNumber = 0;
		for (PatternBody patternBody : pattern.getBodies()) {
			patternBodyNumber++;
			int checkConstraintNumber = 0;
			for (Constraint constraint : patternBody.getConstraints()) {
				if (constraint instanceof CheckConstraint) {
					CheckConstraint checkConstraint = (CheckConstraint) constraint;
					checkConstraintNumber++;
					String postFix = patternBodyNumber + "_"
							+ checkConstraintNumber;
					if (xExpression.equals(checkConstraint.getExpression())) {
						return CorePatternLanguageHelper
								.getFullyQualifiedName(pattern) + "_" + postFix;
					}
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.
	 * AbstractEvaluator
	 * #doEvaluate(org.eclipse.viatra2.gtasm.patternmatcher.incremental
	 * .rete.tuple.Tuple)
	 */
	@Override
	public Object doEvaluate(Tuple tuple) throws Throwable {
		// First option: try to evalute with the generated code
		if (matchChecker != null) {
			return matchChecker.evaluateXExpression(tuple, tupleNameMap);
		}

		// Second option: try to evaluate with the interpreted approach
		IEvaluationContext context = contextProvider.get();
		for (Entry<QualifiedName, Integer> varPosition : qualifiedMapping
				.entrySet()) {
			context.newValue(varPosition.getKey(),
					tuple.get(varPosition.getValue()));
		}
		IEvaluationResult result = interpreter.evaluate(xExpression, context,
				CancelIndicator.NullImpl);
		if (result == null)
			throw new IncQueryException(
					String.format(
							"XBase expression interpreter returned no result while evaluating expression %s in pattern %s.",
							xExpression, pattern),
					"XBase expression interpreter returned no result.");
		if (result.getException() != null)
			throw result.getException();
		return result.getResult();
	}

}
