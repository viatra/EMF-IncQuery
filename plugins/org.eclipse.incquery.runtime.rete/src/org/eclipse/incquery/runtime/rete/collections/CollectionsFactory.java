/*******************************************************************************
 * Copyright (c) 2010-2013, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan Rath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.rete.collections;

//import gnu.trove.map.hash.THashMap;
//import gnu.trove.set.hash.THashSet;
//import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
//import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//import javolution.util.FastMap;
//import javolution.util.FastSet;

//import org.apache.commons.collections.map.HashedMap;
//import org.apache.commons.collections.set.MapBackedSet;
//import org.eclipse.incquery.runtime.rete.collections.hppc.HPPCHashMap;
//import org.eclipse.incquery.runtime.rete.collections.hppc.HPPCHashSet;

//import com.gs.collections.impl.map.mutable.UnifiedMap;
//import com.gs.collections.impl.set.mutable.UnifiedSet;

/**
 * @author istvanrath
 * Factory class used as an accessor to Collections implementations.
 */
public class CollectionsFactory
{

    public enum CollectionsFramework {
        Java,
        HPPC,
        GS,
        FastUtil,
        Trove,
        Apache,
        Javolution
    }
    
    public static CollectionsFramework mode = CollectionsFramework.Java;
    
    public static <K,V> Map<K,V> getMap() {
        switch (mode) {
        default:
        case Java: return new HashMap<K, V>();
//        case HPPC: return new HPPCHashMap<K, V>(); // non-thread-safe
//        case GS: return new UnifiedMap<K,V>();
//        case FastUtil: return new Object2ReferenceOpenHashMap<K,V>();
//        case Trove: return new THashMap<K, V>();
//        case Apache: 
//            return new HashedMap(); // non-thread-safe
//            //return new StaticBucketMap(100000); // non-thread-safe
//        case Javolution: return new FastMap<K, V>();
        }
    }
    
    public static <E> Set<E> getSet() {
        switch (mode) {
        default:
        case Java: return new HashSet<E>();
//        case GS: return new UnifiedSet<E>();
//        case FastUtil: return new ObjectOpenHashSet<E>();
//        case Trove: return new THashSet<E>();
//        case Apache: return MapBackedSet.decorate(getMap());
//        case Javolution: return new FastSet<E>();
        //  case HPPC: return new HPPCHashSet<E>();
        }
    }
 
    public static <E> Set<E> getSet(Collection<E> initial) {
        Set<E> r = getSet();
        r.addAll(initial);
        return r;
    }
    
}