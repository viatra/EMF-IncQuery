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

package org.eclipse.incquery.ui.wizards.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.incquery.tooling.core.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.incquery.ui.IncQueryGUIPlugin;
import org.eclipse.incquery.ui.wizards.NewEiqFileWizardContainerConfigurationPage;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IListAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.ui.PlatformUI;

/**
 * An {@link IListAdapter} implementation for importing {@link EPackage}s.
 * 
 * @author Tamas Szabo
 *
 */
@SuppressWarnings("restriction")
public class ImportListAdapter implements IListAdapter<EPackage> {
	
	private NewEiqFileWizardContainerConfigurationPage firstPage;
	private IEiqGenmodelProvider metamodelProviderService;
	private ILog logger = IncQueryGUIPlugin.getDefault().getLog(); 
	
	public ImportListAdapter(
			NewEiqFileWizardContainerConfigurationPage firstPage,
			IEiqGenmodelProvider metamodelProviderService) {
		this.firstPage = firstPage;
		this.metamodelProviderService = metamodelProviderService;
	}

	@Override
	public void customButtonPressed(ListDialogField<EPackage> field, int index) {
		//if Add button is pressed
		if (index == 0) {	
			ElementSelectionDialog listDialog = 
					new ElementSelectionDialog(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
							new ImportListLabelProvider(), 
							"EPackage"
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
		
		try {
			Collection<EPackage> packages = metamodelProviderService.getAllMetamodelObjects(firstPage.getProject());
			for (EPackage ePackage : packages) {
				if (!fieldContains(field, ePackage)) {
					result.add(ePackage);
				}
			}
		} 
		catch (CoreException e) {
			logger.log(new Status(IStatus.ERROR,
					IncQueryGUIPlugin.PLUGIN_ID,
					"Error during EPackage collecting: " + e.getCause().getMessage(), e.getCause()));
		}
		
		return result.toArray();
	}
	
	private boolean fieldContains(ListDialogField<EPackage> field, EPackage _package) {
		for (EPackage _p : field.getElements()) {
			if (_p.getNsURI().matches(_package.getNsURI())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void selectionChanged(ListDialogField<EPackage> field) {}

	@Override
	public void doubleClicked(ListDialogField<EPackage> field) {}
}
