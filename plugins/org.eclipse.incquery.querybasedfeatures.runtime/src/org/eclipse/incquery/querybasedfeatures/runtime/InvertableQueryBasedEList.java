/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.querybasedfeatures.runtime;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * @author Abel Hegedus
 *
 */
public class InvertableQueryBasedEList<ComputedType, StorageType> extends AbstractEList<ComputedType> {
   
  private EList<StorageType> storageEList;
  private EObject sourceObject;
  private IQueryBasedFeatureHandler handler;
  private QueryBasedFeatureInverter<ComputedType, StorageType> inverter;
  
  /**
   * 
   */
  public InvertableQueryBasedEList(EObject sourceObject, EList<StorageType> storageEList,
      IQueryBasedFeatureHandler handler, QueryBasedFeatureInverter<ComputedType, StorageType> inverter) {
    super();
    this.storageEList = storageEList;
    this.sourceObject = sourceObject;
    this.handler = handler;
    this.inverter = inverter;
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#validate(int, java.lang.Object)
   */
  @Override
  protected ComputedType validate(int index, ComputedType object) {
    ComputedType s = super.validate(index, object);
    return inverter.validate(s);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#primitiveGet(int)
   */
  @SuppressWarnings("unchecked")
  @Override
  protected ComputedType primitiveGet(int index) {
    // TODO efficient reversal of index
    StorageType t = storageEList.get(index);
    List<?> values = handler.getManyReferenceValue(sourceObject);
    for (Object object : values) {
      if(inverter.invert((ComputedType) object).equals(t)) {
        return (ComputedType) object;
      }
    }
    return null;
    // NOTE indexing based on source list
    //return (Source) handler.getManyReferenceValue(sourceObject).get(index);
  }


  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#setUnique(int, java.lang.Object)
   */
  @Override
  public ComputedType setUnique(int index, ComputedType object) {
    ComputedType source = get(index);
    StorageType newTarget = inverter.invert(object);
    storageEList.set(index, newTarget);
    return source;
  }


  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#addUnique(java.lang.Object)
   */
  @Override
  public void addUnique(ComputedType object) {
    StorageType newTarget = inverter.invert(object);
    storageEList.add(newTarget);
  }


  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#addUnique(int, java.lang.Object)
   */
  @Override
  public void addUnique(int index, ComputedType object) {
    StorageType newTarget = inverter.invert(object);
    storageEList.add(index, newTarget);
  }


  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#addAllUnique(java.util.Collection)
   */
  @Override
  public boolean addAllUnique(Collection<? extends ComputedType> collection) {
    boolean hasChanged = false;
    for (ComputedType source : collection) {
      StorageType newTarget = inverter.invert(source);
      hasChanged |= storageEList.add(newTarget);
    }
    return hasChanged;
  }


  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#addAllUnique(int, java.util.Collection)
   */
  @Override
  public boolean addAllUnique(int index, Collection<? extends ComputedType> collection) {
    int oldSize = storageEList.size();
    for (ComputedType source : collection) {
      StorageType newTarget = inverter.invert(source);
      storageEList.add(index,newTarget);
      index++;
    }
    return oldSize < storageEList.size();
  }


  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#addAllUnique(java.lang.Object[], int, int)
   */
  @Override
  public boolean addAllUnique(Object[] objects, int start, int end) {
    boolean hasChanged = false;
    for (int i = start; i <= end; i++) {
      @SuppressWarnings("unchecked")
      StorageType newTarget = inverter.invert((ComputedType) objects[i]);
      hasChanged |= storageEList.add(newTarget);
    }
    return hasChanged;
  }


  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#addAllUnique(int, java.lang.Object[], int, int)
   */
  @Override
  public boolean addAllUnique(int index, Object[] objects, int start, int end) {
    int oldSize = storageEList.size();
    for (int i = start; i <= end; i++) {
      @SuppressWarnings("unchecked")
      StorageType newTarget = inverter.invert((ComputedType) objects[i]);
      storageEList.add(index,newTarget);
      index++;
    }
    return oldSize < storageEList.size();
  }


  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#remove(int)
   */
  @Override
  public ComputedType remove(int index) {
    ComputedType source = get(index);
    StorageType target = inverter.invert(source);
    storageEList.remove(target);
    return source;
  }


  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#move(int, int)
   */
  @Override
  public ComputedType move(int targetIndex, int sourceIndex) {
    ComputedType t_source = get(sourceIndex);
    StorageType t_target = inverter.invert(t_source);
    storageEList.move(targetIndex, t_target);
    return t_source;
  }


  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#basicList()
   */
  @SuppressWarnings("unchecked")
  @Override
  protected List<ComputedType> basicList() {
     return (List<ComputedType>) handler.getManyReferenceValue(sourceObject);
  }


  /* (non-Javadoc)
   * @see java.util.AbstractList#get(int)
   */
  @Override
  public ComputedType get(int index) {
    return basicGet(index);
  }


  /* (non-Javadoc)
   * @see java.util.AbstractCollection#size()
   */
  @Override
  public int size() {
    return handler.getManyReferenceValue(sourceObject).size();
  }
  
}
