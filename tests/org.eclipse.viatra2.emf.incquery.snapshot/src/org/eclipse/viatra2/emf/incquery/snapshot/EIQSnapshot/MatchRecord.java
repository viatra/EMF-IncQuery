/*******************************************************************************
 * Copyright (c) 2010-2012, Gabor Bergmann, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann, Abel Hegedus - initial API and implementation
 *******************************************************************************/

/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Match Record</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord#getSubstitutions <em>Substitutions</em>}</li>
 *   <li>{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord#getRole <em>Role</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage#getMatchRecord()
 * @model
 * @generated
 */
public interface MatchRecord extends EObject {
	/**
	 * Returns the value of the '<em><b>Substitutions</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSubstitutionRecord}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Substitutions</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Substitutions</em>' containment reference list.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage#getMatchRecord_Substitutions()
	 * @model containment="true"
	 * @generated
	 */
	EList<MatchSubstitutionRecord> getSubstitutions();

	/**
	 * Returns the value of the '<em><b>Role</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.RecordRole}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Role</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Role</em>' attribute.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.RecordRole
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage#getMatchRecord_Role()
	 * @model transient="true" changeable="false" volatile="true" derived="true"
	 * @generated
	 */
	RecordRole getRole();

} // MatchRecord
