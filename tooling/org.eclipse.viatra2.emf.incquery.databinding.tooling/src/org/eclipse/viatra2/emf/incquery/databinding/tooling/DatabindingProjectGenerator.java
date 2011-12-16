package org.eclipse.viatra2.emf.incquery.databinding.tooling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.core.project.IBundleProjectService;
import org.eclipse.viatra2.emf.incquery.core.codegen.CodeGenerationException;
import org.eclipse.viatra2.emf.incquery.core.codegen.util.CodegenSupport;
import org.eclipse.viatra2.emf.incquery.core.codegen.util.GTPatternJavaData;
import org.eclipse.viatra2.emf.incquery.core.codegen.util.ModulesLoader;
import org.eclipse.viatra2.emf.incquery.core.codegen.util.PatternsCollector;
import org.eclipse.viatra2.emf.incquery.core.project.IncQueryNature;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.EcoreModel;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncQueryGenmodel;
import org.eclipse.viatra2.framework.FrameworkException;
import org.eclipse.viatra2.framework.FrameworkManagerException;
import org.eclipse.viatra2.framework.IFramework;
import org.eclipse.viatra2.gtasm.support.helper.GTASMHelper;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.GTPattern;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class DatabindingProjectGenerator {
	
	IProject incQueryProject, databindingProject;
	IncQueryGenmodel iqGen;
	ModulesLoader modulesLoader;
	
	public DatabindingProjectGenerator(IProject incQueryProject,IncQueryGenmodel iqGen, IProgressMonitor monitor) throws CodeGenerationException {
		super();
		this.incQueryProject = incQueryProject;
		this.iqGen = iqGen;
		this.modulesLoader = new ModulesLoader(incQueryProject);
	}

	public void fullBuild(IProgressMonitor monitor) throws CodeGenerationException {
		try {
			buildProject(monitor);
			clean(monitor);
			buildAfterClean(monitor);
		} catch (OperationCanceledException e) {
			throw new CodeGenerationException("Error during EMF-IncQuery Databinding project generation. ", e);
		} catch (CoreException e) {
			throw new CodeGenerationException("Error during EMF-IncQuery Databinding project generation. ", e);
		} catch (FrameworkException e) {
			throw new CodeGenerationException("Error during EMF-IncQuery Databinding project generation. ", e);
		} catch (FrameworkManagerException e) {
			throw new CodeGenerationException("Error during EMF-IncQuery Databinding project generation. ", e);
		}
	}

	private void buildProject(IProgressMonitor monitor) throws OperationCanceledException, CoreException {

		BundleContext context = null;
		ServiceReference<?> ref = null;

		try {
			context = DatabindingToolingActivator.context;
			ref = context.getServiceReference(IBundleProjectService.class.getName());
			IBundleProjectService service = (IBundleProjectService) context.getService(ref);
			IBundleProjectDescription bundleDesc = service.getDescription(incQueryProject);
			this.databindingProject = DatabindingProjectSupport.createProject(monitor, bundleDesc.getBundleName(),incQueryProject.getName());
		}
		finally
		{if(context != null && ref != null)
			context.ungetService(ref);}
	}

	public void clean(IProgressMonitor monitor) throws CodeGenerationException {
		IProject project = getProject();
		IFolder folder = project.getFolder(IncQueryNature.SRCGEN_DIR);
		try {
			folder.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			for (IResource res : folder.members()) {
				res.delete(true, monitor);
			}
		} catch (CoreException e) {
			throw new CodeGenerationException("Error during cleanup before EMF-IncQuery code generation.", e);
		}
	}

	public void buildAfterClean(IProgressMonitor monitor) throws CodeGenerationException, FrameworkManagerException, CoreException, FrameworkException {
			IFramework framework = modulesLoader.loadFramework(incQueryProject);
			try {
				modulesLoader.loadAllModules(framework);
				Set<GTPattern> patterns = new PatternsCollector(framework).getCollectedPatterns();
				Map<GTPattern, GTPatternJavaData> gtPatternJavaRepresentations = generateGTPatternJavaData(patterns, monitor);
				
				DatabindingAdapterGenerator matcherGenerator = new DatabindingAdapterGenerator(gtPatternJavaRepresentations, getProject(), incQueryProject);
				matcherGenerator.generateActivator(databindingProject.getName() , monitor);

				Map<String,DatabindingAdapterData> databindableMatchers = matcherGenerator.generateHandlersForPatternMatcherCalls(monitor);
				ExtensionsforDatabindingProjectGenerator extensionGenerator = new ExtensionsforDatabindingProjectGenerator(databindingProject,iqGen);
				extensionGenerator.contributeToExtensionPoint(databindableMatchers, getEditorId2DomainMap(iqGen), monitor);
							
			} finally {
				modulesLoader.disposeFramework(framework);
			}		
	}

	@SuppressWarnings("unused")
	private Collection<String> getFileExtension(IncQueryGenmodel incqueryGenmodel) {
		List<String> fileExtensions = new ArrayList<String>(); 
		
		for(EcoreModel ecoreModel :incqueryGenmodel.getEcoreModel())
			for(GenPackage genPackage: ecoreModel.getModels().getAllGenPackagesWithClassifiers()) 
			{
				fileExtensions.add(genPackage.getFileExtension());
			}
		return fileExtensions;
	}
	
	private Map<String,String> getEditorId2DomainMap(IncQueryGenmodel incqueryGenmodel) {
		Map<String,String> editorIDs = new HashMap<String, String>(); 
		
		for(EcoreModel ecoreModel :incqueryGenmodel.getEcoreModel()){
			for(EObject genp : ecoreModel.getModels().eContents()){
				if(genp instanceof GenPackage){
					editorIDs.put(((GenPackage) genp).getQualifiedEditorClassName()+"ID", ((GenPackage) genp).getNSName());
				}
			}
		}
		
		return editorIDs;
	}

	private Map<GTPattern, GTPatternJavaData> generateGTPatternJavaData(
			Set<GTPattern> patterns,
			IProgressMonitor monitor) throws CodeGenerationException {
		Map<GTPattern, GTPatternJavaData>  datas = new HashMap<GTPattern, GTPatternJavaData>();
		
		for(GTPattern pattern: patterns) {
			
			Map<String, String> annotation = GTASMHelper.extractLowerCaseRuntimeAnnotation(pattern, "@PatternUI");
			if (annotation != null){
				GTPatternJavaData data = new GTPatternJavaData();
				data.setPatternName(pattern.getName());
				
				//matcher
				IPath pathRoot = incQueryProject.getFolder(IncQueryNature.GENERATED_MATCHERS_DIR).getFullPath();
				String packageNameRoot = IncQueryNature.GENERATED_MATCHERS_PACKAGEROOT;
				CodegenSupport.PackageLocationFinder matcherPLF= new CodegenSupport.PackageLocationFinder(pattern, pathRoot, packageNameRoot, monitor);
				//sets the matcher and the matcher's package
				data.setMatcherName(getMatcherName(pattern));
				data.setMatcherPackage(matcherPLF.getJavaPackageName()+"."+getMatcherName(pattern));
				
				//singature
				pathRoot = incQueryProject.getFolder(IncQueryNature.GENERATED_DTO_DIR).getFullPath();
				packageNameRoot = IncQueryNature.GENERATED_DTO_PACKAGEROOT;
				CodegenSupport.PackageLocationFinder signaturePLF= new CodegenSupport.PackageLocationFinder(pattern, pathRoot, packageNameRoot, monitor);
				//sets the matcher and the matcher's package
				data.setSignatureName(getSignatureName(pattern));
				data.setSignaturePackage(signaturePLF.getJavaPackageName()+"."+getSignatureName(pattern));
				
				datas.put(pattern, data);
			}
		}
		return datas;
	}
	
	private String getMatcherName(GTPattern pattern) {
		String pName = pattern.getName().substring(1);
		return Character.toUpperCase(pattern.getName().charAt(0))+pName +"Matcher";
		
	}
	
	private String getSignatureName(GTPattern pattern) {
		String pName = pattern.getName().substring(1);
		return Character.toUpperCase(pattern.getName().charAt(0))+pName +"Signature";
	}

	public IProject getProject() {
		return databindingProject;
	}
}
