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

import org.eclipse.viatra2.emf.incquery.runtime.IncQueryRuntimePlugin;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.AbstractEvaluator;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.interpreter.IEvaluationContext;
import org.eclipse.xtext.xbase.interpreter.IEvaluationResult;
import org.eclipse.xtext.xbase.interpreter.IExpressionInterpreter;

import com.google.inject.Provider;

/**
 * Evaluates an XBase XExpression inside Rete.
 * @author Bergmann Gábor
 *
 */
public class XBaseEvaluator extends AbstractEvaluator {
	private final XExpression xExpression;
	private final Map<QualifiedName, Integer> qualifiedMapping;
	
	private final IExpressionInterpreter interpreter = 
			IncQueryRuntimePlugin.getDefault().getInjector().getInstance(IExpressionInterpreter.class);
	private final Provider<IEvaluationContext> contextProvider =
			IncQueryRuntimePlugin.getDefault().getInjector().getProvider(IEvaluationContext.class);
			
	/**
	 * @param xExpression the expression to evaluate
	 * @param qualifiedMapping maps variable qualified names to positions.
	 */
	public XBaseEvaluator(XExpression xExpression,
			Map<QualifiedName, Integer> qualifiedMapping) {
		super();
		this.xExpression = xExpression;
		this.qualifiedMapping = qualifiedMapping;
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
		if (result.getException()!=null) throw result.getException();
		return result.getResult();
	}

}
