package org.eclipse.viatra2.emf.incquery.tooling.generator.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.common.types.JvmConstructor;
import org.eclipse.xtext.common.types.JvmLowerBound;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.JvmWildcardTypeReference;
import org.eclipse.xtext.common.types.TypesFactory;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

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
	 * This overriden method is needed for proper constructor inference. In
	 * Xtext 2.2 the {@link JvmTypesBuilder} infers constructor with default
	 * content <code>('{}')</code>. This method ignores this default stuff. This bug is fixed
	 * in the Xtext 2.3 nightly.
	 * In the init {@link Procedure1} you can define the constructor body.
	 * @see <a
	 *      href="http://www.eclipse.org/forums/index.php/m/798983/?srch=infer+constructor#msg_798983">Eclipse
	 *      Forum Message</a>
	 * @return {@link JvmConstructor}
	 */
   	public JvmConstructor toConstructor(EObject sourceElement, String simpleName, Procedure1<JvmConstructor> init) {
   		JvmConstructor constructor = TypesFactory.eINSTANCE.createJvmConstructor();
		constructor.setSimpleName(nullSaveName(simpleName));
		if (init != null && simpleName != null)
			init.apply(constructor);
		return associate(sourceElement, constructor);
	}
   	
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
