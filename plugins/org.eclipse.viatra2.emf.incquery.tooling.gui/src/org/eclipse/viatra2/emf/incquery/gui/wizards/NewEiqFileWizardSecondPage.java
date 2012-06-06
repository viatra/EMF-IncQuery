package org.eclipse.viatra2.emf.incquery.gui.wizards;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaConventionsUtil;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;

@SuppressWarnings("restriction")
public class NewEiqFileWizardSecondPage extends NewTypeWizardPage {

	private Text fileText;
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
		importList.setLabelText("Imported packages");
		importList.setTableColumns(new ListDialogField.ColumnsDescription(1, false));
		importList.setLabelText(getSuperInterfacesLabel());
		importList.setRemoveButtonIndex(1);
		importList.doFillIntoGrid(parent, nColumns);
		final TableViewer tableViewer= importList.getTableViewer();
		tableViewer.setColumnProperties(new String[] {EPACKAGE});

		GridData gd= (GridData) importList.getListControl(null).getLayoutData();
		gd.heightHint= convertHeightInCharsToPixels(6);
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
		
		IStatus packageStatus = validatePackageName(getPackageText());
		
		StatusInfo si = new StatusInfo(packageStatus.getSeverity(), packageStatus.getMessage());
		//si.setOK();
		
		String containerPath = getPackageFragmentRootText();
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource containerResource = root.findMember(new Path(containerPath));
		
		if (containerResource == null) {
			si.setError("The given source folder does not exist");
		}

		if (fileText != null && patternText != null) {
		
			String fileName = fileText.getText();
			String patternName = patternText.getText();
	
			if (fileName.length() == 0) {
				si.setError("File name must be specified");
			}
			
			if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
				si.setError("File name must be valid");
			}
			
			boolean wrongExtension = false;
			
			if (!fileName.contains(".")) {
				wrongExtension = true;
			} else {
				int dotLoc = fileName.lastIndexOf('.');
				String ext = fileName.substring(dotLoc + 1);
				wrongExtension = !ext.equalsIgnoreCase("eiq"); 
			}
			
			if (wrongExtension) {
				si.setError("File extension must be \"eiq\"");
			}
	
			if (patternName == null || patternName.length() == 0) {
				si.setWarning("Pattern name should be specified");
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
	
	private IStatus validatePackageName(String text) {
		if (text == null || text.isEmpty()) {
			return new Status(IStatus.WARNING, IncQueryGUIPlugin.PLUGIN_ID, "The use of default package is discouraged.");
		}
		IJavaProject project= getJavaProject();
		if (project == null || !project.exists()) {
			 return JavaConventions.validatePackageName(text, JavaCore.VERSION_1_6, JavaCore.VERSION_1_6);
		}
		IStatus status = JavaConventionsUtil.validatePackageName(text, project);
		if (status.getSeverity() != ERROR && !text.toLowerCase().equals(text)) {
			return new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, "Only lower case package names supported.");
		}
		return status;
	}

	public String getFileName() {
		return fileText.getText();
	}

	public String getPatternName() {
		return patternText.getText();
	}
	
	public String getContainerName() {
		return getPackageFragmentRootText();
	}
	
	public String getPackageName() {
		IPackageFragmentRoot root = getPackageFragmentRoot();
		
		IPackageFragment fragment = root.getPackageFragment(getPackageText());
		
		if (!fragment.exists()) {
			try {
				root.createPackageFragment(getPackageText(), true, new NullProgressMonitor());
			} 
			catch (JavaModelException e) {
				IncQueryGUIPlugin.getDefault().logException(
						"Cannot load packages " + e.getMessage(), e);
			}
		}

		return getPackageText();
	}
}
