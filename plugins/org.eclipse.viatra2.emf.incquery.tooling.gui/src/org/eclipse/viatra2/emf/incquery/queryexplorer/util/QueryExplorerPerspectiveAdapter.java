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

package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;

public class QueryExplorerPerspectiveAdapter extends PerspectiveAdapter {
	
	@Override
	public void perspectiveChanged(IWorkbenchPage page,	IPerspectiveDescriptor perspective, String changeId) {
		
		if (changeId.matches("viewShow")) {
			for (IViewReference ref : page.getViewReferences()) {
				if (ref.getId().matches(QueryExplorer.ID)) {
//					CheckboxTreeViewer tableViewer = QueryExplorer.getInstance().getPatternsViewer();
//					int size = tableViewer.getTree().getItems().length;
					
//					for (int i = 0;i<size;i++) {
//						String element = tableViewer.getElementAt(i).toString();
//						if (PatternRegistry.getInstance().isActive(element)) {
//							tableViewer.setChecked(element, true);
//						}
//						else {
//							tableViewer.setChecked(element, false);
//						}
//					}
				}
			}
		}
		
		super.perspectiveChanged(page, perspective, changeId);
	}
}
