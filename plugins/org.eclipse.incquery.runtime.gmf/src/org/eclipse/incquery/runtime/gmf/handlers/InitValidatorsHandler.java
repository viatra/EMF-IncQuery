/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.gmf.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.validation.runtime.ValidationPartListener;
import org.eclipse.viatra2.emf.incquery.validation.runtime.ValidationUtil;

public class InitValidatorsHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Notifier notifier = null;

        IEditorPart activeEditor = HandlerUtil.getActiveEditor(event);

        ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
        Object firstElement = ((IStructuredSelection) selection).getFirstElement();
        if (firstElement != null && firstElement instanceof GraphicalEditPart) {
            GraphicalEditPart gep = (GraphicalEditPart) firstElement;
            Object model = gep.getModel();
            if (model != null && model instanceof View) {
                View model2 = (View) model;
                EObject element = model2.getElement();
                if (element != null) {// && element instanceof Element) {
                    Resource resource = element.eResource();
                    if (resource == null)
                        notifier = element;
                    else
                        notifier = resource;
                }
            }
        }
        if (notifier == null)
            throw new ExecutionException("Must select a node or diagram representing an EMF model or model element.");
        // FIXME should go through ValidationInitUtil!
        ValidationUtil.addNotifier(activeEditor, notifier);
        activeEditor.getEditorSite().getPage().addPartListener(ValidationPartListener.getInstance());
        
        return null;
    }

}
