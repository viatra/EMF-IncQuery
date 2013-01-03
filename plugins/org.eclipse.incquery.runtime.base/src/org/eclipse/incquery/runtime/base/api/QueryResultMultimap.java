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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.base.itc.alg.incscc.Direction;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

/**
 * Implementation of {@link Multimap} interface to represent query results. A query result multimap takes a model query,
 * with a specified key (input/source) parameter and a value (output/target) parameter.
 * 
 * <p>
 * Apart from the standard multimap behavior, it is possible to register listeners that are notified when the contents
 * of the multimap change.
 * 
 * <p>
 * Subclasses of the query result multimap can attach to any query evaluation engine and shoug
 * 
 * @author Abel Hegedus
 * 
 * @param <KeyType>
 *            the type of the keys stored in the multimap
 * @param <ValueType>
 *            the type of the values stored in the multimap
 */
public abstract class QueryResultMultimap<KeyType, ValueType> extends QueryResultAssociativeStore<KeyType, ValueType> implements Multimap<KeyType, ValueType> {

    /**
     * This multimap contains the current key-values. Implementing classes should not modify it directly
     */
    private Multimap<KeyType, ValueType> cache;
    
    /**
     * Constructor only visible to subclasses.
     * 
     * @param logger
     *            a logger that can be used for error reporting
     */
    protected QueryResultMultimap(Logger logger) {
        cache = HashMultimap.create();
        this.logger = logger;
    }

    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.base.api.QueryResultAssociativeStore#getCacheEntries()
     */
    @Override
    protected Collection<Entry<KeyType, ValueType>> getCacheEntries() {
        return cache.entries();
    }

    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.base.api.QueryResultAssociativeStore#internalCachePut(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean internalCachePut(KeyType key, ValueType value) {
        return cache.put(key, value);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.base.api.QueryResultAssociativeStore#internalCacheRemove(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean internalCacheRemove(KeyType key, ValueType value) {
        return cache.remove(key, value);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.base.api.QueryResultAssociativeStore#internalCacheContainsEntry(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean internalCacheContainsEntry(KeyType key, ValueType value) {
        return cache.containsEntry(key, value);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.base.api.QueryResultAssociativeStore#internalCacheSize()
     */
    @Override
    protected int internalCacheSize() {
        return cache.size();
    }
    
    /**
     * @return the cache
     */
    protected Multimap<KeyType, ValueType> getCache() {
        return cache;
    }

    /**
     * @param cache
     *            the cache to set
     */
    protected void setCache(Multimap<KeyType, ValueType> cache) {
        this.cache = cache;
    }

    // ======================= implemented Multimap methods ======================

