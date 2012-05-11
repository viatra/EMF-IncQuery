package org.eclipse.viatra2.emf.incquery.queryexplorer.content.detail;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;

public class ModelElementCellEditor extends DialogCellEditor {

	private Notifier root;
	
    public ModelElementCellEditor(Composite parent, Notifier root) {
        super(parent);
        this.root = root;
    }

    protected Object openDialogBox(Control cellEditorWindow) {
    	ListDialog listDialog = new ListDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
    	listDialog.setTitle("Parameter selection dialog");
    	listDialog.setAddCancelButton(true);
    	listDialog.setContentProvider(new ModelElementListDialogContentProvider(root));
    	listDialog.setLabelProvider(new ModelElementListDialogLabelProvider());
    	listDialog.setInput(root);
    	listDialog.open();
    	Object[] result = listDialog.getResult();
    	if (result != null && result.length > 0) {
    		return result[0];
    	}
        return null;
    }

}
