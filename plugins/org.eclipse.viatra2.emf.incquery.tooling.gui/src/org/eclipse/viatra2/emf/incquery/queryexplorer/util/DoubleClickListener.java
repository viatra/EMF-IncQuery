package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.viatra2.emf.incquery.queryexplorer.handlers.ShowLocationHandler;

public class DoubleClickListener implements IDoubleClickListener {

	@Override
	public void doubleClick(DoubleClickEvent event) {
		ISelection selection = event.getSelection();
		if (selection != null && selection instanceof TreeSelection) {
			// FIXME how to invoke GMF?
			new ShowLocationHandler().showLocation((TreeSelection) selection);
		}
	}

}
