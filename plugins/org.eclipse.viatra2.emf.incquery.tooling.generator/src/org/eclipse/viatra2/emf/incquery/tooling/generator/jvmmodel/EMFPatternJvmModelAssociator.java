package org.eclipse.viatra2.emf.incquery.tooling.generator.jvmmodel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.emf.incquery.tooling.generator.builder.IErrorFeedback;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.resource.DerivedStateAwareResource;
import org.eclipse.xtext.xbase.jvmmodel.JvmModelAssociator;

import com.google.inject.Inject;

/**
 * This subClass is needed for local variable scoping. PatternBody not associated with any Inferred classes.
 * 
 * @author Mark Czotter
 * 
 */
public class EMFPatternJvmModelAssociator extends JvmModelAssociator {

	@Inject
	IErrorFeedback feedback;
	
	@Override
	public JvmIdentifiableElement getLogicalContainer(EObject object) {
		if (object instanceof PatternBody) return null;
		return super.getLogicalContainer(object);
	}

	@Override
	public void installDerivedState(DerivedStateAwareResource resource,
			boolean preIndexingPhase) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IFile file = root.getFile(new Path(resource.getURI().toPlatformString(true)));
		feedback.clearMarkers(file, IErrorFeedback.JVMINFERENCE_ERROR_TYPE);
		super.installDerivedState(resource, preIndexingPhase);
	}
	
}
