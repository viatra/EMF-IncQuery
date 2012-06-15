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
 * A representation of the model object '<em><b>Inc Query Snapshot</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.IncQuerySnapshot#getMatchSetRecords <em>Match Set Records</em>}</li>
 *   <li>{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.IncQuerySnapshot#getModelRoots <em>Model Roots</em>}</li>
 *   <li>{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.IncQuerySnapshot#getInputSpecification <em>Input Specification</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage#getIncQuerySnapshot()
 * @model
 * @generated
 */
public interface IncQuerySnapshot extends EObject {
	/**
	 * Returns the value of the '<em><b>Match Set Records</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Match Set Records</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Match Set Records</em>' containment reference list.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage#getIncQuerySnapshot_MatchSetRecords()
	 * @model containment="true"
	 * @generated
	 */
	EList<MatchSetRecord> getMatchSetRecords();

	/**
	 * Returns the value of the '<em><b>Model Roots</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.EObject}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Model Roots</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Model Roots</em>' reference list.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage#getIncQuerySnapshot_ModelRoots()
	 * @model
	 * @generated
	 */
	EList<EObject> getModelRoots();

	/**
	 * Returns the value of the '<em><b>Input Specification</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * The literals are from the enumeration {@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.InputSpecification}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Input Specification</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Input Specification</em>' attribute.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.InputSpecification
	 * @see #setInputSpecification(InputSpecification)
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage#getIncQuerySnapshot_InputSpecification()
	 * @model default=""
	 * @generated
	 */
	InputSpecification getInputSpecification();

	/**
	 * Sets the value of the '{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.IncQuerySnapshot#getInputSpecification <em>Input Specification</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Input Specification</em>' attribute.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.InputSpecification
	 * @see #getInputSpecification()
	 * @generated
	 */
	void setInputSpecification(InputSpecification value);

} // IncQuerySnapshot
