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

package org.eclipse.viatra2.emf.incquery.gui.wizards.internal;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;

public class ImportListLabelProvider implements ILabelProvider {

	private ImageRegistry imageRegistry;
	
	public ImportListLabelProvider() {
		imageRegistry = IncQueryGUIPlugin.getDefault().getImageRegistry();
	}
	
	@Override
	public void addListener(ILabelProviderListener listener) {}

	@Override
	public void dispose() {}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {}

	@Override
	public Image getImage(Object element) {
		return imageRegistry.get(IncQueryGUIPlugin.ICON_EPACKAGE);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof EPackage) {
			return ((EPackage) element).getNsURI();
		}
		return null;
	}

}
