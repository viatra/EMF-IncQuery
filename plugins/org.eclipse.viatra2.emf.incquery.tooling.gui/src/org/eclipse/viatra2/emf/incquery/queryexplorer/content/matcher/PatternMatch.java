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

package org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher;

import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.viatra2.emf.incquery.databinding.runtime.DatabindingAdapterUtil;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DatabindingUtil;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;

/**
 * A PatternMatch is associated to every match of a matcher.
 * It is the lowest level element is the treeviewer.
 * 
 * @author Tamas Szabo
 *
 */
public class PatternMatch {

	private String text;
	private IPatternMatch match;
	private PatternMatcher parent;
	private String message;
	private ParameterValueChangedListener listener;
	private List<IObservableValue> affectedValues;
	
	public PatternMatch(PatternMatcher parent, IPatternMatch match) {
		this.parent = parent;
		this.match = match;
		this.message = DatabindingUtil.getMessage(match, parent.isGenerated());
		this.listener = new ParameterValueChangedListener();
		if (message != null) {
			setText(DatabindingAdapterUtil.getMessage(match, message));
			affectedValues = DatabindingAdapterUtil.observeFeatures(match, listener, message);
		}
		else {
			this.text = match.toString();
		}
	}
	
	public void dispose() {
		if (affectedValues != null) {
			for (IObservableValue val : affectedValues) {
				val.removeValueChangeListener(listener);
			}
		}
	}
	
	public void setText(String text) {
		this.text = text;
		String[] properties = new String[] {"text"};
		QueryExplorer.getInstance().getMatcherTreeViewer().update(this, properties);
	}

	public String getText() {
		return text;
	}

	public PatternMatcher getParent() {
		return parent;
	}
	
	private class ParameterValueChangedListener implements IValueChangeListener {
		@Override
		public void handleValueChange(ValueChangeEvent event) {
			setText(DatabindingAdapterUtil.getMessage(match, message));
		}
	}

	public IPatternMatch getPatternMatch() {
		return match;
	}
	
	public Object[] getLocationObjects() {
		return this.match.toArray();
	}
}
