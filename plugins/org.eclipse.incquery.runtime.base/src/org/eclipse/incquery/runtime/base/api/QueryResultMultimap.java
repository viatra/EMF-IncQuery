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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.base.itc.alg.incscc.Direction;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

/**
 * Implementation of {@link Multimap} interface to represent query results. A query result multimap takes
 *  a model query, with a specified key (input/source) parameter and a value (output/target) parameter.
 *  
 *  <p> Apart from the standard multimap behavior, it is possible to register listeners that are notified when the
 *   contents of the multimap change.
 *   
 *  <p> Subclasses of the query result multimap can attach to any query evaluation engine and shoug
 * 
 * @author Abel Hegedus
 *
 * @param <KeyType> the type of the keys stored in the multimap
 * @param <ValueType> the type of the values stored in the multimap
 */
public abstract class QueryResultMultimap<KeyType, ValueType> implements Multimap<KeyType, ValueType> {

    /**
     * Error literal returned when multimap modification is attempted without a setter available
     */
    private static final String NOT_ALLOW_MODIFICATIONS = "Query result multimap does not allow modifications";
    /**
     * This multimap contains the current key-values.
     * Implementing classes should not modify it directly
     */
    private Multimap<KeyType, ValueType> cache;
    /**
     * Logger that can be used for reporting errors during runtime
     */
    private Logger logger;
    /**
     * The collection of listeners registered for this result multimap
     */
    private Collection<IQueryResultUpdateListener<KeyType, ValueType>> listeners;

    /**
     * The setter registered for changing the contents of the multimap
     */
    private IQueryResultSetter<KeyType, ValueType> setter;
    
    /**
     * Constructor only visible to subclasses.
     * 
     * @param logger a logger that can be used for error reporting
     */
    protected QueryResultMultimap(Logger logger){
        cache = HashMultimap.create();
        this.logger = logger;
    }
    
    /**
     * Registers a listener for this query result multimap that is invoked every time
     *  when a key-value pair is inserted or removed from the multimap.
     *  
     * <p> The listener can be unregistered via {@link #removeCallbackOnQueryResultUpdate(IQueryResultUpdateListener)}.
     * 
     * @param listener  the listener that will be notified of each key-value pair that is inserted or removed, starting from now. 
     * @param fireNow if true, notifyPut will be immediately invoked on all current key-values as a one-time effect.
     */
    public void addCallbackOnQueryResultUpdate(IQueryResultUpdateListener<KeyType, ValueType> listener, boolean fireNow) {
        if(listeners == null) {
            listeners = new HashSet<IQueryResultUpdateListener<KeyType,ValueType>>();
        }
        listeners.add(listener);
    }
    
    /**
     * Unregisters a callback registered by {@link #addCallbackOnQueryResultUpdate(IQueryResultUpdateListener, boolean)}.
     * 
     * @param listener the listener that will no longer be notified. 
     */
    public void removeCallbackOnQueryResultUpdate(IQueryResultUpdateListener<KeyType, ValueType> listener) {
        if(listeners != null) {
            listeners.remove(listener);
        }
    }
    
    /**
     * This method notifies the listeners that the query result multimap has changed.
     * 
     * @param direction the type of the change (insert or delete)
     * @param key the key of the pair that changed
     * @param value the value of the pair that changed
     */
    private void notifyListeners(Direction direction, KeyType key, ValueType value) {
        for (IQueryResultUpdateListener<KeyType, ValueType> listener : listeners) {
            try {
                if (direction == Direction.INSERT) {
                    listener.notifyPut(key, value);
                } else {
                    listener.notifyRemove(key, value);
                }
            } catch (Error e) { // NOPMD
                throw e;
            } catch (Throwable e) { // NOPMD
                logger.warn(
                        String.format(
                                "The query result multimap encountered an error during executing a callback on %s of key %s and value %s. Error message: %s. (Developer note: %s in %s called from QueryResultMultimap)",
                                direction == Direction.INSERT ? "insertion" : "removal", key, value, e.getMessage(), e
                                        .getClass().getSimpleName(), listener), e);
            }
        }
    }
    
    
    /**
     * Implementations of QueryResultMultimap can put a new key-value pair into the multimap with this method.
     * If the insertion of the key-value pair results in a change, the listeners are notified.
     * 
     * <p> No validation or null-checking is performed during the method!
     *  
     * @param key the key which identifies the collection where the new value is put 
     * @param value the value that is put into the collection of the key
     * @return true, if the insertion resulted in a change (the key-value pair was not yet in the multimap)
     */
    protected boolean internalPut(KeyType key, ValueType value) {
        boolean putResult = cache.put(key, value);
        if(putResult) {
            notifyListeners(Direction.INSERT, key, value);
        }
        return putResult;
    }
    
