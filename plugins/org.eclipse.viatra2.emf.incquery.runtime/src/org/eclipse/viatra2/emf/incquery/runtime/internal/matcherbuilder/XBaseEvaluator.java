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

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.viatra2.emf.incquery.runtime.IExtensions;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatchChecker;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.runtime.internal.XtextInjectorProvider;
import org.eclipse.viatra2.emf.incquery.runtime.util.ClassLoaderUtil;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.AbstractEvaluator;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.CheckConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
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
			interpreter = (XbaseInterpreter) injector
					.getInstance(IExpressionInterpreter.class);
			try {
				interpreter.setClassLoader(ClassLoaderUtil
						.getClassLoader(getIFile(pattern)));
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

	private static IFile getIFile(Pattern pattern) {
		Resource resource = pattern.eResource();
		if (resource != null) {
			URI uri = resource.getURI();
			uri = resource.getResourceSet().getURIConverter().normalize(uri);
			String scheme = uri.scheme();
			if ("platform".equals(scheme) && uri.segmentCount() > 1
					&& "resource".equals(uri.segment(0))) {
				StringBuffer platformResourcePath = new StringBuffer();
				for (int j = 1, size = uri.segmentCount(); j < size; ++j) {
					platformResourcePath.append('/');
					platformResourcePath.append(uri.segment(j));
				}
				return ResourcesPlugin.getWorkspace().getRoot()
						.getFile(new Path(platformResourcePath.toString()));
			}
		}
		return null;
	}

	private static String getExpressionID(Pattern pattern,
			XExpression xExpression) {
		// FIXME do it, share it with the xtend plugin.xml generator!!
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
