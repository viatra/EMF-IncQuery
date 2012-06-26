/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.runtime.internal;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.EMFIncQueryRuntimeLogger;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext;

/**
 * TODO generics? 
 * TODO TODO no subtyping between EDataTypes? no EDataTypes metainfo at all?
 * @author Bergmann Gábor
 */
public class EMFPatternMatcherContext<PatternDescription> implements IPatternMatcherContext<PatternDescription> {
		
	protected IncQueryEngine iqEngine;
	
	/**
	 * @param iqEngine
	 */
	public EMFPatternMatcherContext(IncQueryEngine iqEngine) {
		super();
		this.iqEngine = iqEngine;
	}
	
	@Override
	public EdgeInterpretation edgeInterpretation() {
		return EdgeInterpretation.BINARY;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.IPatternMatcherContext#allowedGeneralizationQueryDirection()
	 */
	@Override
	public GeneralizationQueryDirection allowedGeneralizationQueryDirection() {
		return GeneralizationQueryDirection.SUPERTYPE_ONLY;
	}

	@Override
	public Collection<? extends Object> enumerateDirectSupertypes(Object typeObject) {
		if (typeObject==null) return null;
		if (typeObject instanceof EClass || typeObject instanceof EDataType) return enumerateDirectUnarySupertypes(typeObject);
		if (typeObject instanceof EStructuralFeature) return enumerateDirectBinaryEdgeSupertypes(typeObject);
		else throw new IllegalArgumentException("typeObject has invalid type " + typeObject.getClass().getName());
	}
	
	@Override
	public Collection<? extends Object> enumerateDirectSubtypes(Object typeObject) {
		if (typeObject==null) return null;
		if (typeObject instanceof EClass || typeObject instanceof EDataType) return enumerateDirectUnarySubtypes(typeObject);
		if (typeObject instanceof EStructuralFeature) return enumerateDirectBinaryEdgeSubtypes(typeObject);
		else throw new IllegalArgumentException("typeObject has invalid type " + typeObject.getClass().getName());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext#isUnaryType(java.lang.Object)
	 */
	@Override
	public boolean isUnaryType(Object typeObject) {
		return typeObject instanceof EClassifier;
	}	
	@Override
	public Collection<? extends Object> enumerateDirectUnarySubtypes(Object typeObject) {
		if (typeObject instanceof EClass) 
			throw new UnsupportedOperationException("EMF patternmatcher context only supports querying of supertypes, not subtypes.");
			//return contextMapping.retrieveSubtypes((EClass)typeObject);
		else if (typeObject instanceof EDataType) { 
			return Collections.emptyList();// no subtyping between EDataTypes?		
		} else throw new IllegalArgumentException("typeObject has invalid type " + typeObject.getClass().getName());
	}

	@Override
	public Collection<? extends Object> enumerateDirectUnarySupertypes(Object typeObject) {
		if (typeObject instanceof EClass) 
			return ((EClass)typeObject).getESuperTypes();
		else if (typeObject instanceof EDataType) {
			return Collections.emptyList();// no subtyping between EDataTypes?		
		} else throw new IllegalArgumentException("typeObject has invalid type " + typeObject.getClass().getName());
	}
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext#isBinaryType(java.lang.Object)
	 */
	@Override
	public boolean isBinaryEdgeType(Object typeObject) {
		return typeObject instanceof EStructuralFeature;
	}
	@Override
	public Collection<? extends Object> enumerateDirectBinaryEdgeSubtypes(
			Object typeObject) {
		return Collections.emptyList();// no subtyping between structural features
	}

	@Override
	public Collection<? extends Object> enumerateDirectBinaryEdgeSupertypes(
			Object typeObject) {
		return Collections.emptyList();// no subtyping between structural features
	}

	@Override
	public Collection<? extends Object> enumerateDirectTernaryEdgeSubtypes(
			Object typeObject) {
		throw new UnsupportedOperationException();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext#isTernaryType(java.lang.Object)
	 */
	@Override
	public boolean isTernaryEdgeType(Object typeObject) {
		return false;
	}
	@Override
	public Collection<? extends Object> enumerateDirectTernaryEdgeSupertypes(
			Object typeObject) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Object binaryEdgeSourceType(Object typeObject) {
		EStructuralFeature feature = (EStructuralFeature)typeObject;
		return feature.getEContainingClass();
	}

	@Override
	public Object binaryEdgeTargetType(Object typeObject) {
		if (typeObject instanceof EAttribute) {
			EAttribute attribute = (EAttribute) typeObject;
			return attribute.getEAttributeType();
		} else if (typeObject instanceof EReference) {
			EReference reference = (EReference) typeObject;
			return reference.getEReferenceType();
		} else throw new IllegalArgumentException("typeObject has invalid type " + typeObject.getClass().getName());
	}

	@Override
	public Object ternaryEdgeSourceType(Object typeObject) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object ternaryEdgeTargetType(Object typeObject) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reportPatternDependency(PatternDescription pattern) {
		// Ignore, because we don't support changing machines here
	}

	public EMFIncQueryRuntimeLogger getLogger() {
		return iqEngine.getLogger();
	}
	
	@Override
	public void logDebug(String message) {
		if (getLogger()!=null) getLogger().logDebug(message);
	}

	@Override
	public void logError(String message) {
		if (getLogger()!=null) getLogger().logError(message);
	}

	@Override
	public void logError(String message, Throwable cause) {
		if (getLogger()!=null) getLogger().logError(message, cause);
	}
	
	 /* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext#logWarning(java.lang.String)
	 */
	@Override
	public void logWarning(String message) {
		if (getLogger()!=null) getLogger().logWarning(message);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext#logWarning(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void logWarning(String message, Throwable cause) {
		if (getLogger()!=null) getLogger().logWarning(message, cause);
	}




}
