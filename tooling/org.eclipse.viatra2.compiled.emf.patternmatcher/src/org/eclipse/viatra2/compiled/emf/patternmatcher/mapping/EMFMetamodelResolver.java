/*******************************************************************************
 * Copyright (c) 2004-2009 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.compiled.emf.patternmatcher.mapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.viatra2.core.IEntity;
import org.eclipse.viatra2.core.IModelElement;
import org.eclipse.viatra2.core.IModelManager;
import org.eclipse.viatra2.core.IRelation;
import org.eclipse.viatra2.framework.IFramework;

/**
 * This class resolves ECore metamodels from their imported VPM representations. 
 * @author Bergmann Gábor
 *
 */
public class EMFMetamodelResolver {

	IFramework fw;
	IModelManager modelManager;
	
	IEntity entDataTypes = null;	

	IRelation relName = null;
	IRelation relSFName = null;
	IRelation relUri = null;
	IRelation relPrefix = null;
	
	Map<String, EPackage> supplementaryEPackages;
	Map<IModelElement, EPackage> ePackageCache;
	Map<IModelElement, EClassifier> eClassifierCache;
	Map<IModelElement, EStructuralFeature> eStructuralFeatureCache;
	
	/**
	 * 
	 * @param fw
	 * @param supplementaryEPackages (nsUri, EPackage) of pre-resolved EPackages
	 */
	public EMFMetamodelResolver(IFramework fw, Map<String, EPackage> supplementaryEPackages) {
		super();
		this.fw = fw;
		this.modelManager = fw.getTopmodel().getModelManager();
		
		ePackageCache = new HashMap<IModelElement, EPackage>();
		eClassifierCache = new HashMap<IModelElement, EClassifier>();
		eStructuralFeatureCache = new HashMap<IModelElement, EStructuralFeature>();
		this.supplementaryEPackages = 
			supplementaryEPackages == null ? 
					Collections.<String, EPackage>emptyMap() : supplementaryEPackages;
		init();

	}
	
	void init() {
		// compatible with both versions of nEmf
		String prefix = "emf";
		if (modelManager.getEntityByName("nemf")!=null) prefix = "nemf";
		
		entDataTypes = modelManager.getEntityByName(prefix+".ecore.datatypes");
		relName = modelManager.getRelationByName(prefix+".ecore.ENamedElement.name");
		relSFName = modelManager.getRelationByName(prefix+".ecore.EClass.EStructuralFeature.name");
		relUri = modelManager.getRelationByName(prefix+".ecore.EPackage.nsUri");
		relPrefix = modelManager.getRelationByName(prefix+".ecore.EPackage.nsPrefix");
	}
	
	public EPackage resolveEPackage(IModelElement element) {
		EPackage result = ePackageCache.get(element);
		if (result == null) {
			result = getEPackage(element);
			ePackageCache.put(element, result);
		}
		return result;
	}

	public EClassifier resolveEClassifier(IModelElement element) {
		EClassifier result = eClassifierCache.get(element);
		if (result == null) {
			result = getEClassifier(element);
			eClassifierCache.put(element, result);
		}
		return result;		
	}

	public EStructuralFeature resolveEStructuralFeature(IModelElement element) {
		EStructuralFeature result = eStructuralFeatureCache.get(element);
		if (result == null) {
			result = getEStructuralFeature(element);
			eStructuralFeatureCache.put(element, result);
		}
		return result;
	}


	private EPackage getEPackage(IModelElement element) {
		if (element != null && element instanceof IEntity) {
			String packageUri = extractNsUri(element);
			if (packageUri!=null) {
				EPackage ePackage = supplementaryEPackages.get(packageUri); // first try the pre-resolved packages
				if (ePackage == null) { // then the registry
					ePackage = EPackage.Registry.INSTANCE.getEPackage(packageUri);
				}
				return ePackage;
			}
		}
		return null;
	}

	private EClassifier getEClassifier(IModelElement element) {
		if (element != null && element instanceof IEntity) {
			String classifierName = extractName(element);
			IEntity parent = ((IEntity)element).getParent();
			if (entDataTypes != null && parent == entDataTypes) { // first try classes of ECore
				return EcoreFactory.eINSTANCE.getEcorePackage().getEClassifier(classifierName);
			} else { // then classes of custom packages
				EPackage ePackage = resolveEPackage(parent);
				if (classifierName!=null && ePackage!=null) 
					return ePackage.getEClassifier(classifierName);
			}
		}
		return null;		
	}

	private EStructuralFeature getEStructuralFeature(IModelElement element) {
		if (element != null && element instanceof IRelation) {
			String featureName = extractName(element);
			IModelElement from = ((IRelation)element).getFrom();
			if (featureName!=null) {
				EClassifier classifier = resolveEClassifier(from);
				if (classifier!=null && classifier instanceof EClass) 
					return ((EClass)classifier).getEStructuralFeature(featureName);
			}
		}
		return null;
	}	
	
	
	public String extractName(IModelElement element) {
		String name1 = extractAttribute(element, relName);
		return name1 !=null ? name1 : extractAttribute(element, relSFName);
	}

	public String extractNsUri(IModelElement ePackage) {
		return extractAttribute(ePackage, relUri);
	}

	public String extractNsPrefix(IModelElement ePackage) {
		return extractAttribute(ePackage, relPrefix);
	}

	public String extractAttribute(IModelElement element, IRelation attributeType) {
		if (element==null || attributeType == null) return null;
		IModelElement attribute = element.getRelationTargetByType(attributeType);
		if (attribute != null && attribute instanceof IEntity) {
			return ((IEntity)attribute).getValue();
		} else return null;
	}

}
