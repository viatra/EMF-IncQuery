package org.eclipse.viatra2.emf.incquery.gui.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguageFactory;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguageFactory;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.inject.Inject;

public class NewEiqFileWizard extends Wizard implements INewWizard {
	private NewEiqFileWizardPage page;
	private ISelection selection;

	@Inject
	IResourceSetProvider resourceSetProvider;
	
	public NewEiqFileWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	public void addPages() {
		page = new NewEiqFileWizardPage();
		page.init((IStructuredSelection) selection);
		addPage(page);
	}

	public boolean performFinish() {
		final String containerName = page.getContainerName();
		final String fileName = page.getFileName();
		final String patternName = page.getPatternName();
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, fileName, patternName, monitor);
				} catch (Exception e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	private void doFinish(String containerName, String fileName, String patternName, IProgressMonitor monitor) throws IOException {
		
		monitor.beginTask("Creating " + fileName, 1);
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource containerResource = root.findMember(new Path(containerName));
		ResourceSet resourceSet = resourceSetProvider.get(containerResource.getProject());

		URI fileURI = URI.createPlatformResourceURI(containerResource.getFullPath().append(fileName).toString(), false);
		Resource resource = resourceSet.createResource(fileURI);
		
		PatternModel pm = EMFPatternLanguageFactory.eINSTANCE.createPatternModel();
		Pattern pattern = PatternLanguageFactory.eINSTANCE.createPattern();
		pattern.setName(patternName);
		PatternBody body = PatternLanguageFactory.eINSTANCE.createPatternBody();
		pattern.getBodies().add(body);
		
		pm.getPatterns().add(pattern);

		resource.getContents().add(pm);
		
		resource.save(Collections.EMPTY_MAP);
		
		monitor.worked(1);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}