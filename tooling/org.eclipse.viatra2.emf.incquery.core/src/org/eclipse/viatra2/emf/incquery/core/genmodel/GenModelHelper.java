package org.eclipse.viatra2.emf.incquery.core.genmodel;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra2.emf.incquery.core.project.IncQueryNature;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.EcoreModel;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncQueryGenmodel;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncquerygenmodelFactory;

/**
 * @author Zoltan Ujhelyi
 */
public class GenModelHelper {

	/**
	 * Initializes an IncQuery generator model in the project
	 * @param path a project-relative path to create an IncQuery genmodel
	 * @param project
	 */
	public static void createGenmodel(IPath path, IProject project) {
		try {
			IPath fullPath = project.getFullPath().append(path);
			ResourceSet set = new ResourceSetImpl();
			Resource resource = set.createResource(URI
					.createPlatformResourceURI(fullPath.toString(), true));
			IncQueryGenmodel genModel = IncquerygenmodelFactory.eINSTANCE
					.createIncQueryGenmodel();
			resource.getContents().add(genModel);
			resource.save(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param file
	 *            the .incquery genmodel file, or null.
	 * @throws {@link RuntimeException}
	 * @return the parsed IncQueryGenmodel, or null if not available
	 */
	public static IncQueryGenmodel parseGenModel(IFile file) {
		if (file == null) return null;
		ResourceSet resourceSet = new ResourceSetImpl();
		URI fileURI = URI.createPlatformResourceURI(file.getFullPath()
				.toString(), false);
		Resource resource = resourceSet.getResource(fileURI, true);
		if (resource != null && resource.getContents().size() == 1) {
			EObject topElement = resource.getContents().get(0);
			return topElement instanceof IncQueryGenmodel ? (IncQueryGenmodel) topElement
					: null;
		} else
			return null;
	}
	/**
	 * @param project
	 *            the IncQuery project, or null.
	 * @throws {@link RuntimeException}
	 * @return the parsed IncQueryGenmodel, or null if not available
	 */
	public static IncQueryGenmodel parseGenModel(IProject project) {
		if (project == null) return null;
		IFile file = project.getFile(IncQueryNature.IC_GENMODEL);
		return parseGenModel(file);
	}
	
	public static void addEcoreGenModelToIncQueryGenModel(IncQueryGenmodel iqGen, GenModel genModel) {
		EcoreModel ecoreModel = IncquerygenmodelFactory.eINSTANCE.createEcoreModel();
		ecoreModel.setModels(genModel);
		iqGen.getEcoreModel().add(ecoreModel);
		try {
			iqGen.eResource().save(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
