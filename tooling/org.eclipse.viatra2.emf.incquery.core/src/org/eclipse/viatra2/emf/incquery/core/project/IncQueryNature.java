package org.eclipse.viatra2.emf.incquery.core.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Zoltan Ujhelyi
 */
public class IncQueryNature implements IProjectNature {

	/**
	 * The project nature identifier used for defining the project nature of an
	 * IncQuery project.
	 */
	public static final String NATURE_ID = "org.eclipse.viatra2.emf.incquery.projectnature"; //$NON-NLS-1$
	public static final String BUNDLE_ID = "org.eclipse.viatra2.emf.incquery.core"; //$NON-NLS-1$
	public static final String MODEL_BUNDLE_ID = "org.eclipse.viatra2.emf.importer.generic.core"; //$NON-NLS-1$
	public static final String MODELS_DIR = "models"; //$NON-NLS-1$
	public static final String VTCL_DIR = "models/vtcl"; //$NON-NLS-1$
	public static final String SOURCE_VPML = "models/nemf_xml.vpml"; //$NON-NLS-1$
	public static final String TARGET_VPML = "models/model.vpml"; //$NON-NLS-1$
	public static final String IC_GENMODEL = "models/generator.incquery"; //$NON-NLS-1$
	public static final String SRCGEN_DIR = "src-gen"; //$NON-NLS-1$
	public static final String SRC_DIR = "src"; //$NON-NLS-1$
	public static final String GENERATED_BUILDERS_DIR = "src-gen/patternbuilders"; //$NON-NLS-1$
	public static final String GENERATED_BUILDERS_PACKAGEROOT = "patternbuilders"; //$NON-NLS-1$
	public static final String GENERATED_MATCHERS_DIR = "src-gen/patternmatchers"; //$NON-NLS-1$
	public static final String GENERATED_MATCHERS_PACKAGEROOT = "patternmatchers"; //$NON-NLS-1$
	public static final String GENERATED_DTO_DIR = "src-gen/signatures"; //$NON-NLS-1$
	public static final String GENERATED_DTO_PACKAGEROOT = "signatures"; //$NON-NLS-1$
	public static final String GENERATED_HANDLER_DIR = "src/handlers"; //$NON-NLS-1$
	public static final String GENERATED_HANDLER_PACKAGEROOT = "handlers"; //$NON-NLS-1$
	public static final String PLUGIN_XML = "plugin.xml"; //$NON-NLS-1$
	public static final String SOURCE_BUILD_PROPERTIES = "templates/build.properties"; //$NON-NLS-1$
	IProject project;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	public IProject getProject() {
		return project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core
	 * .resources.IProject)
	 */
	public void setProject(IProject project) {
		this.project = project;
	}

	public void configure() throws CoreException {
	}

	public void deconfigure() throws CoreException {
	}

}
