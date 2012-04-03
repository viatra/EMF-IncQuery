/*******************************************************************************
 * Copyright (c) 2011 Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.patternlanguage.types;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Constraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.EntityType;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableReference;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassifierConstraint;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.util.TypeReferences;
import org.eclipse.xtext.xbase.lib.StringExtensions;
import org.eclipse.xtext.xbase.typing.XbaseTypeProvider;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class EMFPatternTypeProvider extends XbaseTypeProvider {

	private Logger logger = Logger.getLogger(getClass());
	
	@Inject
	private TypeReferences typeReferences;

	@Override
	protected JvmTypeReference typeForIdentifiable(
			JvmIdentifiableElement identifiable, boolean rawType) {
		if (identifiable instanceof Variable) {
			return _typeForIdentifiable((Variable) identifiable, rawType);
		}
		return super.typeForIdentifiable(identifiable, rawType);
	}

	protected JvmTypeReference _typeForIdentifiable(Variable variable,
			boolean rawType) {
		if (logger.isDebugEnabled()) {
			logger.debug("Calculating type for: " + variable.getName() + " type: " + variable.getType() + " eContainer: " + variable.eContainer());			
		}
		EcoreUtil2.resolveAll(variable);
		JvmTypeReference typeRef = resolveClassType(variable);
		if (typeRef == null) {
			typeRef = resolveClassType(variable.eContainer(), variable);
			if (typeRef == null) {
				typeRef = typeReferences.getTypeForName(Object.class, variable);
			}
		}
		return typeRef;
	}

	private JvmTypeReference resolveClassType(Variable variable) {
		// first try to get the type through the variable's type ref
		if (variable.getType() instanceof ClassType) {
			EClassifier classifier = ((ClassType) variable.getType())
					.getClassname();
			if (classifier != null && classifier.getInstanceClass() != null) {
				return typeReferences.getTypeForName(classifier.getInstanceClass(), variable);
			}
		}
		return null;
	}
	
	private JvmTypeReference resolveClassType(EObject eObject, Variable variable) {
		if (eObject instanceof Pattern) {
			Pattern pattern = (Pattern) eObject;
			for (PatternBody body : pattern.getBodies()) {
				JvmTypeReference typeRef = resolveClassType(body, variable);
				if (typeRef != null) {
					return typeRef;
				}
			}
		}	
		if (eObject instanceof PatternBody) {
			return resolveClassType((PatternBody) eObject, variable);
		}
		return null;
	}

	private JvmTypeReference resolveClassType(PatternBody body, Variable variable) {
		for (Constraint constraint : body.getConstraints()) {
			if (constraint instanceof EClassifierConstraint) {
				JvmTypeReference typeRef = getTypeRef(
						(EClassifierConstraint) constraint, variable);
				if (typeRef != null) {
					return typeRef;
				}
			}
		}		
		return null;
	}

	/**
	 * Returns the JvmTypeReference for variable if it used in EClassConstraint.
	 */
	private JvmTypeReference getTypeRef(EClassifierConstraint constraint,
			Variable variable) {
		if (variable == null) return null;
		EntityType entityType = constraint.getType();
		VariableReference variableRef = constraint.getVar();
		if (variableRef != null) {
			Variable variableRefVariable = variableRef.getVariable();
			if (variable.equals(variableRefVariable)) {
				return referenceFromEntityType(entityType, variable);
			} if (!StringExtensions.isNullOrEmpty(variableRef.getVar()) && variableRef
					.getVar().equals(variable.getName())) { 
				return referenceFromEntityType(entityType, variable);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("VariableRef not referring to parameter Variable!");
				}
			}
		}
		return null;
	}

	private JvmTypeReference referenceFromEntityType(EntityType entityType, Variable variable) {
		if (entityType instanceof ClassType) {
			Class<?> clazz = ((ClassType) entityType).getClassname()
					.getInstanceClass();
			if (clazz != null) {
				JvmTypeReference typeref = typeReferences.getTypeForName(clazz, variable);
				if (typeref != null) {
					return typeref;
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Clazz not found for " + entityType);
				}
			}
		}
		return null;
	}
}
