package org.eclipse.viatra2.emf.incquery.gui.wizards;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
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

@SuppressWarnings("restriction")
public class NewEiqFileWizardPage extends NewTypeWizardPage {

	private Text fileText;
	private Text patternText;
	
	public NewEiqFileWizardPage() {
		super(false, "eiq");
		setTitle("EMF-IncQuery query definition Wizard");
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
		initializeDialogUnits(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		int nColumns= 4;

		GridLayout layout= new GridLayout();
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		
		createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);		
		
		Label label = new Label(composite, SWT.NULL);
		label.setText("&File name:");
		fileText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		fileText.setText("default.eiq");
		GridData gd_1 = new GridData(GridData.FILL_HORIZONTAL);
		gd_1.horizontalSpan = 3;
		fileText.setLayoutData(gd_1);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gd_3 = new GridData(GridData.FILL_HORIZONTAL);
		gd_3.horizontalSpan = 4;
		label.setLayoutData(gd_3);

		label = new Label(composite, SWT.NULL);
		label.setText("&Pattern name:");
		patternText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		GridData gd_2 = new GridData(GridData.FILL_HORIZONTAL);
		gd_2.horizontalSpan = 3;
		patternText.setLayoutData(gd_2);
		patternText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		setControl(composite);
		
		dialogChanged();
	}
	
	@Override
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);

		dialogChanged();
	}
	
	private void dialogChanged() {
		
		StatusInfo si = new StatusInfo();
		si.setOK();
		
		String containerPath = getPackageFragmentRootText();
		String packageName = getPackageText();
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource containerResource = root.findMember(new Path(containerPath));
		
		if (containerResource == null) {
			si.setError("The given source folder does not exist");
		}
		else {
			
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
			}
			else {
				int dotLoc = fileName.lastIndexOf('.');
				String ext = fileName.substring(dotLoc + 1);
				if (ext.equalsIgnoreCase("eiq") == false) {
					wrongExtension = true;
				}
			}
			
			if (wrongExtension) {
				si.setError("File extension must be \"eiq\"");
			}
	
			if (patternName.length() == 0 || patternName == null) {
				si.setError("Pattern name must be specified");
			}
		}

		updateStatus(si);
		
		if (si.isError()) {
			setErrorMessage(si.getMessage());
		}
	}

	public String getFileName() {
		return fileText.getText();
	}

	public String getPatternName() {
		return patternText.getText();
	}
	
	public String getContainerName() {
		return getPackageFragmentRootText()+"/"+getPackageText();
	}
}
