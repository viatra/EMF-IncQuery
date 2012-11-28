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
package org.eclipse.incquery.runtime.base.api;

/**
 * Listener interface for receiving notification from {@link QueryResultMultimap}
 *   
 * @author Abel Hegedus
 *
 * @param <KeyType>
 * @param <ValueType>
 */
public interface IQueryResultUpdateListener<KeyType, ValueType>{
    /**
     * This method is called by the query result multimap when a new key-value pair is put into the multimap
     * 
     * <p> Only invoked if the contents of the multimap changed!
     * 
     * @param key the key of the newly inserted pair
     * @param value the value of the newly inserted pair
     */
    void notifyPut(KeyType key, ValueType value);
    
    /**
     * This method is called by the query result multimap when key-value pair is removed from the multimap
     * 
     * <p> Only invoked if the contents of the multimap changed!
     * 
     * @param key the key of the removed pair
     * @param value the value of the removed pair
     */
    void notifyRemove(KeyType key, ValueType value);
}