/*******************************************************************************
 * Copyright (c) 2010-2012, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.base.api;

import java.util.Collection;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * 
 * The interface exposes useful functionalities for EMF models:
 * <br>
 * - Inverse navigation along arbitrary {@link EReference} instances (heterogenous paths too) 
 * <br>
 * - Finding model elements by attribute value (i.e. inverse navigation along  {@link EAttribute}) 
 * <br>
 * - Getting all the (direct) instances of a given {@link EClass} 
 * <br>
 * - Querying instances of given data types
 * <br>
 * These information can also be observed by registering listeners on the navigation helper.
 * <br><br>
 * Note that the helper will maintain these information incrementally 
 * on a given node's subgraph in the model ({@link EObject}, {@link Resource} or {@link ResourceSet}). <br>
 * Note that none of the defined methods return null upon empty result sets.
 * <br><br>
 * All query methods return either a copy of the result sets (where {@link Setting} is instantiated) or an unmodifiable collection of the result view.
 * 
 * @author Tamas Szabo
 *
 */
public interface NavigationHelper {
	
	/**
	 * Find all {@link EAttribute} and their owners for a given <code>value</code> of the attribute.
	 * The method will return these information as a collection of {@link EStructuralFeature.Setting}.
	 * 
	 * @param value the value of the attribute
	 * @return the collection of settings
	 */
	public Collection<Setting> findByAttributeValue(Object value);
	
	/**
	 * Find all the EAttributes and their owners for a given <code>value</code> of the attribute.
	 * The method will return these information as a collection of {@link EStructuralFeature.Setting}.
	 * Note that a setting will be present in the returned collection only if 
	 * its attribute instance can be found in the given collection of <code>attributes</code>.
	 * 
	 * @param value the value of the attribute
	 * @param attributes the collection of attributes
	 * @return the collection of settings
	 */
	public Collection<Setting> findByAttributeValue(Object value, Collection<EAttribute> attributes);
	
	/**
	 * Find all {@link EObject}s that have an <code>attribute</code> {@link EAttribute} and its value equals to the given <code>value</code>. 
	 * 
	 * @param value the value of the attribute
	 * @param attribute the EAttribute instance
	 * @return the collection of {@link EObject} instances
	 */
	public Collection<EObject> findByAttributeValue(Object value, EAttribute attribute);
	
	/**
	 * Returns the collection of data type instances for the given {@link EDataType} instance.
	 * 
	 * @param type the data type 
	 * @return the collection of data type instances
	 */
	public Collection<Object> getDataTypeInstances(EDataType type);
	
//	/**
//	 * Find all the EAttributes and their owners which have a value of a class that equals to the given one.
//	 * The method will return these information as a collection of {@link EStructuralFeature.Setting}. 
//	 * 
//	 * @param clazz the class of the value
//	 * @return the collection of settings
//	 */
//	public Collection<Setting> findAllAttributeValuesByType(Class<?> clazz);
	
	/**
	 * Find all the {@link EObject} instances that have an {@link EReference} instance with the given <code>target</code>.
	 * The method will return these information as a collection of {@link EStructuralFeature.Setting}. 
	 * 
	 * @param target the endpoint of a reference
	 * @return the collection of settings
	 */
	public Collection<Setting> getInverseReferences(EObject target);
	
	/**
	 * Find all the {@link EObject} instances that have an EReference instance with the given <code>target</code>.
	 * The method will return these information as a collection of {@link EStructuralFeature.Setting}.
	 * Note that a setting will be present in the returned collection only if 
	 * its reference instance can be found in the given collection of <code>references</code>.
	 * 
	 * @param target
	 * @param references
	 * @return
	 */
	public Collection<Setting> getInverseReferences(EObject target, Collection<EReference> references);
	
	/**
	 * Find all {@link EObject}s that have a <code>reference</code> EReference instance with the given <code>target</code>. 
	 * 
	 * @param target the endpoint of a reference
	 * @param reference the EReference instance
	 * @return the collection of {@link EObject} instances
	 */
	public Collection<EObject> getInverseReferences(EObject target, EReference reference);
	
	/**
	 * Get the direct {@link EObject} instances of the given EClass instance.
	 * @param clazz the EClass instance
	 * @return the collection of {@link EObject} instances
	 */
	public Collection<EObject> getDirectInstances(EClass clazz);
	
	/**
	 * Get the exact and descendant {@link EObject} instances of the given EClass instance. 
	 * 
	 * @param clazz the EClass instance 
	 * @return the collection of {@link EObject} instances
	 */
	public Collection<EObject> getAllInstances(EClass clazz);
	
	/**
	 * Returns the collection of {@link EObject} instances which have a feature with the given value.
	 * 
	 * @param value the value of the feature
	 * @param feature the feature instance
	 * @return the collection of {@link EObject} instances
	 */
	public Collection<EObject> findByFeatureValue(Object value, EStructuralFeature feature);
	
	/**
	 * Returns the holder(s) of the given feature.
	 * 
	 * @param feature the feature instance
	 * @return the collection of {@link EObject} instances
	 */
	public Collection<EObject> getHoldersOfFeature(EStructuralFeature feature);
	
	/**
	 * Call this method to dispose the NavigationHelper instance.
	 * The NavigationHelper instance will unregister itself from the list of EContentAdapters of the given Notifier instance.
	 */
	public void dispose();
	
	/**
	 * Registers an instance listener for the navigation helper. 
	 * The listener will be notified about only the instances of the given classes.  
	 * 
	 * @param classes the collection of classes associated to the listener 
	 * @param listener the listener instance
	 */
	public void registerInstanceListener(Collection<EClass> classes, InstanceListener listener);
	
	/**
	 * Unregisters an instance listener for the given classes.
	 * 
	 * @param classes the collection of classes
	 * @param listener the listener instance
	 */
	public void unregisterInstanceListener(Collection<EClass> classes, InstanceListener listener);
	
	/**
	 * Registers a data type listener for the navigation helper. 
	 * The listener will be notified about only the data type instances of the given types.  
	 * 
	 * @param types the collection of types associated to the listener 
	 * @param listener the data type instance
	 */
	public void registerDataTypeListener(Collection<EDataType> types, DataTypeListener listener);
	
	/**
	 * Unregisters a data type listener for the given types.
	 * 
	 * @param types the collection of data types
	 * @param listener the listener instance
	 */
	public void unregisterDataTypeListener(Collection<EDataType> types, DataTypeListener listener);
	
	/**
	 * Registers a feature listener for the navigation helper. 
	 * The listener will be notified about only the settings associated to the given features. 
	 * 
	 * @param features the collection of features associated to the listener
	 * @param listener the listener instance
	 */
	public void registerFeatureListener(Collection<EStructuralFeature> features, FeatureListener listener);
	
	/**
	 * Unregisters a feature listener for the given features. 
	 * 
	 * @param listener the listener instance
	 * @param features the collection of features
	 */
	public void unregisterFeatureListener(Collection<EStructuralFeature> features, FeatureListener listener);
	

}
