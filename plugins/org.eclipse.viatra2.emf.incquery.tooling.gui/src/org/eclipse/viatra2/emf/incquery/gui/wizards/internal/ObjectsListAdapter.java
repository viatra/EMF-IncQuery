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

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IListAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.viatra2.emf.incquery.gui.wizards.NewEiqFileWizardPatternConfigurationPage;

@SuppressWarnings("restriction")
public class ObjectsListAdapter implements IListAdapter<ObjectParameter> {

	private ListDialogField<EPackage> importList;
	private NewEiqFileWizardPatternConfigurationPage page;
	
	public ObjectsListAdapter(NewEiqFileWizardPatternConfigurationPage page, ListDialogField<EPackage> importList) {
		this.importList = importList;
		this.page = page;
	}
	
	@Override
	public void customButtonPressed(ListDialogField<ObjectParameter> field, int index) {
		ObjectParameter parameter = new ObjectParameter();
		ObjectParameterConfigurationDialog dialog = new ObjectParameterConfigurationDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				importList.getElements(), parameter);
		//a unique parameter object is needed because the dialog will be disposed after the ok button is pressed
		if (index == 0) {
			//Add
			if (dialog.open() == Dialog.OK) {
				field.addElement(parameter);
			}
		}
		else if (index == 1) {
			//Modify
			ObjectParameter firstElement = field.getSelectedElements().get(0);
			parameter.setObject(firstElement.getObject());
			parameter.setParameterName(firstElement.getParameterName());
			if (dialog.open() == Dialog.OK) {
				firstElement.setObject(parameter.getObject());
				firstElement.setParameterName(parameter.getParameterName());
			}
		}
	}

	@Override
	public void selectionChanged(ListDialogField<ObjectParameter> field) {
		if (field.getElements().size() > 0) {
			field.enableButton(1, true);
			page.parameterSet = true;
		}
		else {
			field.enableButton(1, false);
			page.parameterSet = false;
		}
		
		page.validatePage();
	}

	@Override
	public void doubleClicked(ListDialogField<ObjectParameter> field) {
		
	}

}
