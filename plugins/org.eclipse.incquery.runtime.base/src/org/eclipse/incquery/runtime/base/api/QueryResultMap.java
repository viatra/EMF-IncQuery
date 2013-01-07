/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.base.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.base.itc.alg.incscc.Direction;

/**
 * @author Abel Hegedus
 *
 */
public abstract class QueryResultMap<KeyType,ValueType> extends QueryResultAssociativeStore<KeyType, ValueType> implements Map<KeyType, ValueType> {

    /**
     * This map contains the current key-values. Implementing classes should not modify it directly
     */
    private Map<KeyType, ValueType> cache;
    
    /**
     * Constructor only visible to subclasses.
     * 
     * @param logger
     *            a logger that can be used for error reporting
     */
    protected QueryResultMap(Logger logger) {
        cache = new HashMap<KeyType, ValueType>();
        setLogger(logger);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.base.api.QueryResultAssociativeStore#getCacheEntries()
     */
    @Override
    protected Collection<java.util.Map.Entry<KeyType, ValueType>> getCacheEntries() {
        return cache.entrySet();
    }

    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.base.api.QueryResultAssociativeStore#internalCachePut(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean internalCachePut(KeyType key, ValueType value) {
        ValueType put = cache.put(key, value);
        if(put == null) {
            return value != null;
        } else {
            return !put.equals(value);
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.base.api.QueryResultAssociativeStore#internalCacheRemove(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean internalCacheRemove(KeyType key, ValueType value) {
        ValueType remove = cache.remove(key);
        return remove != null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.base.api.QueryResultAssociativeStore#internalCacheSize()
     */
    @Override
    protected int internalCacheSize() {
        return cache.size();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.base.api.QueryResultAssociativeStore#internalCacheContainsEntry(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean internalCacheContainsEntry(KeyType key, ValueType value) {
        return cache.get(key).equals(value);
    }
    
    /**
     * @return the cache
     */
    protected Map<KeyType, ValueType> getCache() {
        return cache;
    }

    /**
     * @param cache
     *            the cache to set
     */
    protected void setCache(Map<KeyType, ValueType> cache) {
        this.cache = cache;
    }
    
    // ======================= implemented Map methods ======================

    /* (non-Javadoc)
     * @see java.util.Map#clear()
     */
    @Override
    public void clear() {
        internalClear();
    }

    /* (non-Javadoc)
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(Object key) {
        return cache.containsKey(key);
    }

    /* (non-Javadoc)
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(Object value) {
        return cache.containsValue(value);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * The returned set is immutable.
     * 
     */
    @Override
    public Set<Entry<KeyType, ValueType>> entrySet() {
        return Collections.unmodifiableSet((Set<Entry<KeyType, ValueType>>) getCacheEntries());
    }

    /* (non-Javadoc)
     * @see java.util.Map#get(java.lang.Object)
     */
    @Override
    public ValueType get(Object key) {
        return cache.get(key);
    }

    /* (non-Javadoc)
     * @see java.util.Map#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * The returned set is immutable.
     * 
     */
    @Override
    public Set<KeyType> keySet() {
        return Collections.unmodifiableSet(cache.keySet());
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Throws {@link UnsupportedOperationException} if there is no {@link IQueryResultSetter}
     */
    @Override
    public ValueType put(KeyType key, ValueType value) {
        if (getSetter() == null) {
            throw new UnsupportedOperationException(NOT_ALLOW_MODIFICATIONS);
        }
        ValueType oldValue = cache.get(key);
        boolean modified = modifyThroughQueryResultSetter(key, value, Direction.INSERT);
        return modified ? oldValue : null;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Throws {@link UnsupportedOperationException} if there is no {@link IQueryResultSetter}
     */
    @Override
    public void putAll(Map<? extends KeyType, ? extends ValueType> map) {
        if (getSetter() == null) {
            throw new UnsupportedOperationException(NOT_ALLOW_MODIFICATIONS);
        }
        for (Entry<? extends KeyType, ? extends ValueType> entry : map.entrySet()) {
            modifyThroughQueryResultSetter(entry.getKey(), entry.getValue(), Direction.INSERT);
        }
        return;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Throws {@link UnsupportedOperationException} if there is no {@link IQueryResultSetter}
     */
    @SuppressWarnings("unchecked")
    @Override
    public ValueType remove(Object key) {
        if (getSetter() == null) {
            throw new UnsupportedOperationException(NOT_ALLOW_MODIFICATIONS);
        }
        // if it contains the entry, the types MUST be correct
        if (cache.containsKey(key)) {
            ValueType value = cache.get(key);
            modifyThroughQueryResultSetter((KeyType) key, value, Direction.DELETE);
            return value;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.Map#size()
     */
    @Override
    public int size() {
        return internalCacheSize();
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * The returned collection is immutable.
     * 
     */
    @Override
    public Collection<ValueType> values() {
        return Collections.unmodifiableCollection(cache.values());
    }

}
