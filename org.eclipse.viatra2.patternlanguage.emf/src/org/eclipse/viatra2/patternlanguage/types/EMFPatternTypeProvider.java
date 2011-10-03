package org.eclipse.viatra2.patternlanguage.types;

import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.util.TypeReferences;
import org.eclipse.xtext.xbase.typing.XbaseTypeProvider;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class EMFPatternTypeProvider extends XbaseTypeProvider {

	@Inject
	private TypeReferences typeReferences;
	
	protected JvmTypeReference _typeForIdentifiable(Variable variable, boolean rawType) {
		if (variable.getType() != null) {
			return typeReferences.getTypeForName(variable.getType().getTypename(), variable);
		} else {
			return typeReferences.createAnyTypeReference(variable);
		}
	}
}
