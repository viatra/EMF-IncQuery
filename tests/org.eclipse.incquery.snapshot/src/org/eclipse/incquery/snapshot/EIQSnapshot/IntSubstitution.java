/**
 */
package org.eclipse.incquery.snapshot.EIQSnapshot;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Int Substitution</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.incquery.snapshot.EIQSnapshot.IntSubstitution#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage#getIntSubstitution()
 * @model
 * @generated
 */
public interface IntSubstitution extends MatchSubstitutionRecord {
	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(int)
	 * @see org.eclipse.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage#getIntSubstitution_Value()
	 * @model
	 * @generated
	 */
	int getValue();

	/**
	 * Sets the value of the '{@link org.eclipse.incquery.snapshot.EIQSnapshot.IntSubstitution#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(int value);

} // IntSubstitution