    /**
     * Implementations of QueryResultMultimap can remove a key-value pair from the multimap with this method.
     * If the removal of the key-value pair results in a change, the listeners are notified.
     * 
     * <p> No validation or null-checking is performed during the method!
     * 
     * @param key the key which identifies the collection where the value is removed from 
     * @param value the value that is removed from the collection of the key
     * @return true, if the removal resulted in a change (the key-value pair was in the multimap)
     */
    protected boolean internalRemove(KeyType key, ValueType value) {
        boolean removeResult = cache.remove(key, value);
        if(removeResult) {
            notifyListeners(Direction.DELETE, key, value);
        }
        return removeResult;
    }
    
    /**
     * @param setter the setter to set
     */
    public void setQueryResultSetter(IQueryResultSetter<KeyType, ValueType> setter) {
        this.setter = setter;
    }
    
    /**
     * @return the cache
     */
    protected Multimap<KeyType, ValueType> getCache() {
        return cache;
    }
    
    /**
     * @param cache the cache to set
     */
    protected void setCache(Multimap<KeyType, ValueType> cache) {
        this.cache = cache;
    }
    
    // ======================= implemented Multimap methods ====================== 
    
    /* (non-Javadoc)
     * @see com.google.common.collect.Multimap#size()
     */
    @Override
    public int size() {
        return cache.size();
    }

    /* (non-Javadoc)
     * @see com.google.common.collect.Multimap#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    /* (non-Javadoc)
     * @see com.google.common.collect.Multimap#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(Object key) {
        return cache.containsKey(key);
    }

    /* (non-Javadoc)
     * @see com.google.common.collect.Multimap#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(Object value) {
        return cache.containsValue(value);
    }

    /* (non-Javadoc)
     * @see com.google.common.collect.Multimap#containsEntry(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean containsEntry(Object key, Object value) {
        return cache.containsEntry(key, value);
    }

    /** 
     * {@inheritDoc}
     *  
     * <p> The returned collection is immutable.
     *
     */
    @Override
    public Collection<ValueType> get(KeyType key) {
        return Collections.unmodifiableCollection(cache.get(key));
    }

    /** 
     * {@inheritDoc}
     *  
     * <p> The returned set is immutable.
     *
     */
    @Override
    public Set<KeyType> keySet() {
        return Collections.unmodifiableSet(cache.keySet());
    }

    /** 
     * {@inheritDoc}
     *  
     * <p> The returned multiset is immutable.
     *
     */
    @Override
    public Multiset<KeyType> keys() {
        return Multisets.unmodifiableMultiset(cache.keys()); //cache.keys();
    }

    /** 
     * {@inheritDoc}
     *  
     * <p> The returned collection is immutable.
     *
     */
    @Override
    public Collection<ValueType> values() {
        return Collections.unmodifiableCollection(cache.values());
    }

    /** 
     * {@inheritDoc}
     *  
     * <p> The returned collection is immutable.
     *
     */
    @Override
    public Collection<Entry<KeyType, ValueType>> entries() {
        return Collections.unmodifiableCollection(cache.entries());
    }

