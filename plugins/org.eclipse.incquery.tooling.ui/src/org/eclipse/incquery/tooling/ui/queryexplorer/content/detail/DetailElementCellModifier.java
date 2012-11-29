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

package org.eclipse.incquery.tooling.ui.queryexplorer.content.detail;

import org.eclipse.jface.viewers.ICellModifier;

/**
 * This is a basic implementation of the ICellModifier interface used for pattern matches. 
 * Note that this class is necessary because of the 'two-sided' table viewer used in the Details/Filters view. 
 * 
 * @author Tamas Szabo
 *
 */
public class DetailElementCellModifier implements ICellModifier {

	@Override
	public boolean canModify(Object element, String property) {
		return false;
	}

	@Override
	public Object getValue(Object element, String property) {
		return null;
	}

	@Override
	public void modify(Object element, String property, Object value) {
		
	}
}
