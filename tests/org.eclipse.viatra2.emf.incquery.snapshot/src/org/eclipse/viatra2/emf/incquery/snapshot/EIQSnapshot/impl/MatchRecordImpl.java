/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHandler;
import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHandler.FeatureKind;
import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHelper;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSubstitutionRecord;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.RecordRole;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Match Record</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.impl.MatchRecordImpl#getSubstitutions <em>Substitutions</em>}</li>
 *   <li>{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.impl.MatchRecordImpl#getRole <em>Role</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MatchRecordImpl extends EObjectImpl implements MatchRecord {
	/**
	 * The cached value of the '{@link #getSubstitutions() <em>Substitutions</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubstitutions()
	 * @generated
	 * @ordered
	 */
	protected EList<MatchSubstitutionRecord> substitutions;

	/**
	 * The default value of the '{@link #getRole() <em>Role</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRole()
	 * @generated
	 * @ordered
	 */
	protected static final RecordRole ROLE_EDEFAULT = RecordRole.MATCH;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MatchRecordImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EIQSnapshotPackage.Literals.MATCH_RECORD;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<MatchSubstitutionRecord> getSubstitutions() {
		if (substitutions == null) {
			substitutions = new EObjectContainmentEList<MatchSubstitutionRecord>(MatchSubstitutionRecord.class, this, EIQSnapshotPackage.MATCH_RECORD__SUBSTITUTIONS);
		}
		return substitutions;
	}

	private IncqueryFeatureHandler roleHandler;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public RecordRole getRole() {
		if (roleHandler != null) {
		  return (RecordRole) roleHandler.getSingleReferenceValue();
		 } else {
			 roleHandler = IncqueryFeatureHelper.createHandler(this,
		   EIQSnapshotPackage.Literals.MATCH_RECORD__ROLE,
		   "org.eclipse.viatra2.emf.incquery.testing.queries.RecordRoleValue",
		   "Record", "Role", FeatureKind.SINGLE_REFERENCE);
		  if(roleHandler != null) {
		   return (RecordRole) roleHandler.getSingleReferenceValue();
		  }
		 }
		 return null;
	}


	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EIQSnapshotPackage.MATCH_RECORD__SUBSTITUTIONS:
				return ((InternalEList<?>)getSubstitutions()).basicRemove(otherEnd, msgs);
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
			case EIQSnapshotPackage.MATCH_RECORD__SUBSTITUTIONS:
				return getSubstitutions();
			case EIQSnapshotPackage.MATCH_RECORD__ROLE:
				return getRole();
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
			case EIQSnapshotPackage.MATCH_RECORD__SUBSTITUTIONS:
				getSubstitutions().clear();
				getSubstitutions().addAll((Collection<? extends MatchSubstitutionRecord>)newValue);
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
			case EIQSnapshotPackage.MATCH_RECORD__SUBSTITUTIONS:
				getSubstitutions().clear();
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
			case EIQSnapshotPackage.MATCH_RECORD__SUBSTITUTIONS:
				return substitutions != null && !substitutions.isEmpty();
			case EIQSnapshotPackage.MATCH_RECORD__ROLE:
				return getRole() != ROLE_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

} //MatchRecordImpl
