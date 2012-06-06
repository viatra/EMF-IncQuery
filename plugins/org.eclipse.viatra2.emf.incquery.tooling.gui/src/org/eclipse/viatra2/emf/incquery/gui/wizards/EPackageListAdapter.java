package org.eclipse.viatra2.emf.incquery.gui.wizards;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.presentation.EcoreActionBarContributor;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IListAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

@SuppressWarnings("restriction")
public class EPackageListAdapter implements IListAdapter<String> {

	@Override
	public void customButtonPressed(ListDialogField<String> field, int index) {
		//if Add button is pressed
		if (index == 0) {
//	    	ElementListSelectionDialog listDialog = 
//	    			new ElementListSelectionDialog(
//	    					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
//	    					new ImportListLabelProvider()
//	    			);
//	    	listDialog.setTitle("Select packages to import");
//	    	listDialog.setMessage("Select one or more package(s) (* = any string, ? = any char):");
//	    	Object[] input = getElements(field);
//	    	listDialog.setElements(input);
//	    	listDialog.open();
//	    	Object[] result = listDialog.getResult();
//	    	if (result != null && result.length > 0) {
//	    		for (Object obj : result) {
//	    			field.addElement(obj.toString());
//	    		}
//	    	}
		
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			EcoreActionBarContributor.ExtendedLoadResourceAction.ExtendedLoadResourceDialog loadResourceDialog =
					new EcoreActionBarContributor.ExtendedLoadResourceAction.ExtendedLoadResourceDialog(
							shell, 
							TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain()
					);
			
			if (loadResourceDialog.open() == Window.OK)
			{
				for (URI uri : loadResourceDialog.getURIs()) {
					if (!field.getElements().contains(uri.toString())) {
						field.addElement(uri.toString());
					}
				}
			}
		}
	}
	
//	private Object[] getElements(ListDialogField<String> field) {
//		List<String> result = new ArrayList<String>();
//		for (String key : EPackage.Registry.INSTANCE.keySet()) {
//			if (!field.getElements().contains(key)) {
//				result.add(key);
//			}
//		}
//		return result.toArray();
//	}

	@Override
	public void selectionChanged(ListDialogField<String> field) {}

	@Override
	public void doubleClicked(ListDialogField<String> field) {}
}