    /*
     * (non-Javadoc)
     * 
     * @see com.google.common.collect.Multimap#size()
     */
    @Override
    public int size() {
        return cache.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.common.collect.Multimap#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.common.collect.Multimap#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(Object key) {
        return cache.containsKey(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.common.collect.Multimap#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(Object value) {
        return cache.containsValue(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.common.collect.Multimap#containsEntry(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean containsEntry(Object key, Object value) {
        return cache.containsEntry(key, value);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * The returned collection is immutable.
     * 
     */
    @Override
    public Collection<ValueType> get(KeyType key) {
        return Collections.unmodifiableCollection(cache.get(key));
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
     * The returned multiset is immutable.
     * 
     */
    @Override
    public Multiset<KeyType> keys() {
        return Multisets.unmodifiableMultiset(cache.keys()); // cache.keys();
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

    /**
     * {@inheritDoc}
     * 
     * <p>
     * The returned collection is immutable.
     * 
     */
    @Override
    public Collection<Entry<KeyType, ValueType>> entries() {
        return Collections.unmodifiableCollection(getCacheEntries());
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * The returned map is immutable.
     * 
     */
    @Override
    public Map<KeyType, Collection<ValueType>> asMap() {
        return Collections.unmodifiableMap(cache.asMap());
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Throws {@link UnsupportedOperationException} if there is no {@link IQueryResultSetter}
     */
    @Override
    public boolean put(KeyType key, ValueType value) {
        if (setter == null) {
            throw new UnsupportedOperationException(NOT_ALLOW_MODIFICATIONS);
        }
        return modifyThroughQueryResultSetter(key, value, Direction.INSERT);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Throws {@link UnsupportedOperationException} if there is no {@link IQueryResultSetter}
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object key, Object value) {
        if (setter == null) {
            throw new UnsupportedOperationException(NOT_ALLOW_MODIFICATIONS);
        }
        // if it contains the entry, the types MUST be correct
        if (cache.containsEntry(key, value)) {
            return modifyThroughQueryResultSetter((KeyType) key, (ValueType) value, Direction.DELETE);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Throws {@link UnsupportedOperationException} if there is no {@link IQueryResultSetter}
     */
    @Override
    public boolean putAll(KeyType key, Iterable<? extends ValueType> values) {
        if (setter == null) {
            throw new UnsupportedOperationException(NOT_ALLOW_MODIFICATIONS);
        }
        boolean changed = false;
        for (ValueType value : values) {
            changed |= modifyThroughQueryResultSetter(key, value, Direction.INSERT);
        }
        return changed;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Throws {@link UnsupportedOperationException} if there is no {@link IQueryResultSetter}
     */
    @Override
    public boolean putAll(Multimap<? extends KeyType, ? extends ValueType> multimap) {
        if (setter == null) {
            throw new UnsupportedOperationException(NOT_ALLOW_MODIFICATIONS);
        }
        boolean changed = false;
        for (Entry<? extends KeyType, ? extends ValueType> entry : multimap.entries()) {
            changed |= modifyThroughQueryResultSetter(entry.getKey(), entry.getValue(), Direction.INSERT);
        }
        return changed;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Throws {@link UnsupportedOperationException} if there is no {@link IQueryResultSetter}
     * 
     * <p>
     * The returned collection is immutable.
     */
    @Override
    public Collection<ValueType> replaceValues(KeyType key, Iterable<? extends ValueType> values) {
        if (setter == null) {
            throw new UnsupportedOperationException(NOT_ALLOW_MODIFICATIONS);
        }
        Collection<ValueType> oldValues = removeAll(key);
        Iterator<? extends ValueType> iterator = values.iterator();
        Collection<ValueType> notInserted = Lists.newArrayList();
        while (iterator.hasNext()) {
            ValueType value = iterator.next();
            if(!modifyThroughQueryResultSetter(key, value, Direction.INSERT)) {
                notInserted.add(value);
            }
        }
        if (!notInserted.isEmpty()) {
            logger.warn(String
                    .format("The query result multimap replaceValues on key %s did not insert values %s. (Developer note: %s called from QueryResultMultimap)",
                            key, notInserted.toString(), setter));
        }
        return oldValues;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Throws {@link UnsupportedOperationException} if there is no {@link IQueryResultSetter}
     * 
     * <p>
     * The returned collection is immutable.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Collection<ValueType> removeAll(Object key) {
        if (setter == null) {
            throw new UnsupportedOperationException(NOT_ALLOW_MODIFICATIONS);
        }
        // if it contains the key, the type MUST be correct
        if (cache.containsKey(key)) {
            Collection<ValueType> collection = cache.get((KeyType) key);
            Collection<ValueType> output = ImmutableSet.copyOf(collection);

            for (ValueType valueType : output) {
                modifyThroughQueryResultSetter((KeyType) key, valueType, Direction.DELETE);
            }
            if (cache.containsKey(key)) {
                Collection<ValueType> newValues = cache.get((KeyType) key);
                logger.warn(String
                        .format("The query result multimap removeAll on key %s did not remove all values (the following remained: %s). (Developer note: %s called from QueryResultMultimap)",
                                key, newValues, setter));
            }
            return output;
        }
        return Collections.EMPTY_SET;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Throws {@link UnsupportedOperationException} if there is no {@link IQueryResultSetter}
     */
    @Override
    public void clear() {
        internalClear();
    }

}
