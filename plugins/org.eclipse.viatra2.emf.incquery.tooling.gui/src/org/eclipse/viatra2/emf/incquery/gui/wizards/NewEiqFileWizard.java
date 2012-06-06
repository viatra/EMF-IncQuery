package org.eclipse.viatra2.emf.incquery.gui.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguageFactory;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguageFactory;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.inject.Inject;

public class NewEiqFileWizard extends Wizard implements INewWizard {
	private NewEiqFileWizardFirstPage page1;
	private NewEiqFileWizardSecondPage page2;
	private ISelection selection;
	private IWorkbench workbench;
	
	IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	private IPath filePath;
	
	@Inject
	private IResourceSetProvider resourceSetProvider;
	
	public NewEiqFileWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages() {
		page1 = new NewEiqFileWizardFirstPage();
		page1.init((IStructuredSelection) selection);
		page2 = new NewEiqFileWizardSecondPage();
		page2.init((IStructuredSelection) selection);
		addPage(page1);
		addPage(page2);
		setForcePreviousAndNextButtons(false);
	}

	@Override
	public boolean performFinish() {
		//page 1 is completed and finished button is pressed
		if (page1.isPageComplete()) {
			
		}
		final String containerName = page1.getContainerName();
		final String fileName = page1.getFileName();
		final String patternName = "";
				//page1.getPatternName();
		final String packageName = page1.getPackageName().replaceAll("\\.", "/");
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, packageName, fileName, patternName, monitor);
				} catch (Exception e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
			IFile file = (IFile) root.findMember(filePath);
			BasicNewResourceWizard.selectAndReveal(file, workbench.getActiveWorkbenchWindow());
			IDE.openEditor(workbench.getActiveWorkbenchWindow().getActivePage(), file, true);
		} catch (InterruptedException e) {
			//This is never thrown as of false cancellable parameter of getContainer().run
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			IncQueryGUIPlugin.getDefault().logException(
					"Cannot create Query Definition file: "
							+ realException.getMessage(), realException);
			MessageDialog.openError(getShell(), "Error",
					realException.getMessage());
			return false;
		} catch (PartInitException e) {
			IncQueryGUIPlugin.getDefault().logException("Cannot open editor: " + e.getMessage(), e);
			MessageDialog.openError(getShell(), "Error", e.getMessage());
		}
		return true;
	}
	
	private void doFinish(String containerPath, String packageName, String fileName, String patternName, IProgressMonitor monitor) {
		monitor.beginTask("Creating " + fileName, 1);
		createEiqFile(containerPath, packageName, fileName, patternName);
		monitor.worked(1);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		this.workbench = workbench;
	}
	
	private void createEiqFile(String containerPath, String packageName, String fileName,	String patternName) {

		IResource containerResource = root.findMember(new Path(containerPath));
		ResourceSet resourceSet = resourceSetProvider.get(containerResource.getProject());

		filePath = containerResource.getFullPath().append(packageName+"/"+fileName);
		String fullPath = filePath.toString();
		
		URI fileURI = URI.createPlatformResourceURI(fullPath, false);
		Resource resource = resourceSet.createResource(fileURI);

		PatternModel pm = EMFPatternLanguageFactory.eINSTANCE.createPatternModel();
		if (page1.getPackageName() != null && !page1.getPackageName().isEmpty()) {
			pm.setPackageName(page1.getPackageName());
		}
		if (patternName != null && patternName.length() > 0) {
			Pattern pattern = PatternLanguageFactory.eINSTANCE.createPattern();
			pattern.setName(patternName);
			PatternBody body = PatternLanguageFactory.eINSTANCE
					.createPatternBody();
			pattern.getBodies().add(body);
			pm.getPatterns().add(pattern);
		}
		resource.getContents().add(pm);

		try {
			resource.save(Collections.EMPTY_MAP);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}