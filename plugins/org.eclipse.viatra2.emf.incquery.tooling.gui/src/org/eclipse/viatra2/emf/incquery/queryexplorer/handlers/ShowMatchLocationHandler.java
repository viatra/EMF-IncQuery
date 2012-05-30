package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;

public class ShowMatchLocationHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TreeSelection) {
			showLocation((TreeSelection) selection);
		}
		return null;
	}
	
	public void showLocation(TreeSelection selection) {
		Object obj = selection.getFirstElement();
		
		if (obj != null && obj instanceof ObservablePatternMatch) {
			ObservablePatternMatch pm = (ObservablePatternMatch) obj;
			
			IEditorPart editorPart = pm.getParent().getParent().getEditorPart();
			/*if(editorPart.getSite().getPage().getActiveEditor() != editorPart) {
				//bring editor part to top
				IHandlerService handlerService = (IHandlerService) editorPart.getSite().getService(IHandlerService.class);
				try {
					handlerService.executeCommand(CommandConstants.SHOW_LOCATION_COMMAND_ID, null);
					return;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					IncQueryEngine.getDefaultLogger().logError("Exception when activating show location!", e);
				} catch (NotDefinedException e) {
					IncQueryEngine.getDefaultLogger().logError("Exception when activating show location!", e);
				} catch (NotEnabledException e) {
					IncQueryEngine.getDefaultLogger().logError("Exception when activating show location!", e);
				} catch (NotHandledException e) {
					IncQueryEngine.getDefaultLogger().logError("Exception when activating show location!", e);
				}
			}*/
			
			Object[] locationObjects = pm.getLocationObjects();
			IStructuredSelection preparedSelection = prepareSelection(editorPart, locationObjects);
			navigateToElements(editorPart, preparedSelection);

			editorPart.getSite().getPage().bringToTop(editorPart);
			
			reflectiveSetSelection(editorPart, preparedSelection); 
		}
	}

	private void reflectiveSetSelection(IEditorPart editorPart, IStructuredSelection preparedSelection) {
		//Reflection API is used here!!!
		try {
			Method m = editorPart.getClass().getMethod("setSelectionToViewer", Collection.class);
			m.invoke(editorPart, preparedSelection.toList());
		}
		catch (NoSuchMethodException e) {
			IncQueryEngine.getDefaultLogger().logDebug("setSelectionToViewer method not found");
		}
		catch (Exception e) {
			IncQueryEngine.getDefaultLogger().logDebug("setSelectionToViewer call failed");
		}
	}

	/**
	 * @param editorPart
	 * @param locationObjects
	 * @return
	 */
	private TreeSelection prepareSelection(IEditorPart editorPart, Object[] locationObjects) {
		List<TreePath> paths = new ArrayList<TreePath>(); //[locationObjects.length]
		for (Object o: locationObjects) {
			TreePath path = createTreePath(editorPart, (EObject) o);
			if(path != null) {
				paths.add(path);
			}
		}

		TreeSelection treeSelection = new TreeSelection((TreePath[]) paths.toArray(new TreePath[0]));
		return treeSelection;
	}
	
	protected void navigateToElements(IEditorPart editorPart, IStructuredSelection selection) {
		ISelectionProvider selectionProvider = editorPart.getEditorSite().getSelectionProvider();
		selectionProvider.setSelection(selection);
	}
	
	protected TreePath createTreePath(IEditorPart editor, EObject obj) {
		List<Object> nodes = new ArrayList<Object>();
		nodes.add(obj);
		EObject tmp = ((EObject) obj).eContainer();
			
		while (tmp != null) {
			nodes.add(0, tmp);
			tmp = tmp.eContainer();
		}
		
		return new TreePath(nodes.toArray());
	}
	
}
