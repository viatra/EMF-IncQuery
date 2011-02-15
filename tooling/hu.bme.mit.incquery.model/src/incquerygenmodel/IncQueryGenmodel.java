/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package incquerygenmodel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Inc Query Genmodel</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link incquerygenmodel.IncQueryGenmodel#getEcoreModel <em>Ecore Model</em>}</li>
 * </ul>
 * </p>
 *
 * @see incquerygenmodel.IncquerygenmodelPackage#getIncQueryGenmodel()
 * @model
 * @generated
 */
public interface IncQueryGenmodel extends EObject {
	/**
	 * Returns the value of the '<em><b>Ecore Model</b></em>' containment reference list.
	 * The list contents are of type {@link incquerygenmodel.EcoreModel}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ecore Model</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ecore Model</em>' containment reference list.
	 * @see incquerygenmodel.IncquerygenmodelPackage#getIncQueryGenmodel_EcoreModel()
	 * @model containment="true"
	 * @generated
	 */
	EList<EcoreModel> getEcoreModel();

} // IncQueryGenmodel
