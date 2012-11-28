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

package org.eclipse.incquery.runtime.internal.matcherbuilder;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.IExtensions;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IMatchChecker;
import org.eclipse.incquery.runtime.internal.XtextInjectorProvider;
import org.eclipse.incquery.runtime.rete.boundary.AbstractEvaluator;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;
import org.eclipse.incquery.runtime.util.CheckExpressionUtil;
import org.eclipse.incquery.runtime.util.ClassLoaderUtil;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.interpreter.IEvaluationContext;
import org.eclipse.xtext.xbase.interpreter.IEvaluationResult;
import org.eclipse.xtext.xbase.interpreter.IExpressionInterpreter;
import org.eclipse.xtext.xbase.interpreter.impl.XbaseInterpreter;

import com.google.inject.Injector;
import com.google.inject.Provider;

/**
 * Evaluates an XBase XExpression inside Rete.
 * 
 * @author Bergmann Gábor
 */
public class XBaseEvaluator extends AbstractEvaluator {
	private final XExpression xExpression;
	private final Map<String, Integer> tupleNameMap;
	private final Pattern pattern;

	private IMatchChecker matchChecker;

	private XbaseInterpreter interpreter;
	private Provider<IEvaluationContext> contextProvider;

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
			if (id.equals(CheckExpressionUtil.getExpressionUniqueID(pattern,
					xExpression))) {
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
			interpreter = (XbaseInterpreter) injector
					.getInstance(IExpressionInterpreter.class);
			try {
				ClassLoader classLoader = ClassLoaderUtil
						.getClassLoader(CheckExpressionUtil.getIFile(pattern));
				if (classLoader != null) {
					interpreter.setClassLoader(ClassLoaderUtil
							.getClassLoader(CheckExpressionUtil
									.getIFile(pattern)));
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			contextProvider = injector.getProvider(IEvaluationContext.class);
		}
	}

	@Override
	public Object doEvaluate(Tuple tuple) throws Throwable {
		// First option: try to evalute with the generated code
		if (matchChecker != null) {
			return matchChecker.evaluateXExpression(tuple, tupleNameMap);
		}

		// Second option: try to evaluate with the interpreted approach
		IEvaluationContext context = contextProvider.get();
		for (Entry<String, Integer> entry : tupleNameMap.entrySet()) {
			context.newValue(QualifiedName.create(entry.getKey()),
					tuple.get(entry.getValue()));
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
