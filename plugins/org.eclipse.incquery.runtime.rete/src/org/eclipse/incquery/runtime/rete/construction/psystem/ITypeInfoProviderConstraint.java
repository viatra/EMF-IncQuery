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

package org.eclipse.incquery.runtime.rete.construction.psystem;

/**
 * @author Bergmann Gábor
 * 
 */
public interface ITypeInfoProviderConstraint {

    /**
     * Returns type information that is known about the variable if this constraint holds. Null if no such information
     * is discernible.
     * 
     * @param variable
     * @return the inferred type information, or TypeInfoSpecials.NO_TYPE_INFO_PROVIDED if no type information is
     *         inferrable. Never returns null.
     */
    public Object getTypeInfo(PVariable variable);

    public static enum TypeInfoSpecials {
        NO_TYPE_INFO_PROVIDED, ANY_UNARY, ANY_TERNARY;

        public static Object wrapUnary(Object typeKey) {
            return typeKey == null ? ITypeInfoProviderConstraint.TypeInfoSpecials.ANY_UNARY : typeKey;
        }

        public static Object unwrapUnary(Object typeKey) {
            return typeKey == ITypeInfoProviderConstraint.TypeInfoSpecials.ANY_UNARY ? null : typeKey;
        }

        public static Object wrapTernary(Object typeKey) {
            return typeKey == null ? ITypeInfoProviderConstraint.TypeInfoSpecials.ANY_TERNARY : typeKey;
        }

        public static Object unwrapTernary(Object typeKey) {
            return typeKey == ITypeInfoProviderConstraint.TypeInfoSpecials.ANY_TERNARY ? null : typeKey;
        }

        public static Object wrapAny(Object typeKey) {
            return typeKey == null ? ITypeInfoProviderConstraint.TypeInfoSpecials.NO_TYPE_INFO_PROVIDED : typeKey;
        }
    }

}
