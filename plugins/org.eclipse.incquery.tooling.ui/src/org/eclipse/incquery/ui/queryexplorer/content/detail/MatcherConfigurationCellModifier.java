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

package org.eclipse.incquery.ui.queryexplorer.content.detail;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.incquery.ui.queryexplorer.util.DatabindingUtil;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Item;

public class MatcherConfigurationCellModifier implements ICellModifier {
	
	private TableViewer viewer;
	
	public MatcherConfigurationCellModifier(TableViewer viewer) {
		this.viewer = viewer;
	}
	
	@Override
	public boolean canModify(Object element, String property) {
		if (property.equalsIgnoreCase("filter")) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public Object getValue(Object element, String property) {
		MatcherConfiguration conf = (MatcherConfiguration) element;
		if (property.equalsIgnoreCase("filter")) {
			if (conf.getFilter() instanceof EObject) {
				EObject eObj = (EObject) conf.getFilter();
				URI uri = eObj.eClass().eResource().getURI();
				AdapterFactoryLabelProvider lp = DatabindingUtil.getAdapterFactoryLabelProvider(uri);
				if (lp != null) {
					return lp.getText(eObj);
				}
			}
			return conf.getFilter();
		}
		else if (property.equalsIgnoreCase("class")) {
			return conf.getClazz();
		}
		else if (property.equalsIgnoreCase("parameter")) {
			return conf.getParameterName();
		}
		return "";
	}

	@Override
	public void modify(Object element, String property, Object value) {
		if (element instanceof Item) {
	         element = ((Item) element).getData();
	    }
		MatcherConfiguration conf = (MatcherConfiguration) element;
		
		if (conf != null && property.equalsIgnoreCase("filter")) {
			conf.setFilter(value);
			viewer.update(conf, null);
		}
	}

}
