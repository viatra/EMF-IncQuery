package org.eclipse.viatra2.emf.incquery.gui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IListAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

@SuppressWarnings("restriction")
public class EPackageListAdapter implements IListAdapter<String> {

	@Override
	public void customButtonPressed(ListDialogField<String> field, int index) {
		//if Add button is pressed
		if (index == 0) {
	    	ElementListSelectionDialog listDialog = 
	    			new ElementListSelectionDialog(
	    					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
	    					new ImportListLabelProvider()
	    			);
	    	listDialog.setTitle("Select packages to import");
	    	listDialog.setMessage("Select one or more package(s) (* = any string, ? = any char):");
	    	Object[] input = getElements(field);
	    	listDialog.setElements(input);
	    	listDialog.open();
	    	Object[] result = listDialog.getResult();
	    	if (result != null && result.length > 0) {
	    		for (Object obj : result) {
	    			field.addElement(obj.toString());
	    		}
	    	}
		}
	}
	
	private Object[] getElements(ListDialogField<String> field) {
		List<String> result = new ArrayList<String>();
		for (String key : EPackage.Registry.INSTANCE.keySet()) {
			if (!field.getElements().contains(key)) {
				result.add(key);
			}
		}
		return result.toArray();
	}

	@Override
	public void selectionChanged(ListDialogField<String> field) {}

	@Override
	public void doubleClicked(ListDialogField<String> field) {}
}
