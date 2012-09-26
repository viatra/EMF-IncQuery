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
package org.eclipse.viatra2.emf.incquery.runtime.derived;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * @author Abel Hegedus
 *
 */
public class InvertableDerivedFeatureEList<Source, Target> extends AbstractEList<Source> {
   
  private EList<Target> targetEList;
  private EObject sourceObject;
  private IncqueryDerivedFeature handler;
  private DerivedFeatureInverter<Source, Target> inverter;
  
  public interface DerivedFeatureInverter<Source, Target>{
    Target invert(Source source);
    Source validate(Source source);
  }
  
  /**
   * 
   */
  public InvertableDerivedFeatureEList(EObject sourceObject, EList<Target> targetEList,
      IncqueryDerivedFeature handler, DerivedFeatureInverter<Source, Target> inverter) {
    super();
    this.targetEList = targetEList;
    this.sourceObject = sourceObject;
    this.handler = handler;
    this.inverter = inverter;
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#validate(int, java.lang.Object)
   */
  @Override
  protected Source validate(int index, Source object) {
    Source s = super.validate(index, object);
    return inverter.validate(s);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#primitiveGet(int)
   */
  @SuppressWarnings("unchecked")
  @Override
  protected Source primitiveGet(int index) {
    // TODO efficient reversal of index
    Target t = targetEList.get(index);
    List<?> values = handler.getManyReferenceValue(sourceObject);
    for (Object object : values) {
      if(inverter.invert((Source) object).equals(t)) {
        return (Source) object;
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
  public Source setUnique(int index, Source object) {
    Source source = get(index);
    Target newTarget = inverter.invert(object);
    targetEList.set(index, newTarget);
    return source;
  }


  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#addUnique(java.lang.Object)
   */
  @Override
  public void addUnique(Source object) {
    Target newTarget = inverter.invert(object);
    targetEList.add(newTarget);
  }


  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#addUnique(int, java.lang.Object)
   */
  @Override
  public void addUnique(int index, Source object) {
    Target newTarget = inverter.invert(object);
    targetEList.add(index, newTarget);
  }


  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#addAllUnique(java.util.Collection)
   */
  @Override
  public boolean addAllUnique(Collection<? extends Source> collection) {
    boolean hasChanged = false;
    for (Source source : collection) {
      Target newTarget = inverter.invert(source);
      hasChanged |= targetEList.add(newTarget);
    }
    return hasChanged;
  }


  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#addAllUnique(int, java.util.Collection)
   */
  @Override
  public boolean addAllUnique(int index, Collection<? extends Source> collection) {
    int oldSize = targetEList.size();
    for (Source source : collection) {
      Target newTarget = inverter.invert(source);
      targetEList.add(index,newTarget);
      index++;
    }
    return oldSize < targetEList.size();
  }


  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#addAllUnique(java.lang.Object[], int, int)
   */
  @Override
  public boolean addAllUnique(Object[] objects, int start, int end) {
    boolean hasChanged = false;
    for (int i = start; i <= end; i++) {
      @SuppressWarnings("unchecked")
      Target newTarget = inverter.invert((Source) objects[i]);
      hasChanged |= targetEList.add(newTarget);
    }
    return hasChanged;
  }


  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#addAllUnique(int, java.lang.Object[], int, int)
   */
  @Override
  public boolean addAllUnique(int index, Object[] objects, int start, int end) {
    int oldSize = targetEList.size();
    for (int i = start; i <= end; i++) {
      @SuppressWarnings("unchecked")
      Target newTarget = inverter.invert((Source) objects[i]);
      targetEList.add(index,newTarget);
      index++;
    }
    return oldSize < targetEList.size();
  }


  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#remove(int)
   */
  @Override
  public Source remove(int index) {
    Source source = get(index);
    Target target = inverter.invert(source);
    targetEList.remove(target);
    return source;
  }


  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#move(int, int)
   */
  @Override
  public Source move(int targetIndex, int sourceIndex) {
    Source t_source = get(sourceIndex);
    Target t_target = inverter.invert(t_source);
    targetEList.move(targetIndex, t_target);
    return t_source;
  }


  /* (non-Javadoc)
   * @see org.eclipse.emf.common.util.AbstractEList#basicList()
   */
  @SuppressWarnings("unchecked")
  @Override
  protected List<Source> basicList() {
     return (List<Source>) handler.getManyReferenceValue(sourceObject);
  }


  /* (non-Javadoc)
   * @see java.util.AbstractList#get(int)
   */
  @Override
  public Source get(int index) {
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
