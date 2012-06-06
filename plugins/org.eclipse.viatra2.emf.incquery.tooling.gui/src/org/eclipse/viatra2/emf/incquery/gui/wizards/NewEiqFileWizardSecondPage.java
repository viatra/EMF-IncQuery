package org.eclipse.viatra2.emf.incquery.gui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

@SuppressWarnings("restriction")
public class NewEiqFileWizardSecondPage extends NewTypeWizardPage {

	private static final String PATTERN_NAME_MUST_BE_SPECIFIED = "Pattern name must be specified!";
	private Text patternText;
	private ListDialogField<String> importList;
	private ImportListLabelProvider labelProvider;
	private EPackageListAdapter adapter;
	private static final String EPACKAGE= "EPackage"; 
	
	public NewEiqFileWizardSecondPage() {
		super(false, "eiq");
		setTitle("EMF-IncQuery query definition Wizard");
	}
	
	private void createImportsControl(Composite parent, int nColumns) {
		String[] buttonLiterals= new String[] {"Add", "Remove"};
		adapter = new EPackageListAdapter();
		labelProvider = new ImportListLabelProvider();
		
		importList = new ListDialogField<String>(adapter, buttonLiterals, labelProvider);
		importList.setLabelText("&Imported packages:");
		importList.setTableColumns(new ListDialogField.ColumnsDescription(1, false));
		importList.setRemoveButtonIndex(1);
		importList.doFillIntoGrid(parent, nColumns);
		final TableViewer tableViewer= importList.getTableViewer();
		tableViewer.setColumnProperties(new String[] {EPACKAGE});

		GridData gd= (GridData) importList.getListControl(null).getLayoutData();
		gd.heightHint= convertHeightInCharsToPixels(3);
		gd.grabExcessVerticalSpace= false;
		gd.widthHint= getMaxFieldWidth();
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
		int nColumns= 4;
		
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
				dialogChanged();
			}
		});
		
		label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gd_2 = new GridData(GridData.FILL_HORIZONTAL);
		gd_2.horizontalSpan = nColumns;
		label.setLayoutData(gd_2);
		
		createImportsControl(composite, nColumns);
		
		setControl(composite);
		dialogChanged();
	}
	
	@Override
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		dialogChanged();
	}
	
	private void dialogChanged() {
		
		StatusInfo si = new StatusInfo(StatusInfo.OK, "");

		if (patternText != null) {
			String patternName = patternText.getText();
			if (patternName == null || patternName.length() == 0) {
				si.setError(PATTERN_NAME_MUST_BE_SPECIFIED);
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
