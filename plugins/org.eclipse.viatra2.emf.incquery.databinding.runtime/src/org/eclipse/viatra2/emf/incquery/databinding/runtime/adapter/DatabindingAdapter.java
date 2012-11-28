/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.databinding.runtime.adapter;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.ValueProperty;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.databinding.runtime.api.IncQueryObservables;

import com.google.common.base.Preconditions;

/**
 * The class is used to observe given parameters of a pattern.
 * 
 * @author Tamas Szabo
 *
 * @param <T> the type parameter of the match
 */
public abstract class DatabindingAdapter<T extends IPatternMatch> {
	
    protected class MatcherProperty extends ValueProperty {

        private String expression;

        public MatcherProperty(String expression) {
            this.expression = expression;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.core.databinding.property.value.IValueProperty#getValueType()
         */
        @Override
        public Object getValueType() {
            return Object.class;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.core.databinding.property.value.IValueProperty#observe(org.eclipse.core.databinding.observable
         * .Realm, java.lang.Object)
         */
        @Override
        public IObservableValue observe(Realm realm, Object source) {
            Preconditions.checkArgument((source instanceof IPatternMatch), "Source must be a typed Pattern Match");
            return IncQueryObservables.getObservableValue((IPatternMatch) source, expression);
        }

    }

	/**
	 * Returns the array of observable values.
	 * 
	 * @return the array of values
	 */
	public abstract String[] getParameterNames();
	
	/**
	 * Returns an observable value for the given match and parameterName.
	 * 
	 * @param match the match object
	 * @param parameterName the parameter name
	 * @return an observable value
	 */
	public abstract IObservableValue getObservableParameter(T match, String parameterName);
}
