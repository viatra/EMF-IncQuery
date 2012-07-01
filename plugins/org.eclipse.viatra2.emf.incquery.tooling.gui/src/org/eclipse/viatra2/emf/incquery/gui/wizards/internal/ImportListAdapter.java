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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IListAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * An {@link IListAdapter} implementation for importing {@link EPackage}s.
 * 
 * @author Tamas Szabo
 *
 */
@SuppressWarnings("restriction")
public class ImportListAdapter implements IListAdapter<EPackage> {
		
	@Override
	public void customButtonPressed(ListDialogField<EPackage> field, int index) {
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
					field.addElement((EPackage) obj);
				}
			}
		}
	}
	
	/**
	 * Returns the available {@link EPackage}s.
	 * 
	 * @param field the {@link ListDialogField} instance to avoid duplicate importing
	 * @return the array of {@link EPackage}s
	 */
	private Object[] getElements(ListDialogField<EPackage> field) {
		List<EPackage> result = new ArrayList<EPackage>();
		Set<String> keys = new HashSet<String>(EPackage.Registry.INSTANCE.keySet());
		for (String key : keys) {
			EPackage _package = EPackage.Registry.INSTANCE.getEPackage(key);
			if (!field.getElements().contains(_package)) {
				result.add(_package);
			}
		}
		return result.toArray();
	}

	@Override
	public void selectionChanged(ListDialogField<EPackage> field) {}

	@Override
	public void doubleClicked(ListDialogField<EPackage> field) {}
}
