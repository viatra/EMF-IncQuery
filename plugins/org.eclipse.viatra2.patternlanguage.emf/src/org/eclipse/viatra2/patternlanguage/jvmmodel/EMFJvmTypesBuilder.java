package org.eclipse.viatra2.patternlanguage.jvmmodel;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.common.types.JvmConstructor;
import org.eclipse.xtext.common.types.TypesFactory;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

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
