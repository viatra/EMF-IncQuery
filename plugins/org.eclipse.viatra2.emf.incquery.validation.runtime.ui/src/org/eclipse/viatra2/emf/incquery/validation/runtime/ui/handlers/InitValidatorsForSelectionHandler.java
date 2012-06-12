/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.validation.runtime.ui.handlers;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.validation.runtime.Constraint;
import org.eclipse.viatra2.emf.incquery.validation.runtime.ConstraintAdapter;
import org.eclipse.viatra2.emf.incquery.validation.runtime.ValidationUtil;



public class InitValidatorsForSelectionHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart activeEditor = HandlerUtil.getActiveEditor(event);
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		Object selectedElement = selection.getFirstElement();
		
		if (selectedElement instanceof Notifier) {
			initializeAdapters(activeEditor, (Notifier) selectedElement);
		}
	
		return null;
	}

	/**
	 * @param activeEditor
	 * @param root
	 */
	protected void initializeAdapters(IEditorPart activeEditor, Notifier root) {
		Set<ConstraintAdapter<IPatternMatch>> adapters = new HashSet<ConstraintAdapter<IPatternMatch>>();
		
		Map<IEditorPart, Set<ConstraintAdapter<IPatternMatch>>> adapterMap = ValidationUtil.getAdapterMap();
		if(adapterMap.containsKey(activeEditor)) {
			// FIXME define proper semantics for validation based on selection
			// FIXME handle already existing violations
			
			//adapterMap.get(activeEditor).addAll(adapters);
		} else {
			for (Constraint<IPatternMatch> c : ValidationUtil.getConstraints()) {
				adapters.add(new ConstraintAdapter<IPatternMatch>(c, root));
			}
			adapterMap.put(activeEditor, adapters);
			activeEditor.getEditorSite().getPage().addPartListener(ValidationUtil.editorPartListener);
		}
	}
}
