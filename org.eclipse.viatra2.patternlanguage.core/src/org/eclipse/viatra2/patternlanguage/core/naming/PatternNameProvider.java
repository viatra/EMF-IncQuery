package org.eclipse.viatra2.patternlanguage.core.naming;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.naming.SimpleNameProvider;

import com.google.inject.Inject;

public class PatternNameProvider extends SimpleNameProvider {

	@Inject
	IQualifiedNameProvider nameProvider;
	@Inject
	IQualifiedNameConverter nameConverter;
	
	@Override
	public QualifiedName getFullyQualifiedName(EObject obj) {
		if (obj instanceof PatternBody) {
			PatternBody patternBody = (PatternBody) obj;
			Pattern pattern = (Pattern) patternBody.eContainer();
			return getFullyQualifiedName(pattern).append(
					Integer.toString(pattern.getBodies().indexOf(patternBody)));
		}
		return super.getFullyQualifiedName(obj);
	}

}
