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

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage
 * @generated
 */
public interface EIQSnapshotFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	EIQSnapshotFactory eINSTANCE = org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.impl.EIQSnapshotFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Match Set Record</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Match Set Record</em>'.
	 * @generated
	 */
	MatchSetRecord createMatchSetRecord();

	/**
	 * Returns a new object of class '<em>Match Record</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Match Record</em>'.
	 * @generated
	 */
	MatchRecord createMatchRecord();

	/**
	 * Returns a new object of class '<em>EMF Substitution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>EMF Substitution</em>'.
	 * @generated
	 */
	EMFSubstitution createEMFSubstitution();

	/**
	 * Returns a new object of class '<em>Int Substitution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Int Substitution</em>'.
	 * @generated
	 */
	IntSubstitution createIntSubstitution();

	/**
	 * Returns a new object of class '<em>Long Substitution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Long Substitution</em>'.
	 * @generated
	 */
	LongSubstitution createLongSubstitution();

	/**
	 * Returns a new object of class '<em>Double Substitution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Double Substitution</em>'.
	 * @generated
	 */
	DoubleSubstitution createDoubleSubstitution();

	/**
	 * Returns a new object of class '<em>Float Substitution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Float Substitution</em>'.
	 * @generated
	 */
	FloatSubstitution createFloatSubstitution();

	/**
	 * Returns a new object of class '<em>Boolean Substitution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Boolean Substitution</em>'.
	 * @generated
	 */
	BooleanSubstitution createBooleanSubstitution();

	/**
	 * Returns a new object of class '<em>String Substitution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>String Substitution</em>'.
	 * @generated
	 */
	StringSubstitution createStringSubstitution();

	/**
	 * Returns a new object of class '<em>Date Substitution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Date Substitution</em>'.
	 * @generated
	 */
	DateSubstitution createDateSubstitution();

	/**
	 * Returns a new object of class '<em>Enum Substitution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Enum Substitution</em>'.
	 * @generated
	 */
	EnumSubstitution createEnumSubstitution();

	/**
	 * Returns a new object of class '<em>Miscellaneous Substitution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Miscellaneous Substitution</em>'.
	 * @generated
	 */
	MiscellaneousSubstitution createMiscellaneousSubstitution();

	/**
	 * Returns a new object of class '<em>Inc Query Snapshot</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Inc Query Snapshot</em>'.
	 * @generated
	 */
	IncQuerySnapshot createIncQuerySnapshot();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	EIQSnapshotPackage getEIQSnapshotPackage();

} //EIQSnapshotFactory
