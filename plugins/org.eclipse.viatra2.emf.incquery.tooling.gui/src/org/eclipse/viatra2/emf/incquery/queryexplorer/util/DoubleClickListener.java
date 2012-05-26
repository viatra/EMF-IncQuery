package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

public class DoubleClickListener implements IDoubleClickListener {

	@Override
	public void doubleClick(DoubleClickEvent event) {
		ISelection selection = event.getSelection();
		if (selection != null && selection instanceof TreeSelection) {
			// FIXME how to invoke GMF?
			IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(IHandlerService.class);
			try {
				handlerService.executeCommand(CommandConstants.SHOW_LOCATION_COMMAND_ID, null);
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotDefinedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotEnabledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotHandledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//new ShowLocationHandler().showLocation((TreeSelection) selection);
		}
	}

}
