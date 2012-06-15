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
 * A representation of the model object '<em><b>Match Set Record</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord#getPatternQualifiedName <em>Pattern Qualified Name</em>}</li>
 *   <li>{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord#getMatches <em>Matches</em>}</li>
 *   <li>{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord#getFilter <em>Filter</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage#getMatchSetRecord()
 * @model
 * @generated
 */
public interface MatchSetRecord extends EObject {
	/**
	 * Returns the value of the '<em><b>Pattern Qualified Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Pattern Qualified Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Pattern Qualified Name</em>' attribute.
	 * @see #setPatternQualifiedName(String)
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage#getMatchSetRecord_PatternQualifiedName()
	 * @model
	 * @generated
	 */
	String getPatternQualifiedName();

	/**
	 * Sets the value of the '{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord#getPatternQualifiedName <em>Pattern Qualified Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Pattern Qualified Name</em>' attribute.
	 * @see #getPatternQualifiedName()
	 * @generated
	 */
	void setPatternQualifiedName(String value);

	/**
	 * Returns the value of the '<em><b>Matches</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Matches</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Matches</em>' containment reference list.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage#getMatchSetRecord_Matches()
	 * @model containment="true" ordered="false"
	 * @generated
	 */
	EList<MatchRecord> getMatches();

	/**
	 * Returns the value of the '<em><b>Filter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Filter</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Filter</em>' containment reference.
	 * @see #setFilter(MatchRecord)
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage#getMatchSetRecord_Filter()
	 * @model containment="true"
	 * @generated
	 */
	MatchRecord getFilter();

	/**
	 * Sets the value of the '{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord#getFilter <em>Filter</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Filter</em>' containment reference.
	 * @see #getFilter()
	 * @generated
	 */
	void setFilter(MatchRecord value);

} // MatchSetRecord
