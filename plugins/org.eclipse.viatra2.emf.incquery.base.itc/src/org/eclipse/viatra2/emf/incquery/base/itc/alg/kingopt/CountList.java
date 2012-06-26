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

package org.eclipse.viatra2.emf.incquery.base.itc.alg.kingopt;

import java.util.Collections;
import java.util.HashMap;

public class CountList {

	//TODO DEBUG miatt public
	public HashMap<Integer, Integer> levelCountMap;
	private int starredLevel;
	
	public CountList() {
		levelCountMap = new HashMap<Integer, Integer>();
		starredLevel = Integer.MAX_VALUE;
	}
	
	/**
	 * Adds a count to the specified levelNumber.
	 * 
	 * @param levelNumber
	 */
	public void addCount(int levelNumber) {
		if (levelNumber < starredLevel)
			starredLevel = levelNumber;
		
		if (levelCountMap.containsKey(levelNumber)) {
			levelCountMap.put(levelNumber, levelCountMap.get(levelNumber)+1);
		}
		else {
			levelCountMap.put(levelNumber, 1);
		}
	}
	
	/**
	 * Returns true if the tuple needs to be deleted from the tc relation too.
	 * Returns false otherwise.
	 * 
	 * @param levelNumber - the number of the level on which a count will be removed
	 * @return
	 */
	public boolean deleteCount(int levelNumber) {
		if (levelCountMap.containsKey(levelNumber)) {
			int c = levelCountMap.get(levelNumber)-1;
			if (c == 0) {
				
				//levelNumber key must be deleted from the hashmap
				levelCountMap.remove(levelNumber);
				
				if (levelCountMap.keySet().isEmpty()) {
					//it needs to be deleted from the tc relation too, because there are no derivation for the tuple
					//it means that the starred level is Integer_MAX_VALUE again
					return true;
				}
				else {
					starredLevel = Collections.min(levelCountMap.keySet());
					return false;
				}
			}
			else {
				levelCountMap.put(levelNumber, c);
				return false;
			}
		}
		
		return false;
	}

	public int getStarredLevel() {
		return starredLevel;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Integer i : levelCountMap.keySet()) {
			sb.append("|"+i+"-"+levelCountMap.get(i));
		}
		sb.append("|");
		return sb.toString();
	}
}
