package org.eclipse.viatra2.patternlanguage.core.scoping;

import java.util.Collections;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionStrategy;
import org.eclipse.xtext.util.IAcceptor;

/**
 * Custom strategy for computing ResourceDescription for eiq resources.
 * Adds user data for Pattern EObjectDescription about private modifier.
 * @author Mark Czotter
 *
 */
public class PatternLanguageResourceDescriptionStrategy extends DefaultResourceDescriptionStrategy {
	
	@Override
	public boolean createEObjectDescriptions(EObject eObject,
			IAcceptor<IEObjectDescription> acceptor) {
		if (eObject instanceof Pattern) {
			boolean isPrivate = CorePatternLanguageHelper.isPrivate((Pattern)eObject);
			QualifiedName qualifiedName = getQualifiedNameProvider().getFullyQualifiedName(eObject);
			if (qualifiedName != null) {
				acceptor.accept(EObjectDescription.create(qualifiedName, eObject, Collections.singletonMap("private", String.valueOf(isPrivate))));
			}
			return true;
		}
		return super.createEObjectDescriptions(eObject, acceptor);
	}

}