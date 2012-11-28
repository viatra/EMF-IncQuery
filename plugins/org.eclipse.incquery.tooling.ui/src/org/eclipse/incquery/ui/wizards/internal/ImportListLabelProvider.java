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

package org.eclipse.incquery.ui.wizards.internal;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.incquery.ui.IncQueryGUIPlugin;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

/**
 * {@link ILabelProvider} implementation for the {@link ImportListAdapter}.
 * 
 * @author Tamas Szabo
 *
 */
public class ImportListLabelProvider extends StyledCellLabelProvider implements
		ILabelProvider {

	private ImageRegistry imageRegistry;
	
	public ImportListLabelProvider() {
		imageRegistry = IncQueryGUIPlugin.getDefault().getImageRegistry();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof EPackage) {
			return imageRegistry.get(IncQueryGUIPlugin.ICON_EPACKAGE);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof EPackage) {
			return ((EPackage) element).getNsURI();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.StyledCellLabelProvider#update(org.eclipse.
	 * jface.viewers.ViewerCell)
	 */
	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		StyledString text = new StyledString();
		if (element instanceof EPackage) {
			EPackage ePackage = (EPackage) element;
			text.append(ePackage.getNsURI());
			if (ePackage.eResource().getURI().isPlatform()) {
				text.append(
						String.format(" (%s)", ePackage.eResource().getURI()),
						StyledString.QUALIFIER_STYLER);
			}
			cell.setImage(imageRegistry.get(IncQueryGUIPlugin.ICON_EPACKAGE));
		}
		cell.setText(text.getString());
		cell.setStyleRanges(text.getStyleRanges());
		super.update(cell);
	}


}