    /** 
     * {@inheritDoc}
     *  
     * <p> The returned map is immutable.
     *
     */
    @Override
    public Map<KeyType, Collection<ValueType>> asMap() {
        return Collections.unmodifiableMap(cache.asMap());
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Throws {@link UnsupportedOperationException} if there is no {@link IQueryResultSetter}
     */
    @Override
    public boolean put(KeyType key, ValueType value) {
        if(setter == null) {
            throw new UnsupportedOperationException(NOT_ALLOW_MODIFICATIONS);
        }
        return modifyThroughQueryResultSetter(key, value, Direction.INSERT);
    }

    /**
     * This method is used for calling the query result setter to put or remove a value by modifying the model.
     * 
     * <p> The given key-value pair is first validated (see {@link IQueryResultSetter#validate(Object, Object)}, 
     * then the put or remove method is called (see {@link IQueryResultSetter#put(Object, Object)} and
     *  {@link IQueryResultSetter#remove(Object, Object)}). If the setter reported that the model has been changed,
     *  the change is checked.
     *  
     *  <p> If the model modification did not change the result set in the desired way, a warning is logged.
     *  
     *  <p> If the setter throws any {@link Throwable}, it is either rethrown in case of {@link Error} and logged
     *   otherwise.
     *  
     * 
     * @param key the key of the pair to be inserted or removed
     * @param value the value of the pair to be inserted or removed
     * @param direction specifies whether a put or a remove is performed
     * @return true, if the multimap changed according to the direction
     */
    private boolean modifyThroughQueryResultSetter(KeyType key, ValueType value, Direction direction) {
        try {
            if (setter.validate(key, value)) {
                final int size = cache.size();
                final int expectedChange = (direction == Direction.INSERT) ? 1 : -1;
                boolean changed = false;
                if (direction == Direction.INSERT) {
                    changed = setter.put(key, value);
                } else {
                    changed = setter.remove(key, value);
                }
                if (changed) {
                    return checkModificationThroughQueryResultSetter(key, value, direction, expectedChange, size);
                } else {
                    logger.warn(String
                            .format("The query result multimap %s of key %s and value %s resulted in %s. (Developer note: %s called from QueryResultMultimap)",
                                    direction == Direction.INSERT ? "insertion" : "removal", key, value, Math.abs(cache.size() - size) > 1 ? "more than one changed result" : "no changed results", setter));
                }
            }
        } catch (Error e) { // NOPMD
            throw e;
        } catch (Throwable e) { // NOPMD
            logger.warn(
                    String.format(
                            "The query result multimap encountered an error during invoking setter on %s of key %s and value %s. Error message: %s. (Developer note: %s in %s called from QueryResultMultimap)",
                            direction == Direction.INSERT ? "insertion" : "removal", key, value, e.getMessage(), e
                                    .getClass().getSimpleName(), setter), e);
        }

        return false;
    }

    /**
     * Checks whether the model modification performed by the {@link IQueryResultSetter} resulted 
     *  in the insertion or removal of exactly the required key-value pair. 
     * 
     * @param key the key for the pair that was inserted or removed
     * @param value the value for the pair that was inserted or removed
     * @param direction the direction of the change
     * @param size the size of the cache before the change
     * @return true, if the changes made by the query result setter were correct
     */
    private boolean checkModificationThroughQueryResultSetter(KeyType key, ValueType value, Direction direction,
            final int expectedChange, final int size) {
        if ((direction == Direction.INSERT) == cache.containsEntry(key, value) && (cache.size() - expectedChange) == size) {
             return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Throws {@link UnsupportedOperationException} if there is no {@link IQueryResultSetter}
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object key, Object value) {
        if(setter == null) {
            throw new UnsupportedOperationException(NOT_ALLOW_MODIFICATIONS);
        }
        // if it contains the entry, the types MUST be correct
        if(cache.containsEntry(key, value)) {
            return modifyThroughQueryResultSetter((KeyType)key, (ValueType)value, Direction.DELETE);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Throws {@link UnsupportedOperationException} if there is no {@link IQueryResultSetter}
     */
    @Override
    public boolean putAll(KeyType key, Iterable<? extends ValueType> values) {
        if(setter == null) {
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
     * <p>Throws {@link UnsupportedOperationException} if there is no {@link IQueryResultSetter}
     */
    @Override
    public boolean putAll(Multimap<? extends KeyType, ? extends ValueType> multimap) {
        if(setter == null) {
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
     * <p>Throws {@link UnsupportedOperationException} if there is no {@link IQueryResultSetter}
     *
     * <p>The returned collection is immutable.
     */
    @Override
    public Collection<ValueType> replaceValues(KeyType key, Iterable<? extends ValueType> values) {
        if(setter == null) {
            throw new UnsupportedOperationException(NOT_ALLOW_MODIFICATIONS);
        }
        Collection<ValueType> oldValues = removeAll(key);
        Iterator<? extends ValueType> iterator = values.iterator();
        int inserted = cache.get(key).size();
        while(iterator.hasNext()) {
            modifyThroughQueryResultSetter(key, iterator.next(), Direction.INSERT);
            inserted--;
        }
        if(inserted != 0) {
            logger.warn(String
                    .format("The query result multimap replaceValues on key %s did not insert all values. (Developer note: %s called from QueryResultMultimap)",
                            key, setter));
        }
        return oldValues;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Throws {@link UnsupportedOperationException} if there is no {@link IQueryResultSetter}
     *
     * <p>The returned collection is immutable.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Collection<ValueType> removeAll(Object key) {
        if(setter == null) {
            throw new UnsupportedOperationException(NOT_ALLOW_MODIFICATIONS);
        }
        // if it contains the key, the type MUST be correct
        if(cache.containsKey(key)) {
            Collection<ValueType> collection = cache.get((KeyType)key);
            Collection<ValueType> output = ImmutableSet.copyOf(collection);
            
            for (ValueType valueType : output) {
                modifyThroughQueryResultSetter((KeyType)key, valueType, Direction.DELETE);
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
     * <p>Throws {@link UnsupportedOperationException} if there is no {@link IQueryResultSetter}
     */
    @Override
    public void clear() {
        if(setter == null) {
            throw new UnsupportedOperationException(NOT_ALLOW_MODIFICATIONS);
        }
        Iterator<Entry<KeyType, ValueType>> iterator = cache.entries().iterator();
        while(iterator.hasNext()) {
            Entry<KeyType, ValueType> entry = iterator.next();
            modifyThroughQueryResultSetter(entry.getKey(), entry.getValue(), Direction.DELETE);
        }
        if (cache.size() != 0) {
            StringBuilder sb = new StringBuilder();
            for (Entry<KeyType, ValueType> entry : cache.entries()) {
                if(sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(entry.toString());
            }
            logger.warn(String
                    .format("The query result multimap is not empty after clear, remaining entries: %s. (Developer note: %s called from QueryResultMultimap)",
                            sb.toString(),setter));
        }
    }
    
}
