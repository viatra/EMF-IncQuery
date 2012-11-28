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


package org.eclipse.incquery.runtime.rete.matcher;

import java.util.Collection;


/**
 * Represents all knowledge of the outside world towards the pattern matcher, 
 * but without specific run-time or build-time information.
 * 
 * @author Bergmann Gábor
 *
 * @param <PatternDescription> the type describing a pattern
 */
public interface IPatternMatcherContext<PatternDescription> {
		
	/**
	 * @return TERNARY if edges have their own identity, BINARY if they are only pairs of source and target
	 */
	EdgeInterpretation edgeInterpretation();
	public enum EdgeInterpretation {
		TERNARY /*VPM*/,
		BINARY /*EMF*/
	}
	
	
	Object ternaryEdgeTargetType(Object typeObject);	// TODO global supertypes?
	Object ternaryEdgeSourceType(Object typeObject);	// TODO global supertypes?
	Object binaryEdgeTargetType(Object typeObject);	// TODO global supertypes?
	Object binaryEdgeSourceType(Object typeObject);	// TODO global supertypes?
	
	
	/**
	 * @return the direction in which  sub/supertypes arequeryable
	 */
	GeneralizationQueryDirection allowedGeneralizationQueryDirection();
	public enum GeneralizationQueryDirection {
		/**
		 * Supertypes can be queried. Notifications registered for a supertype will be delivered for all subtypes. 
		 */
		SUPERTYPE_ONLY_SMART_NOTIFICATIONS /*EMF based on IncQuery-Base*/,
		/**
		 * Supertypes can be queried. Notifications are delivered for the registered types. 
		 */
		SUPERTYPE_ONLY /*EMF legacy*/,
		/**
		 * Supertypes and subtypes can be queried. Notifications are delivered for the registered types. 
		 */
		BOTH /*VPM*/
	}	
	
	boolean isUnaryType(Object typeObject);
	Collection<? extends Object> enumerateDirectUnarySubtypes(Object typeObject);
	Collection<? extends Object> enumerateDirectUnarySupertypes(Object typeObject);
	boolean isTernaryEdgeType(Object typeObject);
	Collection<? extends Object> enumerateDirectTernaryEdgeSubtypes(Object typeObject);
	Collection<? extends Object> enumerateDirectTernaryEdgeSupertypes(Object typeObject);
	boolean isBinaryEdgeType(Object typeObject);
	Collection<? extends Object> enumerateDirectBinaryEdgeSubtypes(Object typeObject);	
	Collection<? extends Object> enumerateDirectBinaryEdgeSupertypes(Object typeObject);
	
	Collection<? extends Object> enumerateDirectSupertypes(Object typeObject);
	Collection<? extends Object> enumerateDirectSubtypes(Object typeObject);

//	boolean checkBelowContainer(Object container, Object contained);
//	boolean checkInContainer(Object container, Object contained);
	
	void reportPatternDependency(PatternDescription pattern);
	
	//Logger getLogger();
	void logFatal(String message);
	void logFatal(String message, Throwable cause);
	void logError(String message);
	void logError(String message, Throwable cause);
	void logWarning(String message);
	void logWarning(String message, Throwable cause);
	void logDebug(String message);


	String printPattern(PatternDescription pattern);
	String printType(Object typeObject);


	
	
}
