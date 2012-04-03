package org.eclipse.viatra2.patternlanguage.jvmmodel;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.xbase.jvmmodel.JvmModelAssociator;

/**
 * This subClass is needed for local variable scoping. PatternBody not associated with any Inferred classes.
 * 
 * @author Mark Czotter
 * 
 */
public class EMFPatternJvmModelAssociator extends JvmModelAssociator {

	@Override
	public JvmIdentifiableElement getLogicalContainer(EObject object) {
		if (object instanceof PatternBody) return null;
		return super.getLogicalContainer(object);
	}
	
}
