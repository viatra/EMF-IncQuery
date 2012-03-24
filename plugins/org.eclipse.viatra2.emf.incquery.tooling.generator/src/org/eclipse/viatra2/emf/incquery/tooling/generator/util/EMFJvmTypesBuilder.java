package org.eclipse.viatra2.emf.incquery.tooling.generator.util;

import org.eclipse.xtext.common.types.JvmLowerBound;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.JvmWildcardTypeReference;
import org.eclipse.xtext.common.types.TypesFactory;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder;

import com.google.inject.Inject;

/**
 * Custom {@link JvmTypesBuilder} for EMFPatternLanguage.
 * 
 * @author Mark Czotter
 * 
 */
@SuppressWarnings("restriction")
public class EMFJvmTypesBuilder extends JvmTypesBuilder {

	@Inject
	private TypesFactory factory = TypesFactory.eINSTANCE;
   	
	/**
	 * Creates a {@link JvmWildcardTypeReference} with a {@link JvmLowerBound}
	 * constraint to 'clone' parameter.
	 * 
	 * @param clone
	 * @return {@link JvmWildcardTypeReference} with a {@link JvmLowerBound}
	 *         contraint.
	 */
   	public JvmWildcardTypeReference wildCardSuper(JvmTypeReference clone) {
		JvmWildcardTypeReference result = factory.createJvmWildcardTypeReference();
		JvmLowerBound lowerBound = factory.createJvmLowerBound();
		lowerBound.setTypeReference(clone);
		result.getConstraints().add(lowerBound);
		return result;
   	}
	
}
