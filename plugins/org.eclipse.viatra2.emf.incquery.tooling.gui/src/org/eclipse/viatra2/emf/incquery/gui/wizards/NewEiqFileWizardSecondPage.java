package org.eclipse.viatra2.emf.incquery.gui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.viatra2.emf.incquery.gui.wizards.internal.ImportListLabelProvider;
import org.eclipse.viatra2.emf.incquery.gui.wizards.internal.ImportsListAdapter;
import org.eclipse.viatra2.emf.incquery.gui.wizards.internal.ObjectParameter;
import org.eclipse.viatra2.emf.incquery.gui.wizards.internal.ObjectsListAdapter;
import org.eclipse.viatra2.emf.incquery.gui.wizards.internal.ObjectsListLabelProvider;

@SuppressWarnings("restriction")
public class NewEiqFileWizardSecondPage extends NewTypeWizardPage {

	private static final String PATTERN_NAME_SHOULD_BE_SPECIFIED = "Pattern name should be specified!";
	private static final String PATTERN_NAME_MUST_BE_SPECIFIED = "Pattern name must be specified, if at least parameter is set!";
	private Text patternText;
	private ListDialogField<Resource> importList;
	private ListDialogField<ObjectParameter> objectsList;
	private ImportListLabelProvider importListLabelProvider;
	private ObjectsListLabelProvider objectsListLabelProvider;
	private ImportsListAdapter importsListAdapter;
	private ObjectsListAdapter objectsListAdapter;
	public boolean parameterSet;
	
	public NewEiqFileWizardSecondPage() {
		super(false, "eiq");
		setTitle("EMF-IncQuery query definition Wizard");
		parameterSet = false;
	}
	
	private void createImportsControl(Composite parent, int nColumns) {
		String[] buttonLiterals= new String[] {"Add", "Remove"};
		importsListAdapter = new ImportsListAdapter();
		importListLabelProvider = new ImportListLabelProvider();
		
		importList = new ListDialogField<Resource>(importsListAdapter, buttonLiterals, importListLabelProvider);
		importList.setLabelText("&Imported packages:");
		importList.setTableColumns(new ListDialogField.ColumnsDescription(new String[] {"EPackage"}, true));
		importList.setRemoveButtonIndex(1);
		importList.doFillIntoGrid(parent, nColumns);
	}
	
	private void createObjectSelectionControl(Composite parent, int nColumns) {
		String[] buttonLiterals= new String[] {"Add", "Modify", "Remove"};
		objectsListAdapter = new ObjectsListAdapter(this, importList);
		objectsListLabelProvider = new ObjectsListLabelProvider();
		
		objectsList = new ListDialogField<ObjectParameter>(objectsListAdapter, buttonLiterals, objectsListLabelProvider);
		objectsList.setLabelText("&Pattern parameters:");
		objectsList.setTableColumns(new ListDialogField.ColumnsDescription(new String[] {"Name", "Type"}, true));
		//disable modify button for an empty list
		objectsList.enableButton(1, false);
		objectsList.setRemoveButtonIndex(2);
		objectsList.doFillIntoGrid(parent, nColumns);
	}
	
	public void init(IStructuredSelection selection) {
		IJavaElement jelem= getInitialJavaElement(selection);
		initContainerPage(jelem);
		
		if (jelem != null) {
			IPackageFragment pack = (IPackageFragment) jelem.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
			setPackageFragment(pack, true);
		}
		
		packageChanged();
	}
	
	@Override
	public void createControl(Composite parent) {
		int nColumns= 5;
		
		initializeDialogUnits(parent);
		Composite composite= new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		GridLayout layout= new GridLayout();
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		
		Label label = new Label(composite, SWT.NULL);
		label.setText("&Pattern name:");
		patternText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		patternText.setText("");
		GridData gd_1 = new GridData(GridData.FILL_HORIZONTAL);
		gd_1.horizontalSpan = 3;
		patternText.setLayoutData(gd_1);
		patternText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
		
		label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gd_2 = new GridData(GridData.FILL_HORIZONTAL);
		gd_2.horizontalSpan = nColumns;
		label.setLayoutData(gd_2);
		
		createImportsControl(composite, nColumns);
		createObjectSelectionControl(composite, nColumns);
		
		setControl(composite);
		validatePage();
	}
	
	@Override
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		validatePage();
	}
	
	public void validatePage() {
		
		StatusInfo si = new StatusInfo(StatusInfo.OK, "");

		if (patternText != null) {
			String patternName = patternText.getText();
			if (patternName == null || patternName.length() == 0) {
				if (parameterSet) {
					si.setError(PATTERN_NAME_MUST_BE_SPECIFIED);
				}
				else {
					si.setWarning(PATTERN_NAME_SHOULD_BE_SPECIFIED);
				}
				
			}
		}
		
		if (si.getSeverity() == IStatus.OK) {
			si.setInfo("");
		}

		updateStatus(si);
		
		if (si.isError()) {
			setErrorMessage(si.getMessage());
		}
	}

	public String getPatternName() {
		return patternText.getText();
	}
}
