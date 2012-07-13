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

package org.eclipse.viatra2.emf.incquery.gui.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
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
import org.eclipse.viatra2.emf.incquery.gui.wizards.internal.ObjectParameter;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternLanguageFactory;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguageFactory;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PackageImport;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.inject.Inject;

/**
 * A wizard implementation used to create new eiq files.
 * 
 * @author Tamas Szabo
 *
 */
public class NewEiqFileWizard extends Wizard implements INewWizard {
	
	private static final String NEW_EMF_INC_QUERY_QUERY_DEFINITION_FILE = "Create a new EMF-IncQuery Query Definition file.";
	private NewEiqFileWizardContainerConfigurationPage page1;
	private NewEiqFileWizardPatternConfigurationPage page2;
	private ISelection selection;
	private IWorkbench workbench;
	private IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	private IPath filePath;
	
	@Inject
	private IResourceSetProvider resourceSetProvider;
	
	public NewEiqFileWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages() {
		page1 = new NewEiqFileWizardContainerConfigurationPage();
		page1.init((IStructuredSelection) selection);
		page1.setDescription(NEW_EMF_INC_QUERY_QUERY_DEFINITION_FILE);
		page2 = new NewEiqFileWizardPatternConfigurationPage();
		addPage(page1);
		addPage(page2);
		setForcePreviousAndNextButtons(false);
	}

	@Override
	public boolean performFinish() {
		final String containerName = page1.getContainerName();
		final String fileName = page1.getFileName();
		
		//replace dots with slash in the path
		final String packageName = page1.getPackageName().replaceAll("\\.", "/");
		final String patternName = page2.getPatternName();
		final List<EPackage> imports = page2.getImports();
		final List<ObjectParameter> parameters = page2.getParameters();
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, fileName, packageName, patternName, imports, parameters, monitor);
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
	
	private void doFinish(String containerName, String fileName, String packageName, String patternName, List<EPackage> imports, List<ObjectParameter> parameters, IProgressMonitor monitor) {
		monitor.beginTask("Creating " + fileName, 1);
		createEiqFile(containerName, fileName, packageName, patternName, imports, parameters);
		monitor.worked(1);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		this.workbench = workbench;
	}
	
	private void createEiqFile(String containerName, String fileName, String packageName, String patternName, List<EPackage> imports, List<ObjectParameter> parameters) {	
		IResource containerResource = root.findMember(new Path(containerName));
		ResourceSet resourceSet = resourceSetProvider.get(containerResource.getProject());

		filePath = containerResource.getFullPath().append(packageName+"/"+fileName);
		String fullPath = filePath.toString();
		
		URI fileURI = URI.createPlatformResourceURI(fullPath, false);
		Resource resource = resourceSet.createResource(fileURI);

		PatternModel pm = EMFPatternLanguageFactory.eINSTANCE.createPatternModel();

		//Setting package name
		if (packageName != null && !packageName.isEmpty()) {
			pm.setPackageName(packageName.replace("/", "."));
		}
		
		//Setting imports
		for (EPackage _package : imports) {
			PackageImport _import = EMFPatternLanguageFactory.eINSTANCE.createPackageImport();
			_import.setEPackage(_package);
			pm.getImportPackages().add(_import);
		}
		
		//Creating pattern
		if (patternName != null && patternName.length() > 0) {
			Pattern pattern = PatternLanguageFactory.eINSTANCE.createPattern();
			pattern.setName(patternName);
			PatternBody body = PatternLanguageFactory.eINSTANCE.createPatternBody();
			pattern.getBodies().add(body);
			
			//Setting pattern parameters
			for (ObjectParameter parameter : parameters) {
				Variable var = PatternLanguageFactory.eINSTANCE.createVariable();
				var.setName(parameter.getParameterName());
				
				ClassType classType = EMFPatternLanguageFactory.eINSTANCE.createClassType();
				//it is enough to set only the class name for the class type
				classType.setClassname((EClassifier) parameter.getObject());
				var.setType(classType);
				pattern.getParameters().add(var);
			}
			
			pm.getPatterns().add(pattern);
		}
		resource.getContents().add(pm);

		try {
			resource.save(Collections.EMPTY_MAP);
		} 
		catch (IOException e) {
			IncQueryGUIPlugin.getDefault().logException("Resource could not be saved", e);
		}
	}
}