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

package org.eclipse.viatra2.emf.incquery.triggerengine.validation.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.triggerengine.validation.ValidationPartListener;
import org.eclipse.viatra2.emf.incquery.triggerengine.validation.ValidationUtil;

public class InitValidatorsHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart activeEditor = HandlerUtil.getActiveEditor(event);
		
		if(activeEditor instanceof IEditingDomainProvider) {
			IEditingDomainProvider provider = (IEditingDomainProvider) activeEditor;
			ResourceSet resourceSet = provider.getEditingDomain().getResourceSet();
			if (resourceSet != null) {
				ValidationUtil.addNotifier(activeEditor, resourceSet);
				activeEditor.getSite().getPage().addPartListener(ValidationPartListener.getInstance());
			}
		}
		
		return null;
	}

}
