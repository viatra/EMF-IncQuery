package org.eclipse.viatra2.emf.incquery.validation.runtime.ui.gmf.handlers;

import java.util.HashSet;
import java.util.Set;

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
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.validation.runtime.Constraint;
import org.eclipse.viatra2.emf.incquery.validation.runtime.ConstraintAdapter;
import org.eclipse.viatra2.emf.incquery.validation.runtime.ValidationUtil;

public class InitValidatorsHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Notifier notifier = null;
		
		IEditorPart activeEditor = HandlerUtil.getActiveEditor(event);
		
		ISelection selection =  HandlerUtil.getCurrentSelectionChecked(event);
		Object firstElement = ((IStructuredSelection)selection).getFirstElement();
		if (firstElement != null && firstElement instanceof GraphicalEditPart) {
			GraphicalEditPart gep = (GraphicalEditPart)firstElement;
			Object model = gep.getModel();
			if (model != null && model instanceof View) {
				View model2 = (View)model;
				EObject element = model2.getElement();
				if (element != null) {//  && element instanceof Element) {
					Resource resource = element.eResource();
					if (resource == null) 
						notifier = element; 
					else 
						notifier = resource;
				}
			}
		}
		if (notifier==null) throw new ExecutionException("Must select a node or diagram representing an EMF model or model element.");
	
		Set<ConstraintAdapter<IPatternMatch>> adapters = new HashSet<ConstraintAdapter<IPatternMatch>>();
		for (Constraint<IPatternMatch> c : ValidationUtil.getConstraints()) {
			adapters.add(new ConstraintAdapter<IPatternMatch>(c, notifier));
		}
		ValidationUtil.getAdapterMap().put(activeEditor, adapters);

		return null;
	}

}
