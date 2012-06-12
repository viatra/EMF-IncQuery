/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.gui.wizards.internal;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
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
public class ImportsListAdapter implements IListAdapter<EPackage> {

	private EditingDomain editingDomain;
	
	public ImportsListAdapter() {
		editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain();
	}
	
	@Override
	public void customButtonPressed(ListDialogField<EPackage> field, int index) {
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
					
					for (EPackage _package : field.getElements()) {
						if (uri.equals(_package.getNsURI())) {
							contains = true;
						}
					}
					if (!contains) {
						EPackage _package = getEPackage(uri);
						if (_package != null) {
							field.addElement(_package);
						}
					}
				}
			}
		}
	}
	
	private EPackage getEPackage(URI uri) {
		Resource resource = editingDomain.getResourceSet().getResource(uri, true);
		return resource.getContents().get(0).eClass().getEPackage();
	}

	@Override
	public void selectionChanged(ListDialogField<EPackage> field) {}

	@Override
	public void doubleClicked(ListDialogField<EPackage> field) {}
}
