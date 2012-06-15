/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHandler;
import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHandler.FeatureKind;
import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHelper;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage;
import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSubstitutionRecord;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Match Substitution Record</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.impl.MatchSubstitutionRecordImpl#getParameterName <em>Parameter Name</em>}</li>
 *   <li>{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.impl.MatchSubstitutionRecordImpl#getDerivedValue <em>Derived Value</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class MatchSubstitutionRecordImpl extends EObjectImpl implements MatchSubstitutionRecord {
	/**
	 * The default value of the '{@link #getParameterName() <em>Parameter Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameterName()
	 * @generated
	 * @ordered
	 */
	protected static final String PARAMETER_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getParameterName() <em>Parameter Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameterName()
	 * @generated
	 * @ordered
	 */
	protected String parameterName = PARAMETER_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getDerivedValue() <em>Derived Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDerivedValue()
	 * @generated
	 * @ordered
	 */
	protected static final Object DERIVED_VALUE_EDEFAULT = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MatchSubstitutionRecordImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EIQSnapshotPackage.Literals.MATCH_SUBSTITUTION_RECORD;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getParameterName() {
		return parameterName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParameterName(String newParameterName) {
		String oldParameterName = parameterName;
		parameterName = newParameterName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EIQSnapshotPackage.MATCH_SUBSTITUTION_RECORD__PARAMETER_NAME, oldParameterName, parameterName));
	}

	private IncqueryFeatureHandler valueHandler;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public Object getDerivedValue() {
		if (valueHandler != null) {
		  return valueHandler.getSingleReferenceValue();
		 } else {
		  valueHandler = IncqueryFeatureHelper.createHandler(this,
		   EIQSnapshotPackage.Literals.MATCH_SUBSTITUTION_RECORD__DERIVED_VALUE,
		   "org.eclipse.viatra2.emf.incquery.testing.queries.SubstitutionValue",
		   "Substitution", "Value", FeatureKind.SINGLE_REFERENCE);
		  if(valueHandler != null) {
		   return valueHandler.getSingleReferenceValue();
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
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case EIQSnapshotPackage.MATCH_SUBSTITUTION_RECORD__PARAMETER_NAME:
				return getParameterName();
			case EIQSnapshotPackage.MATCH_SUBSTITUTION_RECORD__DERIVED_VALUE:
				return getDerivedValue();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case EIQSnapshotPackage.MATCH_SUBSTITUTION_RECORD__PARAMETER_NAME:
				setParameterName((String)newValue);
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
			case EIQSnapshotPackage.MATCH_SUBSTITUTION_RECORD__PARAMETER_NAME:
				setParameterName(PARAMETER_NAME_EDEFAULT);
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
			case EIQSnapshotPackage.MATCH_SUBSTITUTION_RECORD__PARAMETER_NAME:
				return PARAMETER_NAME_EDEFAULT == null ? parameterName != null : !PARAMETER_NAME_EDEFAULT.equals(parameterName);
			case EIQSnapshotPackage.MATCH_SUBSTITUTION_RECORD__DERIVED_VALUE:
				return DERIVED_VALUE_EDEFAULT == null ? getDerivedValue() != null : !DERIVED_VALUE_EDEFAULT.equals(getDerivedValue());
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
		result.append(" (parameterName: ");
		result.append(parameterName);
		result.append(')');
		return result.toString();
	}

} //MatchSubstitutionRecordImpl
