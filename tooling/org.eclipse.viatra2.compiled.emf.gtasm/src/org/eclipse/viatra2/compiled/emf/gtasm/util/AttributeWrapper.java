/**
 * 
 */
package org.eclipse.viatra2.compiled.emf.gtasm.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author akinator
 *
 */
public class AttributeWrapper implements IVariable {

	private EObject source;
	private EStructuralFeature feature;
	
	
	AttributeWrapper(EObject s,  String name)
	{
	source = s;
	feature = (EStructuralFeature)source.eGet((source.eClass()).getEStructuralFeature(name));	
	}
	
	/* (non-Javadoc)
	 * @see viatra.gtasm.compiled.util.IVariable#getName()
	 */
	public String getName() {
		return feature.getName();
	}

	/* (non-Javadoc)
	 * @see viatra.gtasm.compiled.util.IVariable#getValue()
	 */
	public String getValue() {
		return source.eGet(feature).toString();
	}

	/* (non-Javadoc)
	 * @see viatra.gtasm.compiled.util.IVariable#setName(java.lang.String)
	 */
	public void setName(String name) {
		

	}

	/* (non-Javadoc)
	 * @see viatra.gtasm.compiled.util.IVariable#setValue(java.lang.String)
	 */
	public void setValue(String value) {
		//if(feature.isMany())
		//{
		//	((EList)source.eGet(feature)).add(value);
		//}	
		//else
			source.eSet(feature, value);
	}

}
