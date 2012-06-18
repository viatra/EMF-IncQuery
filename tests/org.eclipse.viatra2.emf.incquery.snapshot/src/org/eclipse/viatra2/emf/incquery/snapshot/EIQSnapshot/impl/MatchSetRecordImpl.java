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
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Match Set Record</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.impl.MatchSetRecordImpl#getPatternQualifiedName <em>Pattern Qualified Name</em>}</li>
 *   <li>{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.impl.MatchSetRecordImpl#getMatches <em>Matches</em>}</li>
 *   <li>{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.impl.MatchSetRecordImpl#getFilter <em>Filter</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MatchSetRecordImpl extends EObjectImpl implements MatchSetRecord {
	/**
	 * The default value of the '{@link #getPatternQualifiedName() <em>Pattern Qualified Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPatternQualifiedName()
	 * @generated
	 * @ordered
	 */
	protected static final String PATTERN_QUALIFIED_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPatternQualifiedName() <em>Pattern Qualified Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPatternQualifiedName()
	 * @generated
	 * @ordered
	 */
	protected String patternQualifiedName = PATTERN_QUALIFIED_NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getMatches() <em>Matches</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMatches()
	 * @generated
	 * @ordered
	 */
	protected EList<MatchRecord> matches;

	/**
	 * The cached value of the '{@link #getFilter() <em>Filter</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFilter()
	 * @generated
	 * @ordered
	 */
	protected MatchRecord filter;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MatchSetRecordImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EIQSnapshotPackage.Literals.MATCH_SET_RECORD;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPatternQualifiedName() {
		return patternQualifiedName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPatternQualifiedName(String newPatternQualifiedName) {
		String oldPatternQualifiedName = patternQualifiedName;
		patternQualifiedName = newPatternQualifiedName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EIQSnapshotPackage.MATCH_SET_RECORD__PATTERN_QUALIFIED_NAME, oldPatternQualifiedName, patternQualifiedName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<MatchRecord> getMatches() {
		if (matches == null) {
			matches = new EObjectContainmentEList<MatchRecord>(MatchRecord.class, this, EIQSnapshotPackage.MATCH_SET_RECORD__MATCHES);
		}
		return matches;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MatchRecord getFilter() {
		return filter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetFilter(MatchRecord newFilter, NotificationChain msgs) {
		MatchRecord oldFilter = filter;
		filter = newFilter;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EIQSnapshotPackage.MATCH_SET_RECORD__FILTER, oldFilter, newFilter);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFilter(MatchRecord newFilter) {
		if (newFilter != filter) {
			NotificationChain msgs = null;
			if (filter != null)
				msgs = ((InternalEObject)filter).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EIQSnapshotPackage.MATCH_SET_RECORD__FILTER, null, msgs);
			if (newFilter != null)
				msgs = ((InternalEObject)newFilter).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EIQSnapshotPackage.MATCH_SET_RECORD__FILTER, null, msgs);
			msgs = basicSetFilter(newFilter, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EIQSnapshotPackage.MATCH_SET_RECORD__FILTER, newFilter, newFilter));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EIQSnapshotPackage.MATCH_SET_RECORD__MATCHES:
				return ((InternalEList<?>)getMatches()).basicRemove(otherEnd, msgs);
			case EIQSnapshotPackage.MATCH_SET_RECORD__FILTER:
				return basicSetFilter(null, msgs);
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
			case EIQSnapshotPackage.MATCH_SET_RECORD__PATTERN_QUALIFIED_NAME:
				return getPatternQualifiedName();
			case EIQSnapshotPackage.MATCH_SET_RECORD__MATCHES:
				return getMatches();
			case EIQSnapshotPackage.MATCH_SET_RECORD__FILTER:
				return getFilter();
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
			case EIQSnapshotPackage.MATCH_SET_RECORD__PATTERN_QUALIFIED_NAME:
				setPatternQualifiedName((String)newValue);
				return;
			case EIQSnapshotPackage.MATCH_SET_RECORD__MATCHES:
				getMatches().clear();
				getMatches().addAll((Collection<? extends MatchRecord>)newValue);
				return;
			case EIQSnapshotPackage.MATCH_SET_RECORD__FILTER:
				setFilter((MatchRecord)newValue);
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
			case EIQSnapshotPackage.MATCH_SET_RECORD__PATTERN_QUALIFIED_NAME:
				setPatternQualifiedName(PATTERN_QUALIFIED_NAME_EDEFAULT);
				return;
			case EIQSnapshotPackage.MATCH_SET_RECORD__MATCHES:
				getMatches().clear();
				return;
			case EIQSnapshotPackage.MATCH_SET_RECORD__FILTER:
				setFilter((MatchRecord)null);
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
			case EIQSnapshotPackage.MATCH_SET_RECORD__PATTERN_QUALIFIED_NAME:
				return PATTERN_QUALIFIED_NAME_EDEFAULT == null ? patternQualifiedName != null : !PATTERN_QUALIFIED_NAME_EDEFAULT.equals(patternQualifiedName);
			case EIQSnapshotPackage.MATCH_SET_RECORD__MATCHES:
				return matches != null && !matches.isEmpty();
			case EIQSnapshotPackage.MATCH_SET_RECORD__FILTER:
				return filter != null;
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
		result.append(" (patternQualifiedName: ");
		result.append(patternQualifiedName);
		result.append(')');
		return result.toString();
	}

} //MatchSetRecordImpl
