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
package org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.IncQuerySnapshot;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.InputSpecification;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Inc Query Snapshot</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.impl.IncQuerySnapshotImpl#getMatchSetRecords <em>Match Set Records</em>}</li>
 *   <li>{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.impl.IncQuerySnapshotImpl#getModelRoots <em>Model Roots</em>}</li>
 *   <li>{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.impl.IncQuerySnapshotImpl#getInputSpecification <em>Input Specification</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class IncQuerySnapshotImpl extends EObjectImpl implements IncQuerySnapshot {
	/**
	 * The cached value of the '{@link #getMatchSetRecords() <em>Match Set Records</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMatchSetRecords()
	 * @generated
	 * @ordered
	 */
	protected EList<MatchSetRecord> matchSetRecords;

	/**
	 * The cached value of the '{@link #getModelRoots() <em>Model Roots</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModelRoots()
	 * @generated
	 * @ordered
	 */
	protected EList<EObject> modelRoots;

	/**
	 * The default value of the '{@link #getInputSpecification() <em>Input Specification</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInputSpecification()
	 * @generated
	 * @ordered
	 */
	protected static final InputSpecification INPUT_SPECIFICATION_EDEFAULT = InputSpecification.UNSET;

	/**
	 * The cached value of the '{@link #getInputSpecification() <em>Input Specification</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInputSpecification()
	 * @generated
	 * @ordered
	 */
	protected InputSpecification inputSpecification = INPUT_SPECIFICATION_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IncQuerySnapshotImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EIQSnapshotPackage.Literals.INC_QUERY_SNAPSHOT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<MatchSetRecord> getMatchSetRecords() {
		if (matchSetRecords == null) {
			matchSetRecords = new EObjectContainmentEList<MatchSetRecord>(MatchSetRecord.class, this, EIQSnapshotPackage.INC_QUERY_SNAPSHOT__MATCH_SET_RECORDS);
		}
		return matchSetRecords;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EObject> getModelRoots() {
		if (modelRoots == null) {
			modelRoots = new EObjectResolvingEList<EObject>(EObject.class, this, EIQSnapshotPackage.INC_QUERY_SNAPSHOT__MODEL_ROOTS);
		}
		return modelRoots;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InputSpecification getInputSpecification() {
		return inputSpecification;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInputSpecification(InputSpecification newInputSpecification) {
		InputSpecification oldInputSpecification = inputSpecification;
		inputSpecification = newInputSpecification == null ? INPUT_SPECIFICATION_EDEFAULT : newInputSpecification;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EIQSnapshotPackage.INC_QUERY_SNAPSHOT__INPUT_SPECIFICATION, oldInputSpecification, inputSpecification));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EIQSnapshotPackage.INC_QUERY_SNAPSHOT__MATCH_SET_RECORDS:
				return ((InternalEList<?>)getMatchSetRecords()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case EIQSnapshotPackage.INC_QUERY_SNAPSHOT__MATCH_SET_RECORDS:
				return getMatchSetRecords();
			case EIQSnapshotPackage.INC_QUERY_SNAPSHOT__MODEL_ROOTS:
				return getModelRoots();
			case EIQSnapshotPackage.INC_QUERY_SNAPSHOT__INPUT_SPECIFICATION:
				return getInputSpecification();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case EIQSnapshotPackage.INC_QUERY_SNAPSHOT__MATCH_SET_RECORDS:
				getMatchSetRecords().clear();
				getMatchSetRecords().addAll((Collection<? extends MatchSetRecord>)newValue);
				return;
			case EIQSnapshotPackage.INC_QUERY_SNAPSHOT__MODEL_ROOTS:
				getModelRoots().clear();
				getModelRoots().addAll((Collection<? extends EObject>)newValue);
				return;
			case EIQSnapshotPackage.INC_QUERY_SNAPSHOT__INPUT_SPECIFICATION:
				setInputSpecification((InputSpecification)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case EIQSnapshotPackage.INC_QUERY_SNAPSHOT__MATCH_SET_RECORDS:
				getMatchSetRecords().clear();
				return;
			case EIQSnapshotPackage.INC_QUERY_SNAPSHOT__MODEL_ROOTS:
				getModelRoots().clear();
				return;
			case EIQSnapshotPackage.INC_QUERY_SNAPSHOT__INPUT_SPECIFICATION:
				setInputSpecification(INPUT_SPECIFICATION_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case EIQSnapshotPackage.INC_QUERY_SNAPSHOT__MATCH_SET_RECORDS:
				return matchSetRecords != null && !matchSetRecords.isEmpty();
			case EIQSnapshotPackage.INC_QUERY_SNAPSHOT__MODEL_ROOTS:
				return modelRoots != null && !modelRoots.isEmpty();
			case EIQSnapshotPackage.INC_QUERY_SNAPSHOT__INPUT_SPECIFICATION:
				return inputSpecification != INPUT_SPECIFICATION_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (inputSpecification: ");
		result.append(inputSpecification);
		result.append(')');
		return result.toString();
	}

} //IncQuerySnapshotImpl
