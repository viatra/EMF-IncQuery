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

package org.eclipse.incquery.tooling.ui.wizards.internal;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * {@link ITableLabelProvider} implementation for the {@link ObjectListAdapter}.
 * 
 * @author Tamas Szabo
 * 
 */
public class ObjectListLabelProvider extends LabelProvider implements ITableLabelProvider {

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof ObjectParameter) {
            ObjectParameter parameter = (ObjectParameter) element;
            if (columnIndex == 0) {
                return parameter.getParameterName();
            } else {
                return parameter.getObject() != null ? parameter.getObject().getName() : "";
            }
        }
        return null;
    }

}
