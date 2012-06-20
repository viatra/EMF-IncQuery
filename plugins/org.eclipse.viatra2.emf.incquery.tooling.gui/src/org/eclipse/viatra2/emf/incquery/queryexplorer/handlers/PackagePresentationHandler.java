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

package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternComposite;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternsViewerFlatContentProvider;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternsViewerFlatLabelProvider;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternsViewerHierarchicalContentProvider;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternsViewerHierarchicalLabelProvider;

public class PackagePresentationHandler extends AbstractHandler {

	private PatternsViewerFlatContentProvider flatCP = new PatternsViewerFlatContentProvider();
	private PatternsViewerFlatLabelProvider flatLP = new PatternsViewerFlatLabelProvider();
	private PatternsViewerHierarchicalContentProvider hierarchicalCP = new PatternsViewerHierarchicalContentProvider();
	private PatternsViewerHierarchicalLabelProvider hierarchicalLP = new PatternsViewerHierarchicalLabelProvider();
	
	public PackagePresentationHandler() {
		
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String commandId = event.getCommand().getId();
		
		CheckboxTreeViewer patternsViewer = QueryExplorer.getInstance().getPatternsViewer();
		PatternComposite patternsViewerInput = QueryExplorer.getInstance().getPatternsViewerInput();
		
		if (commandId.contains("flat")) {
			patternsViewer.setContentProvider(flatCP);
			patternsViewer.setLabelProvider(flatLP);
		}
		else {
			patternsViewer.setContentProvider(hierarchicalCP);
			patternsViewer.setLabelProvider(hierarchicalLP);
		}
		
		patternsViewerInput.updateSelection();
		
		return null;
	}
}
