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

package org.eclipse.incquery.tooling.ui.wizards.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * A {@link Dialog} implementation for configuring one parameter of a pattern.
 * 
 * @author Tamas Szabo
 *
 */
public class ObjectParameterConfigurationDialog extends Dialog {

	private static final String SELECT_A_MODEL_ELEMENT = "Select a model element (* = any string, ? = any char):";
	private static final String PARAMETER_TYPE_SELECTION = "Parameter type selection";
	private static final String PARAMETER_TYPE = "&Parameter type:";
	private static final String PARAMETER_NAME = "&Parameter name:";
	private static final String TITLE = "&Pattern parameter configuration";
	private Text parameterName;
	private Text parameterType;
	private List<EPackage> currentPackages;
	private ObjectParameter result;
	
	protected ObjectParameterConfigurationDialog(Shell shell, List<EPackage> currentPackages, ObjectParameter result) {
		super(shell);
		shell.setText(TITLE);
		this.currentPackages = currentPackages;
		this.result = result;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		int nColumns = 4;
		
		Composite composite= new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		GridLayout layout= new GridLayout();
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		
		Label label = new Label(composite, SWT.NULL);
		label.setText(PARAMETER_NAME);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 1;
		label.setLayoutData(gridData);
		
		parameterName = new Text(composite, SWT.BORDER | SWT.SINGLE);
		parameterName.setText(result.getParameterName());
		parameterName.setEditable(true);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		parameterName.setLayoutData(gridData);
		parameterName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				result.setParameterName(parameterName.getText());
				super.keyReleased(e);
			}
		});
		
		label = new Label(composite, SWT.NULL);
		label.setText(PARAMETER_TYPE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 1;
		label.setLayoutData(gridData);
		
		parameterType = new Text(composite, SWT.BORDER | SWT.SINGLE);
		parameterType.setText(result.getObject() == null ? "" : ((EClassifier) result.getObject()).getName());
		parameterType.setEditable(false);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 1;
		parameterType.setLayoutData(gridData);
		
		Button button = new Button(composite, SWT.PUSH);
		button.setText("Browse...");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 1;
		button.setLayoutData(gridData);

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setParameterType(openDialogBox());
			}
		});
		
		return super.createDialogArea(parent);
	}
	
	/**
	 * Opens an element selection dialog for choosing the type of the parameter as an {@link EClassifier}.
	 * 
	 * @return the type of the parameter
	 */
	private EClassifier openDialogBox() {
    	ElementListSelectionDialog listDialog = 
    			new ElementListSelectionDialog(
    					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
    					new ObjectParameterConfigurationLabelProvider()
    			);
    	listDialog.setTitle(PARAMETER_TYPE_SELECTION);
    	listDialog.setMessage(SELECT_A_MODEL_ELEMENT);
    	listDialog.setElements(getElements());
    	listDialog.open();
    	Object[] result = listDialog.getResult();
    	if (result != null && result.length > 0) {
    		return (EClassifier) result[0];
    	}
        return null;
    }
	
	/**
	 * Returns the array of {@link EClassifier} instances based on the imported {@link EPackage}s.
	 * 
	 * @return the array of {@link EClassifier}s
	 */
	private Object[] getElements() {
		List<EObject> result = new ArrayList<EObject>();
		for (EPackage _package : currentPackages) {
			TreeIterator<EObject> iterator = _package.eAllContents();
			
			while (iterator.hasNext()) {
				EObject nextObject = iterator.next();
				if (nextObject instanceof EClassifier) {
					result.add(nextObject);
				}
			}
		}
		return result.toArray();
	}
	
	private void setParameterType(EClassifier object) {
		this.result.setObject(object);
		if (object != null) {
			parameterType.setText(((EClassifier) object).getName());
		}
	}
}
