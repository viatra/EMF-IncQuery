/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.databinding.runtime;

import java.util.Map;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.viatra2.emf.incquery.databinding.runtime.util.DatabindingAdapterUtil;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

public class GenericDatabindingAdapter extends DatabindingAdapter<IPatternMatch> {

    private Map<String, String> parameterMap;
	
    public GenericDatabindingAdapter(Map<String, String> parameterMap) {
        this.parameterMap = parameterMap;
	}

    public GenericDatabindingAdapter(Pattern pattern) {
        this.parameterMap = DatabindingAdapterUtil.calculateObservableValues(pattern);
    }
	
	@Override
	public String[] getParameterNames() {
		return parameterMap.keySet().toArray(new String[parameterMap.keySet().size()]);
	}

	@Override
	public IObservableValue getObservableParameter(IPatternMatch match,	String parameterName) {
		if (parameterMap.size() > 0) {
			String expression = parameterMap.get(parameterName);
			return DatabindingAdapterUtil.getObservableValue(match, expression);
		}
		return null;
	}

}