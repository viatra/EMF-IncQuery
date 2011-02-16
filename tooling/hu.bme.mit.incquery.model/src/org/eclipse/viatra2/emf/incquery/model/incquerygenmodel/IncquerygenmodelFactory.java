/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.viatra2.emf.incquery.model.incquerygenmodel;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncquerygenmodelPackage
 * @generated
 */
public interface IncquerygenmodelFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	IncquerygenmodelFactory eINSTANCE = org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.impl.IncquerygenmodelFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Inc Query Genmodel</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Inc Query Genmodel</em>'.
	 * @generated
	 */
	IncQueryGenmodel createIncQueryGenmodel();

	/**
	 * Returns a new object of class '<em>Ecore Model</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Ecore Model</em>'.
	 * @generated
	 */
	EcoreModel createEcoreModel();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	IncquerygenmodelPackage getIncquerygenmodelPackage();

} //IncquerygenmodelFactory
