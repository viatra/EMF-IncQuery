package org.eclipse.viatra2.patternlanguage.jvmmodel;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.common.types.JvmConstructor;
import org.eclipse.xtext.common.types.TypesFactory;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * This subclass is needed for proper jvm model infer, because in Xtext 2.2 the JvmTypesBuilder infers
 * a constructor with default content ('{}'). This subclass ignores this default stuff. 
 * In the init procedure you can define the constructor body.
 * This bug is fixed in the Xtext 2.3 nightly.
 * @see http://www.eclipse.org/forums/index.php/m/798983/?srch=infer+constructor#msg_798983 
 * @author mark
 *
 */
@SuppressWarnings("restriction")
public class EMFJvmTypesBuilder extends JvmTypesBuilder {

   	public JvmConstructor toConstructor(EObject sourceElement, String simpleName, Procedure1<JvmConstructor> init) {
   		JvmConstructor constructor = TypesFactory.eINSTANCE.createJvmConstructor();
		constructor.setSimpleName(nullSaveName(simpleName));
		if (init != null && simpleName != null)
			init.apply(constructor);
		return associate(sourceElement, constructor);
	}
	
}
