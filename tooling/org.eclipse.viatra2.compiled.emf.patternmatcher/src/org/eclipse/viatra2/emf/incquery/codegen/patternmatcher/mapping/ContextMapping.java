/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.codegen.patternmatcher.mapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.viatra2.emf.incquery.codegen.patternmatcher.Activator;

/**
 * Establishes a mapping between Viatra terms and an EMF context.
 * Uses a two-level information model, with general metamodel mappings and additional information specialized for individual ResourceSets.
 * Use an advisor for each EMF metamodel to load this information; advisors can be deployed programmatically or using the provided extension point.
 * @author Bergmann Gábor
 */
public class ContextMapping {
	static ContextMapping generalInstance;
	static WeakHashMap<TransactionalEditingDomain, ContextMapping> specializedInstances = new WeakHashMap<TransactionalEditingDomain, ContextMapping>();
	/**
	 * Retrieves the general context mapping, effective for all EMF ResourceSets
	 * @return the general context mapping
	 */
	public static ContextMapping general(){
		if (generalInstance == null) generalInstance = new ContextMapping(true);
		return generalInstance;
	}
	/**
	 * Retrieves the specialized context mapping, extending or overriding the general case for a specific EMF ResourceSet
	 * @param domain
	 * @return the specialized context mapping
	 */
	public static ContextMapping specialized(TransactionalEditingDomain domain){
		ContextMapping instance = specializedInstances.get(domain);
		if (instance == null) {
			instance = new ContextMapping(false);
			specializedInstances.put(domain, instance);
		}
		return instance;
	}


	boolean general;

	final Map<EClass, Collection<EClass>> subtypesByClass= new HashMap<EClass, Collection<EClass>>();

	final Map<EStructuralFeature, String> fqnForEStructuralFeatures = new HashMap<EStructuralFeature, String>();
	final Map<EClassifier, String> fqnForEClassifiers = new HashMap<EClassifier, String>();
	final Map<String, EStructuralFeature> eStructuralFeaturesByFQN = new HashMap<String, EStructuralFeature>() { // ugly hack
		private static final long serialVersionUID = -5669469081601885476L;
		@Override
		public EStructuralFeature put(String key, EStructuralFeature value) {
			fqnForEStructuralFeatures.put(value, key);
			return super.put(key, value);
		}
		@Override
		public void putAll(Map<? extends String, ? extends EStructuralFeature> m) {
			for (Map.Entry<? extends String, ? extends EStructuralFeature> entry : m.entrySet())
				fqnForEStructuralFeatures.put(entry.getValue(), entry.getKey());
			super.putAll(m);
		}
	};
	final Map<String, EClassifier> eClassifiersByFQN= new HashMap<String, EClassifier>() { // VERY ugly hack
		private static final long serialVersionUID = 5208015325234188047L;
		@Override
		public EClassifier put(String key, EClassifier value) {
			fqnForEClassifiers.put(value, key);
			if (value instanceof EClass) gatherTypeRefinements((EClass)value);
			return super.put(key, value);
		}
		@Override
		public void putAll(Map<? extends String, ? extends EClassifier> m) {
			for (Map.Entry<? extends String, ? extends EClassifier> entry : m.entrySet()) {
				fqnForEClassifiers.put(entry.getValue(), entry.getKey());
				if (entry.getValue() instanceof EClass) gatherTypeRefinements((EClass)entry.getValue());
			}
			super.putAll(m);
		}

	};

	private void gatherTypeRefinements(EClass subtype) {
		for (EClass supertype : subtype.getESuperTypes())
		{
			Collection<EClass> subtypes = subtypesByClass.get(supertype);
			if (subtypes == null)  {
				subtypes = new LinkedHashSet<EClass>();
				subtypesByClass.put(supertype, subtypes);
			}
			subtypes.add(subtype);
		}
	}

	/**
	 * @param fullyQualifiedName the name of the type to retrieve
	 * @return the structural feature corresponding to this reference
	 */
	public EStructuralFeature retrieveReferenceType(String fullyQualifiedName) {
		EStructuralFeature result = eStructuralFeaturesByFQN.get(fullyQualifiedName);
		if (result == null && !general) return general().retrieveReferenceType(fullyQualifiedName);
		return result;
	}
	/**
	 * @param fullyQualifiedName the name of the type to retrieve
	 * @return the classifier corresponding to this entity
	 */
	public EClassifier retrieveEntityType(String fullyQualifiedName) {
		EClassifier result = eClassifiersByFQN.get(fullyQualifiedName);
		if (result == null && !general) return general().retrieveEntityType(fullyQualifiedName);
		return result;
	}
	/**
	 * @param type the type whose subtypes are queried
	 * @return the subtypes of the specified class
	 */
	public Collection<? extends Object> retrieveSubtypes(EClass type) {
		Collection<? extends Object> result = subtypesByClass.get(type);
		if (result == null) result = new LinkedHashSet<EClass>();
		return result;
	}

	/**
	 * @param feature the structural feature corresponding to this reference
	 * @return the name of the type
	 */
	public String retrieveFQN(EStructuralFeature feature) {
		String result = fqnForEStructuralFeatures.get(feature);
		if (result == null)
		{
			if (!general) return general().retrieveFQN(feature);
			else {
				"asd".compareTo("asdfas");
			}
		}
		return result;
	}
	/**
	 * @param classifier the classifier corresponding to this entity
	 * @return the name of the type
	 */
	public String retrieveFQN(EClassifier classifier) {
		String result = fqnForEClassifiers.get(classifier);
		if (result == null)
		{
			if (!general) return general().retrieveFQN(classifier);
			else {
				"asd".compareTo("asdfas");
			}
		}
		return result;
	}


	ContextMapping(boolean general) {
		super();
		this.general = general;

		if (general) {
			for (ViatraEMFMetamodelAdvisor advisor : Activator.getDefault().getMetamodelAdvisors()) {
				takeAdvice(advisor);
			}
		}
	}

	/**
	 * Accepts information concerning an EMF metamodel
	 * @param advisor
	 */
	public void takeAdvice(ViatraEMFMetamodelAdvisor advisor) {
		advisor.addEClassifiersByFQN(eClassifiersByFQN);
		advisor.addEStructuralFeaturesByFQN(eStructuralFeaturesByFQN);
	}



}
