/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage
 * @generated
 */
public class EIQSnapshotSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static EIQSnapshotPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EIQSnapshotSwitch() {
		if (modelPackage == null) {
			modelPackage = EIQSnapshotPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @parameter ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case EIQSnapshotPackage.MATCH_SET_RECORD: {
				MatchSetRecord matchSetRecord = (MatchSetRecord)theEObject;
				T result = caseMatchSetRecord(matchSetRecord);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EIQSnapshotPackage.MATCH_RECORD: {
				MatchRecord matchRecord = (MatchRecord)theEObject;
				T result = caseMatchRecord(matchRecord);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EIQSnapshotPackage.MATCH_SUBSTITUTION_RECORD: {
				MatchSubstitutionRecord matchSubstitutionRecord = (MatchSubstitutionRecord)theEObject;
				T result = caseMatchSubstitutionRecord(matchSubstitutionRecord);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EIQSnapshotPackage.EMF_SUBSTITUTION: {
				EMFSubstitution emfSubstitution = (EMFSubstitution)theEObject;
				T result = caseEMFSubstitution(emfSubstitution);
				if (result == null) result = caseMatchSubstitutionRecord(emfSubstitution);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EIQSnapshotPackage.INT_SUBSTITUTION: {
				IntSubstitution intSubstitution = (IntSubstitution)theEObject;
				T result = caseIntSubstitution(intSubstitution);
				if (result == null) result = caseMatchSubstitutionRecord(intSubstitution);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EIQSnapshotPackage.LONG_SUBSTITUTION: {
				LongSubstitution longSubstitution = (LongSubstitution)theEObject;
				T result = caseLongSubstitution(longSubstitution);
				if (result == null) result = caseMatchSubstitutionRecord(longSubstitution);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EIQSnapshotPackage.DOUBLE_SUBSTITUTION: {
				DoubleSubstitution doubleSubstitution = (DoubleSubstitution)theEObject;
				T result = caseDoubleSubstitution(doubleSubstitution);
				if (result == null) result = caseMatchSubstitutionRecord(doubleSubstitution);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EIQSnapshotPackage.FLOAT_SUBSTITUTION: {
				FloatSubstitution floatSubstitution = (FloatSubstitution)theEObject;
				T result = caseFloatSubstitution(floatSubstitution);
				if (result == null) result = caseMatchSubstitutionRecord(floatSubstitution);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EIQSnapshotPackage.BOOLEAN_SUBSTITUTION: {
				BooleanSubstitution booleanSubstitution = (BooleanSubstitution)theEObject;
				T result = caseBooleanSubstitution(booleanSubstitution);
				if (result == null) result = caseMatchSubstitutionRecord(booleanSubstitution);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EIQSnapshotPackage.STRING_SUBSTITUTION: {
				StringSubstitution stringSubstitution = (StringSubstitution)theEObject;
				T result = caseStringSubstitution(stringSubstitution);
				if (result == null) result = caseMatchSubstitutionRecord(stringSubstitution);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EIQSnapshotPackage.DATE_SUBSTITUTION: {
				DateSubstitution dateSubstitution = (DateSubstitution)theEObject;
				T result = caseDateSubstitution(dateSubstitution);
				if (result == null) result = caseMatchSubstitutionRecord(dateSubstitution);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EIQSnapshotPackage.ENUM_SUBSTITUTION: {
				EnumSubstitution enumSubstitution = (EnumSubstitution)theEObject;
				T result = caseEnumSubstitution(enumSubstitution);
				if (result == null) result = caseMatchSubstitutionRecord(enumSubstitution);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EIQSnapshotPackage.MISCELLANEOUS_SUBSTITUTION: {
				MiscellaneousSubstitution miscellaneousSubstitution = (MiscellaneousSubstitution)theEObject;
				T result = caseMiscellaneousSubstitution(miscellaneousSubstitution);
				if (result == null) result = caseMatchSubstitutionRecord(miscellaneousSubstitution);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Match Set Record</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Match Set Record</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMatchSetRecord(MatchSetRecord object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Match Record</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Match Record</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMatchRecord(MatchRecord object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Match Substitution Record</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Match Substitution Record</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMatchSubstitutionRecord(MatchSubstitutionRecord object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EMF Substitution</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EMF Substitution</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEMFSubstitution(EMFSubstitution object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Int Substitution</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Int Substitution</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIntSubstitution(IntSubstitution object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Long Substitution</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Long Substitution</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseLongSubstitution(LongSubstitution object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Double Substitution</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Double Substitution</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDoubleSubstitution(DoubleSubstitution object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Float Substitution</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Float Substitution</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFloatSubstitution(FloatSubstitution object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Boolean Substitution</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Boolean Substitution</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBooleanSubstitution(BooleanSubstitution object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>String Substitution</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>String Substitution</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStringSubstitution(StringSubstitution object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Date Substitution</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Date Substitution</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDateSubstitution(DateSubstitution object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Enum Substitution</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Enum Substitution</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEnumSubstitution(EnumSubstitution object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Miscellaneous Substitution</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Miscellaneous Substitution</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMiscellaneousSubstitution(MiscellaneousSubstitution object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

} //EIQSnapshotSwitch
