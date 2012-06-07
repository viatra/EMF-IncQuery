package org.eclipse.viatra2.emf.incquery.gui.wizards.internal;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.presentation.EcoreActionBarContributor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IListAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

@SuppressWarnings("restriction")
public class ImportsListAdapter implements IListAdapter<Resource> {

	private EditingDomain editingDomain;
	
	public ImportsListAdapter() {
		editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain();
	}
	
	@Override
	public void customButtonPressed(ListDialogField<Resource> field, int index) {
		//if Add button is pressed
		if (index == 0) {	
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			EcoreActionBarContributor.ExtendedLoadResourceAction.ExtendedLoadResourceDialog loadResourceDialog =
					new EcoreActionBarContributor.ExtendedLoadResourceAction.ExtendedLoadResourceDialog(
							shell, 
							editingDomain
					);
			
			if (loadResourceDialog.open() == Window.OK)
			{
				for (URI uri : loadResourceDialog.getURIs()) {
					boolean contains = false;
					
					for (Resource resource : field.getElements()) {
						if (uri.equals(resource.getURI())) {
							contains = true;
						}
					}
					if (!contains) {
						field.addElement(getResource(uri));
					}
				}
			}
		}
	}
	
	private Resource getResource(URI uri) {
		Resource resource = editingDomain.getResourceSet().getResource(uri, true);
		return resource;
	}

	@Override
	public void selectionChanged(ListDialogField<Resource> field) {}

	@Override
	public void doubleClicked(ListDialogField<Resource> field) {}
}
