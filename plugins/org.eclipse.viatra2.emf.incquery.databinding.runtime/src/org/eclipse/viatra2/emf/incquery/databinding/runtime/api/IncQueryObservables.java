/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.databinding.runtime.api;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.databinding.runtime.adapter.DatabindingAdapterUtil;
import org.eclipse.viatra2.emf.incquery.databinding.runtime.collection.ObservablePatternMatchList;
import org.eclipse.viatra2.emf.incquery.databinding.runtime.collection.ObservablePatternMatchSet;

/**
 * Utility class for observing EMF-IncQuery related objects, such as match sets,
 * match parameters.
 * 
 * @author Abel Hegedus
 *
 */
public class IncQueryObservables {

    /**
     * Hidden constructor for utility class
     */
    private IncQueryObservables() {
        
    }
    
    /**
     * Create an observable list of the match set of the given query on the selected notifier.
     * 
     * <p>The matches are ordered by appearance, so a new match is always put on the end of the list.
     * 
     * <p>Use the generated matcher factories for initialization, in the generic case, you may have to
     * accept an unchecked invocation (or use the Generic classes if you are sure).
     * 
     * @param factory the matcher factory for the query to observe
     * @param notifier the notifier to use for the matcher
     * @return an observable list of matches
     */
    public static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> IObservableList observeMatchesAsList(IMatcherFactory<Matcher> factory, Notifier notifier) {
        return new ObservablePatternMatchList<Match>(factory, notifier);
    }
    
    /**
     * Create an observable set of the match set of the given query on the selected notifier.
     * 
     * <p>Use the generated matcher factories for initialization, in the generic case, you may have to
     * accept an unchecked invocation (or use the Generic classes if you are sure).
     * 
     * @param factory the matcher factory for the query to observe
     * @param notifier the notifier to use for the matcher
     * @return an observable set of matches
     */
    public static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> IObservableSet observeMatchesAsSet(IMatcherFactory<Matcher> factory, Notifier notifier) {
        return new ObservablePatternMatchSet<Match>(factory, notifier);
    }

    /**
     * Registers the given changeListener for the appropriate features of the given signature.
     * The features will be computed based on the message parameter.
     * 
     * @param signature the signature instance
     * @param changeListener the changle listener 
     * @param message the message which can be found in the appropriate PatternUI annotation
     * @return the list of IObservableValue instances for which the IValueChangeListener was registered
     */
    public static List<IObservableValue> observeFeatures(IPatternMatch match, IValueChangeListener changeListener, String message) {
    	List<IObservableValue> affectedValues = new ArrayList<IObservableValue>();
    	if (message != null) {
    		String[] tokens = message.split("\\$");
    
    		for (int i = 0; i < tokens.length; i++) {
    			
    			//odd tokens 
    			if (i % 2 != 0) {
    				IObservableValue value = IncQueryObservables.getObservableValue(match, tokens[i]);
    				if (value != null) {
    					value.addValueChangeListener(changeListener);
    					affectedValues.add(value);
    				}
    			}
    		}
    	}
    	return affectedValues;
    }

    /**
     * Registers the given change listener on the given object's all accessible fields. 
     * This function uses Java Reflection.
     * 
     * @param changeListener the changle listener 
     * @param object the observed object
     * @return the list of IObservableValue instances for which the IValueChangeListener was registered
     */
    public static List<IObservableValue> observeAllAttributes(IValueChangeListener changeListener, Object object) {
    	List<IObservableValue> affectedValues = new ArrayList<IObservableValue>();
    	if (object instanceof EObject) {
    		for (EStructuralFeature feature : ((EObject) object).eClass().getEAllStructuralFeatures()) {
    			IObservableValue val = EMFProperties.value(feature).observe(object);
    			affectedValues.add(val);
    			val.addValueChangeListener(changeListener);
    		}
    	}
    	return affectedValues;
    }

    /**
     * Returns an IObservableValue for the given match based on the given expression.
     * If an attribute is not present in the expression than it tries with the 'name' attribute.
     * If it is not present the returned value will be null.
     * 
     * @param match the match object
     * @param expression the expression
     * @return IObservableValue instance or null 
     */
    public static IObservableValue getObservableValue(IPatternMatch match, String expression) {
    	IObservableValue val = null;
    	String[] objectTokens = expression.split("\\.");
    	
    	if (objectTokens.length > 0) {
    		Object o = null;
    		EStructuralFeature feature = null;
    		
    		if (objectTokens.length == 2) {
    			o = match.get(objectTokens[0]);
    			feature = DatabindingAdapterUtil.getFeature(o, objectTokens[1]);
    		}
    		if (objectTokens.length == 1) {
    			o = match.get(objectTokens[0]);
    			feature = DatabindingAdapterUtil.getFeature(o, "name");
    		}
    		if (o != null && feature != null) {
    			val = EMFProperties.value(feature).observe(o);
    		}
    	}
    	
    	return val;
    }
    
    
}
