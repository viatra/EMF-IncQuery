 package org.eclipse.viatra2.compiled.emf.gtasm.util;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;


//import viatra.gtasm.compiled.util.VPMUtil.Multipicity;

public class RelationWrapper implements IRelationWrapper {

	private EObject target, source;
	private EStructuralFeature feature;



//	((List)department.eGet((department.eClass()).getEStructuralFeature("RefEmployees"))).add(employee);

	/**
	 * @param s source of the relation
	 * @param t target of the relation
	 * @param name Name of the corresponding EStructuralFeature representing the relation
	 */
	RelationWrapper(EObject s, EObject t, String name)
	{
	target = t;
	source = s;
	feature = (EStructuralFeature)source.eGet((source.eClass()).getEStructuralFeature(name));
	}


	/**
	 * Creates the corresponding viatra.gtasm.compiled.emf.internal EAttribute or EReference held by its parameters
	 */

	public void create(){
		if(feature.isMany())
		{
			((EList)source.eGet(feature)).add(target);
		}
		else
			source.eSet(feature, target);
	}

	public void remove()
	{
		if(feature.isMany())
			((List)source.eGet(feature)).remove(target);
		else
			source.eUnset(feature);

		target = null;
		source = null;
		feature = null;
	}

	/*public  VPMUtil.Multipicity getMultpl()
	{
		if(feature.getUpperBound() != 1 || feature.getUpperBound() != 0)
				return VPMUtil.Multipicity.one_to_one;
		else
				return VPMUtil.Multipicity.one_to_many;
	}
	*/
	public EObject getSource(){ return source;  }
	public void setSource(EObject snew)
	{

		if(feature.isMany())
		{
			((EList)source.eGet(feature)).remove(target);
			((EList<EObject>)snew.eGet(feature)).add(target);
		}
		else
			source.eSet(feature, snew);

		source = snew;
	}

	public EObject getTarget(){ return target;}
	public void setTarget(EObject tnew)
	{
		if(feature.isMany())
		{
			((EList)source.eGet(feature)).remove(target);
			((EList<EObject>)source.eGet(feature)).add(tnew);
		}
		else
			source.eSet(feature, tnew);

		target = tnew;
	}


	/*public String getFQN() {
		return VPMUtil.getElementFQN(source)+"."+feature.getName();
	}
*/

	public String getName() {
		return feature.getName();
	}


	public String getValue() {
		// TODO Auto-generated method stub
		return null;
	}


	public void setName(String name) {
		// TODO Auto-generated method stub

	}


	public void setValue(String value) {
		// TODO Auto-generated method stub

	}

	//company.eSet(   (company.eClass()).getEStructuralFeature("AttrCompanyName"), "Bosch");

}
