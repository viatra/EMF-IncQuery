package org.eclipse.viatra2.emf.incquery.validation.runtime.ui.handlers;

import java.util.HashSet;
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

public class InitValidatorsHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart activeEditor = HandlerUtil.getActiveEditor(event);
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		Object selectedElement = selection.getFirstElement();
		
		if (selectedElement instanceof Notifier) {
			Notifier notifier = (Notifier) selectedElement;
			Set<ConstraintAdapter<IPatternMatch>> adapters = new HashSet<ConstraintAdapter<IPatternMatch>>();
			for (Constraint<IPatternMatch> c : ValidationUtil.getConstraints()) {
				adapters.add(new ConstraintAdapter<IPatternMatch>(c, notifier));
			}
			ValidationUtil.getAdapterMap().put(activeEditor, adapters);
		}

		return null;
	}
}
