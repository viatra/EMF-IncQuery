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
package org.eclipse.viatra2.emf.incquery.base.itc.alg.incscc;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Tamas Szabo
 *
 */
public class CollectionHelper {

	/**
	 * Returns the intersection of two sets.
	 * 
	 * @param set1 the first set
	 * @param set2 the second set
	 * @return the intersection of the sets
	 */
	public static <V> Set<V> intersection(Set<V> set1, Set<V> set2) {
		Set<V> intersection = new HashSet<V>();
		for (V n : set1) {
			if (set2.contains(n)) {
				intersection.add(n);
			}
		}
		return intersection;
	}
	
	/**
	 * Returns the difference of two sets (S1\S2).
	 * 
	 * @param set1 the first set
	 * @param set2 the second set
	 * @return the difference of the sets
	 */
	public static <V> Set<V> difference(Set<V> set1, Set<V> set2) {
		Set<V> difference = new HashSet<V>();
		for (V n : set1) {
			if (!set2.contains(n)) {
				difference.add(n);
			}
		}
		return difference;
	}
	
}
