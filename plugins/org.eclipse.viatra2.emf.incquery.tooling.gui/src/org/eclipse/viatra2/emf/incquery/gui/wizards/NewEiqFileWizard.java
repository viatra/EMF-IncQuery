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
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguageFactory;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguageFactory;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.inject.Inject;

public class NewEiqFileWizard extends Wizard implements INewWizard {
	private NewEiqFileWizardPage page;
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
	
	public void addPages() {
		page = new NewEiqFileWizardPage();
		page.init((IStructuredSelection) selection);
		addPage(page);
		setForcePreviousAndNextButtons(false);
	}

	public boolean performFinish() {
		final String containerName = page.getContainerName();
		final String fileName = page.getFileName();
		final String patternName = page.getPatternName();
		final String packageName = page.getPackageName().replaceAll("\\.", "/");
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, packageName, fileName, patternName, monitor);
				} catch (Exception e) {
					e.printStackTrace();
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
			e.printStackTrace();
			return false;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		} catch (PartInitException e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Error", e.getMessage());
		}
		return true;
	}
	
	private void doFinish(String containerPath, String packageName, String fileName, String patternName, IProgressMonitor monitor) {
		monitor.beginTask("Creating " + fileName, 1);
		createEiqFile(containerPath, packageName, fileName, patternName);
		monitor.worked(1);
	}

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
		if (page.getPackageName() != null && !page.getPackageName().isEmpty()) {
			pm.setPackageName(page.getPackageName());
		}
		Pattern pattern = PatternLanguageFactory.eINSTANCE.createPattern();
		pattern.setName(patternName);
		PatternBody body = PatternLanguageFactory.eINSTANCE.createPatternBody();
		pattern.getBodies().add(body);
		pm.getPatterns().add(pattern);
		resource.getContents().add(pm);

		try {
			resource.save(Collections.EMPTY_MAP);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}