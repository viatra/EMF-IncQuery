/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.emf.types;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.ClassType;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.EnumValue;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.ReferenceType;
import org.eclipse.incquery.patternlanguage.patternLanguage.AggregatedValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.BoolValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.ComputationValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.CountAggregator;
import org.eclipse.incquery.patternlanguage.patternLanguage.DoubleValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.IntValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.ListValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.LiteralValueReference;
import org.eclipse.incquery.patternlanguage.patternLanguage.PathExpressionTail;
import org.eclipse.incquery.patternlanguage.patternLanguage.StringValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.Type;
import org.eclipse.incquery.patternlanguage.patternLanguage.ValueReference;

/**
 * A common utility class for basic type checking methods.
 */
public class EMFPatternTypeUtil {

    /**
     * @param valueReference
     * @return an {@link EClassifier} for the given input {@link ValueReference}. The ValueReference can be a
     *         {@link LiteralValueReference}, or a {@link ComputationValue}.
     */
    public static EClassifier getClassifierForLiteralComputationEnumValueReference(ValueReference valueReference) {
        if (valueReference instanceof LiteralValueReference) {
            if (valueReference instanceof IntValue) {
                return EcorePackage.Literals.EINT;
            } else if (valueReference instanceof StringValue) {
                return EcorePackage.Literals.ESTRING;
            } else if (valueReference instanceof BoolValue) {
                return EcorePackage.Literals.EBOOLEAN;
            } else if (valueReference instanceof DoubleValue) {
                return EcorePackage.Literals.EDOUBLE;
            } else if (valueReference instanceof ListValue) {
                return null;
            }
        } else if (valueReference instanceof AggregatedValue) {
            AggregatedValue aggregatedValue = (AggregatedValue) valueReference;
            if (aggregatedValue.getAggregator() instanceof CountAggregator) {
                return EcorePackage.Literals.EINT;
            }
        } else if (valueReference instanceof EnumValue) {
            EnumValue enumValue = (EnumValue) valueReference;
            return enumValue.getEnumeration();
        }
        return null;
    }

    public static Type getTypeFromPathExpressionTail(PathExpressionTail pathExpressionTail) {
        if (pathExpressionTail == null) {
            return null;
        }
        if (pathExpressionTail.getTail() != null) {
            return getTypeFromPathExpressionTail(pathExpressionTail.getTail());
        }
        return pathExpressionTail.getType();
    }

    public static EClassifier getClassifierForType(Type type) {
        EClassifier result = null;
        if (type != null) {
            if (type instanceof ClassType) {
                result = ((ClassType) type).getClassname();
            } else if (type instanceof ReferenceType) {
                EStructuralFeature feature = ((ReferenceType) type).getRefname();
                if (feature instanceof EAttribute) {
                    EAttribute attribute = (EAttribute) feature;
                    result = attribute.getEAttributeType();
                } else if (feature instanceof EReference) {
                    EReference reference = (EReference) feature;
                    result = reference.getEReferenceType();
                }
            }
        }
        return result;
    }

}
