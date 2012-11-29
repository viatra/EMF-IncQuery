/**
 */
package org.eclipse.incquery.snapshot.EIQSnapshot;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Match Substitution Record</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.incquery.snapshot.EIQSnapshot.MatchSubstitutionRecord#getParameterName <em>Parameter Name</em>}</li>
 *   <li>{@link org.eclipse.incquery.snapshot.EIQSnapshot.MatchSubstitutionRecord#getDerivedValue <em>Derived Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage#getMatchSubstitutionRecord()
 * @model abstract="true"
 * @generated
 */
public interface MatchSubstitutionRecord extends EObject {
	/**
	 * Returns the value of the '<em><b>Parameter Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameter Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameter Name</em>' attribute.
	 * @see #setParameterName(String)
	 * @see org.eclipse.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage#getMatchSubstitutionRecord_ParameterName()
	 * @model
	 * @generated
	 */
	String getParameterName();

	/**
	 * Sets the value of the '{@link org.eclipse.incquery.snapshot.EIQSnapshot.MatchSubstitutionRecord#getParameterName <em>Parameter Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parameter Name</em>' attribute.
	 * @see #getParameterName()
	 * @generated
	 */
	void setParameterName(String value);

	/**
	 * Returns the value of the '<em><b>Derived Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Derived Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Derived Value</em>' attribute.
	 * @see org.eclipse.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage#getMatchSubstitutionRecord_DerivedValue()
	 * @model transient="true" changeable="false" volatile="true" derived="true"
	 *        annotation="org.eclipse.incquery.querybasedfeature patternFQN='org.eclipse.viatra2.emf.incquery.testing.queries.SubstitutionValue'"
	 * @generated
	 */
	Object getDerivedValue();

} // MatchSubstitutionRecord
