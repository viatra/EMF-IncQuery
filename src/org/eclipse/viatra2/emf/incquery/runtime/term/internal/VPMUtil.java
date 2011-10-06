/*******************************************************************************
 * Copyright (c) 2004-2010 Akos Horvath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.runtime.term.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;


public class VPMUtil {

	//private static ResourceSet topLevelResource = new ResourceSetImpl();
	//private static Resource tempResource;
	//private static HashMap<String, Vector> Instance = new HashMap<String, Vector>();
	private static HashMap<String, EClass> Type = new HashMap<String, EClass>();
	public static enum Multipicity { many_to_many, many_to_one, one_to_many, one_to_one};
	//private static Deleter del;



	//((EReference)feature).getEOpposite()


	//******************************INIT****************************
	public static void init()
	{
		//topLevelResource.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi",new org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl());
	//	tempResource = topLevelResource.createResource(URI.createURI("temporary"));
		//del = new Deleter();
		//del.setTopLevelResource(tempResource);
	}



	public static void addType(String name, EClass type)
	{
		Type.put(name,type);
	}

	public static String getUpName(String obj){
		return	obj.substring(0,1).toUpperCase().concat(obj.substring(1));

	}

	//*******************************CREATE****************************

	public static EObject createEntity(String type, Resource resource)
	{
		String name = randomNameGenerator();
		//if(Instance.get(type) == null)
		//		Instance.put(type, new Vector());

		EObject created = EcoreUtil.create(Type.get(type));
		created.eSet((created.eClass()).getEStructuralFeature("Name"), name);
		//Instance.get(type).add(created);
		resource.getContents().add(created);
		return created;
	}

	public static EObject createEntity(String type, EObject parent, Resource resource)
	{
		String name = randomNameGenerator();
		//if(Instance.get(type) == null)
		//		Instance.put(type, new Vector());

		EObject created = EcoreUtil.create(Type.get(type));
		created.eSet((created.eClass()).getEStructuralFeature("Name"), name);
		//Instance.get(type).add(created);
		resource.getContents().add(created);
		parent.eContents().add(created);
		return created;
	}

/*	public static RelationWrapper createRelation(EObject source, EObject target, String name)
	{
		return new RelationWrapper(source,target, name);
	}

	//******************************REMOVE*****************************
	public static void remove(EObject obj)
	{
	//	del.remove(obj);
	}

	public static void remove(IRelationWrapper rel)
	{
		rel.remove();

	}

	*/
