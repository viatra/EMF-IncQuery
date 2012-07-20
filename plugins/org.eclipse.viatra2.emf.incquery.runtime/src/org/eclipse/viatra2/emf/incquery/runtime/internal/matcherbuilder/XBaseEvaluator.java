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

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.runtime.internal.XtextInjectorProvider;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.AbstractEvaluator;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
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
 * @author Bergmann Gábor
 *
 */
public class XBaseEvaluator extends AbstractEvaluator {
	private final XExpression xExpression;
	private final Map<QualifiedName, Integer> qualifiedMapping;
	private final Pattern pattern;
	
	private final IExpressionInterpreter interpreter;
	private final Provider<IEvaluationContext> contextProvider;
			
	/**
	 * @param xExpression the expression to evaluate
	 * @param qualifiedMapping maps variable qualified names to positions.
	 * @param pattern 
	 */
	public XBaseEvaluator(XExpression xExpression,
			Map<QualifiedName, Integer> qualifiedMapping, Pattern pattern) {
		super();
		this.xExpression = xExpression;
		this.qualifiedMapping = qualifiedMapping;
		this.pattern = pattern;
		
		Injector injector = XtextInjectorProvider.INSTANCE.getInjector();
		interpreter = injector.getInstance(IExpressionInterpreter.class);
		contextProvider = injector.getProvider(IEvaluationContext.class);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.AbstractEvaluator#doEvaluate(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple)
	 */
	@Override
	public Object doEvaluate(Tuple tuple) throws Throwable {
		IEvaluationContext context = contextProvider.get();
		for (Entry<QualifiedName, Integer> varPosition : qualifiedMapping.entrySet()) {
			context.newValue(varPosition.getKey(), tuple.get(varPosition.getValue()));
		}
		IEvaluationResult result = interpreter.evaluate(xExpression, context, CancelIndicator.NullImpl);
		if (result==null) throw new IncQueryException(
				String.format("XBase expression interpreter returned no result while evaluating expression %s in pattern %s.", xExpression, pattern),
				"XBase expression interpreter returned no result."
				);
		if (result.getException()!=null) throw result.getException();
		return result.getResult();
	}

}
