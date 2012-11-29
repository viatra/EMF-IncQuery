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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.incquery.tooling.ui.queryexplorer.util.DatabindingUtil;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class ModelElementListDialogLabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
		
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof EObject) {
			EObject eObj = (EObject) element;
			URI uri = eObj.eClass().eResource().getURI();
			AdapterFactoryLabelProvider al = DatabindingUtil.getAdapterFactoryLabelProvider(uri);
			if (al != null) {
				return al.getImage(element);
			}
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof EObject) {
			EObject eObj = (EObject) element;
			URI uri = eObj.eClass().eResource().getURI();
			AdapterFactoryLabelProvider al = DatabindingUtil.getAdapterFactoryLabelProvider(uri);
			if (al != null) {
				return al.getText(element);
			}
		}
		return element.toString();
	}

}