//	It would be the union of the setting.getEObject().eResource() of each setting in the loop along with the eObject.eResource(). If you turn on tracking of modifications for the resources, you could save all the modified resources.
//
//
//	Peter Soung wrote:
//
//	    I now have a question on saving the resources after the references have been removed. Is there a way to determine the URI's of all the resources that have been changed and save them rather than manually saving the files that might change?


	public static void delete(EObject eObject)
     {
       EObject rootEObject = EcoreUtil.getRootContainer(eObject);
       Resource resource = rootEObject.eResource();
       Collection<Setting> usages;

       if (resource == null)
       {
         usages = EcoreUtil.UsageCrossReferencer.find(eObject, rootEObject);
       }
       else
       {
         ResourceSet resourceSet = resource.getResourceSet();
         if (resourceSet == null)
         {
           usages = EcoreUtil.UsageCrossReferencer.find(eObject, resource);
         }
         else
         {
           usages = EcoreUtil.UsageCrossReferencer.find(eObject, resourceSet);
         }
       }

       for (Iterator<Setting> i = usages.iterator(); i.hasNext(); )
       {
	        EStructuralFeature.Setting setting = i.next();
	         if (setting.getEStructuralFeature().isChangeable())
	         {
	        	 EcoreUtil.remove(setting, eObject);
	         }
       }
       EcoreUtil.remove(eObject);
     }


	/* Proxy Resolve
	 In viatra.gtasm.compiled.emf.internal
	2.2, there is support for automatic resolving of containment proxies, so
	things might work better there. You need to set the GenModel property
	"Containment Proxies" to true to generate the support for this.

	 */


	//****************************COPY**********************************

	public static EObject copy(EObject in)
	{
		EObject out = EcoreUtil.copy(in);

//		*********ez kell, hogy a copy menjen a content cuccokra is
		//if(in.eContainer() != null)
		//	((EList)in.eContainer().eGet(in.eContainmentFeature())).add(out); //container ez jol megy a containerre
		//nem sikerult meg rendesen tesztelni a dolgot :-(

	    /*

	     Using your suggestion I wrote a method to call .eContents on all objects,
	    if they are proxies I resolve, etc... This produces a complete containment
	    tree for my root object. I then call copier.copy(myRootObj) followed by
	    copier.copyRefernces(). To save the XMI resource I call
	    resource.getContents().addAll(copier.values()), followed by a save on the
	    resource. This now appears to work correctly.

	    However, I cannot read, or open, the XMI resource. I call
	    resourceSet.getResource(...) and resource contents includes all the
	    objects from the XMI file, but there are no relationships or references,
	    so no containment tree at all.

	    BTW, I am creating the XMI with viatra.gtasm.compiled.emf.internal 2.0.3 and loading with 2.2.0. Has the
	    XMI file format changed by chance.?

	    Don't do resource.getContents().addAll(copier.values()). That will
	    break all the containment references and flatten out the whole
	    structure. Only add the one root object to the new resource. In viatra.gtasm.compiled.emf.internal
	    2.2, there is support for automatic resolving of containment proxies, so
	    things might work better there. You need to set the GenModel property
	    "Containment Proxies" to true to generate the support for this.
	     */
		return out;

	}
	//target is avaiable
	public static EObject copy(EObject in, EObject target)
	{
		EObject out = EcoreUtil.copy(in);

//		*********ez kell, hogy a copy menjen a content cuccokra is
		if(target.eContainer() != null)
			((EList)target.eContainer().eGet(in.eContainmentFeature())).add(out); //container ez jol megy a containerre
		//nem sikerult meg rendesen tesztelni a dolgot :-(


		return out;

	}


	public static void move(EObject to, EObject value)
	{
		if(value.eContainer() != null)
			(value.eContainer()).eContents().remove(value);

		to.eContents().add(value);

	}


	public static EObject getByRef(String name)
	{return null;
		/*

		EList Resource::getContents()
		EList EObject::eContents() {Recursivly called}
		EClass EObject::eClass()
		boolean isSuperTypeOf(EClass)

		  for (Iterator i = resource.getAllContents(); i.hasNext(); )
    {
      Object object = i.next();
      if (eClass.isInstance(object))
      {
        //...
      }
    } */
	}

/*
	public static RelationWrapper getInverse(RelationWrapper in)
	{
		return null;
	}

	public static Multipicity getMultiplicity( RelationWrapper in)
	{
		return in.getMultpl();
	}
	*/
	public static String getType(EObject in)
	{   if(in == null) return "";

			if(in.eClass().eContainer() == null )
				return getPackageFQN(in.eClass().getEPackage())+in.eClass().getName();
			else
				return getType(in.eClass().eContainer())+"."+in.eClass().getName();

	}

	public static void print(String str){
		//TODO: the printStream can be modified!
		System.out.print(str);
	}

	public static void print(Object o){
		print(o.toString());
	}

	public static void println(Object o){
		System.out.println(o.toString());
	}

	public static void println(String str){
		//TODO: the printStream can be modified!
		System.out.println(str);
	}

	//***************************FQN*******************************


	private static String getPackageFQN(EPackage base)
	{
		if(base == null) return "";
		String s=base.getName();

		if(base.getESuperPackage() == null )
			return s+".";
		else
			return getPackageFQN(base.getESuperPackage())+s+".";

	}

	public static String getElementFQN(EObject in)
	{if(in == null) return "";

		if(in.eContainer() == null)
			return ((String)in.eGet(in.eClass().getEStructuralFeature("Name")));
		else
			return getElementFQN(in.eContainer())+"."+((String)in.eGet(in.eClass().getEStructuralFeature("Name")));
	}

	private static String randomNameGenerator()
	{

		return "";


	}


	//**************************CHECK*********************************

	public boolean isBelow(Object parent, Object child){return false;}

	public boolean isIN(Object parent, Object child){return false;}




	public static Object getByFQN(String string) {
		// TODO Auto-generated method stub
		return null;
	}


}
