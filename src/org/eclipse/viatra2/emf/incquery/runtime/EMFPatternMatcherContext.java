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

package org.eclipse.viatra2.emf.incquery.runtime;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext;



/**
 * TODO generics? 
 * TODO TODO no subtyping between EDataTypes? no EDataTypes metainfo at all?
 * @author Bergmann Gábor
 */
public class EMFPatternMatcherContext<PatternDescription> implements IPatternMatcherContext<PatternDescription> {
		
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
		if (typeObject instanceof EClass || typeObject instanceof EDataType) return enumerateDirectUnarySupertypes(typeObject);
		if (typeObject instanceof EStructuralFeature) return enumerateDirectBinaryEdgeSupertypes(typeObject);
		else throw new IllegalArgumentException("typeObject has invalid type " + typeObject.getClass().getName());
	}
	
	@Override
	public Collection<? extends Object> enumerateDirectSubtypes(Object typeObject) {
		if (typeObject instanceof EClass || typeObject instanceof EDataType) return enumerateDirectUnarySubtypes(typeObject);
		if (typeObject instanceof EStructuralFeature) return enumerateDirectBinaryEdgeSubtypes(typeObject);
		else throw new IllegalArgumentException("typeObject has invalid type " + typeObject.getClass().getName());
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

	// Logging support
	public interface Logger {
		public void logDebug(String message);
		public void logError(String message);
		public void logError(String message, Throwable cause);
		public void logWarning(String message);
		public void logWarning(String message, Throwable cause);
	}	
	Logger logger;
	public Logger getLogger() {
		return logger;
	}
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	@Override
	public void logDebug(String message) {
		if (logger!=null) logger.logDebug(message);
	}

	@Override
	public void logError(String message) {
		if (logger!=null) logger.logError(message);
	}

	@Override
	public void logError(String message, Throwable cause) {
		if (logger!=null) logger.logError(message, cause);
	}
	
	 /* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext#logWarning(java.lang.String)
	 */
	@Override
	public void logWarning(String message) {
		if (logger!=null) logger.logWarning(message);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext#logWarning(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void logWarning(String message, Throwable cause) {
		if (logger!=null) logger.logWarning(message, cause);
	}




}
