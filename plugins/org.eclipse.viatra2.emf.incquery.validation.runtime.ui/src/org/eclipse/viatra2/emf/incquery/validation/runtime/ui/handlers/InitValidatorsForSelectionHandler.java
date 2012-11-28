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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.validation.runtime.ui.ValidationInitUtil;

public class InitValidatorsForSelectionHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
        IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
        Object selectedElement = selection.getFirstElement();

        if (selectedElement instanceof Notifier) {
            try {
                ValidationInitUtil.initializeAdapters(editorPart, (Notifier) selectedElement);
            } catch (IncQueryException ex) {
                throw new ExecutionException("Could not validate constraints due to a pattern matcher error", ex);
            }
        }
        return null;
    }

}