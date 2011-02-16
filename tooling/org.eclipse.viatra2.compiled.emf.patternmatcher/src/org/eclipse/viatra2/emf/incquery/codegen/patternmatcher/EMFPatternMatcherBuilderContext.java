/*******************************************************************************
 * Copyright (c) 2004-2009 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.codegen.patternmatcher;

import java.util.Map;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra2.core.IModelElement;
import org.eclipse.viatra2.core.IModelManager;
import org.eclipse.viatra2.emf.incquery.codegen.patternmatcher.mapping.ContextMapping;
import org.eclipse.viatra2.emf.incquery.codegen.patternmatcher.mapping.EMFMetamodelResolver;
import org.eclipse.viatra2.emf.incquery.runtime.EMFPatternMatcherContext;
import org.eclipse.viatra2.framework.IFramework;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherBuilderContext;



/**
 * 	Pattern matcher context over EMF resources with additional type information.
 *
 * TODO avoid the two hops in gen(resolve(FQN)), is gen(FQN) enough? generation could work without the bundles loaded!
 *
 * @author Bergmann Gábor
 * @param <PatternDescription>
 *
 */
public class EMFPatternMatcherBuilderContext<PatternDescription>
	extends EMFPatternMatcherContext<PatternDescription>
	implements IPatternMatcherBuilderContext<PatternDescription>
{

	IFramework fw;
	IModelManager modelManager;
	EMFMetamodelResolver metamodelResolver;
	ContextMapping contextMapping;

	/**
	 *
	 * @param fw
	 * @param supplementalEPackages (nsUri, EPackage) map of additionally resolved EPackages
	 */
	public EMFPatternMatcherBuilderContext(IFramework fw, Map<String, EPackage> supplementalEPackages) {
		super();
		this.fw = fw;
		this.modelManager = fw.getTopmodel().getModelManager();
		this.metamodelResolver = new EMFMetamodelResolver(fw, supplementalEPackages);
		this.contextMapping = ContextMapping.general();
	}

	@Override
	public Object resolveConstant(String fullyQualifiedName) {
		Object result = retrieveUnaryType(fullyQualifiedName);
		if (result==null) result = retrieveBinaryEdgeType(fullyQualifiedName);
		return result;
	}

	@Override
	public Object retrieveUnaryType(String fullyQualifiedName) {
		Object result = null;
		IModelElement element = modelManager.getElementByName(fullyQualifiedName);
		if (element != null) {
			result = metamodelResolver.resolveEClassifier(element); // first try the generic nEMF solution
			if (result==null) result = contextMapping.retrieveEntityType(fullyQualifiedName); // otherwise look for a contribution by a generated plugin
		}
		if (result!=null) return result;
		  else throw new IllegalArgumentException("Could not resolve to EClassifier: " + fullyQualifiedName);
	}




	@Override
	public Object retrieveBinaryEdgeType(String fullyQualifiedName) {
		Object result = null;
		IModelElement element = modelManager.getElementByName(fullyQualifiedName);
		if (element != null) {
			result = metamodelResolver.resolveEStructuralFeature(element);
			if (result==null) result = contextMapping.retrieveReferenceType(fullyQualifiedName);
		}
		if (result!=null) return result;
		  else throw new IllegalArgumentException("Could not resolve to EStructuralFeature: " + fullyQualifiedName);
	}


	@Override
	public Object retrieveTernaryEdgeType(String fullyQualifiedName) {
		throw new UnsupportedOperationException();
	}

}
