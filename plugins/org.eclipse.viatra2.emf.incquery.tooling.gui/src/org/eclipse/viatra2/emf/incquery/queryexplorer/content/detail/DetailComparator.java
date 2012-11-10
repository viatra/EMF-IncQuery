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
package org.eclipse.viatra2.emf.incquery.queryexplorer.content.detail;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Comparator for ordering the parameters first and other observables next
 * 
 * @author Zoltan Ujhelyi
 */
public class DetailComparator implements Comparator<String> {

    private Map<String, Integer> parameterMap;

    public DetailComparator(String[] parameterNames) {
        parameterMap = new HashMap<String, Integer>();
        for (int i = 0; i < parameterNames.length; i++) {
            parameterMap.put(parameterNames[i], Integer.valueOf(i));
        }
    }

    @Override
    public int compare(String key1, String key2) {
        boolean isParameter1 = parameterMap.containsKey(key1);
        boolean isParameter2 = parameterMap.containsKey(key2);
        if (isParameter1 && !isParameter2) {
            return -1;
        } else if (!isParameter1 && isParameter2) {
            return 1;
        } else if (isParameter1 && isParameter2) {
            parameterMap.get(key1).compareTo(parameterMap.get(key2));
        }
        return key1.compareTo(key2);

    }
}
