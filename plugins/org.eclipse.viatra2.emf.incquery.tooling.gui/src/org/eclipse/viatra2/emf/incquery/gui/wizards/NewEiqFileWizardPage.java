package org.eclipse.viatra2.emf.incquery.gui.wizards;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewEiqFileWizardPage extends NewTypeWizardPage {

	private Text fileText;
	private Text patternText;
	
	public NewEiqFileWizardPage() {
		super(false, "eiq");
		setTitle("EMF-IncQuery query definition Wizard");
		setDescription("This wizard creates a new file with *.eiq extension.");
	}
	
	public void init(IStructuredSelection selection) {
		IJavaElement jelem= getInitialJavaElement(selection);
		initContainerPage(jelem);
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

		Dialog.applyDialogFont(composite);
	}
	
	private void dialogChanged() {
		
		String fileName = fileText.getText();
		String patternName = patternText.getText();

		if (fileName.length() == 0) {
			updateStatusText("File name must be specified");
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatusText("File name must be valid");
			return;
		}

		if (patternName.length() == 0) {
			updateStatusText("Pattern name must be specified");
			return;
		}

		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("eiq") == false) {
				updateStatusText("File extension must be \"eiq\"");
				return;
			}
		}
		
		updateStatusText(null);
	}
	
	private void updateStatusText(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
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
