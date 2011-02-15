/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package incquerygenmodel;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ecore Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link incquerygenmodel.EcoreModel#getModels <em>Models</em>}</li>
 * </ul>
 * </p>
 *
 * @see incquerygenmodel.IncquerygenmodelPackage#getEcoreModel()
 * @model
 * @generated
 */
public interface EcoreModel extends EObject {
	/**
	 * Returns the value of the '<em><b>Models</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Models</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Models</em>' reference.
	 * @see #setModels(GenModel)
	 * @see incquerygenmodel.IncquerygenmodelPackage#getEcoreModel_Models()
	 * @model required="true"
	 * @generated
	 */
	GenModel getModels();

	/**
	 * Sets the value of the '{@link incquerygenmodel.EcoreModel#getModels <em>Models</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Models</em>' reference.
	 * @see #getModels()
	 * @generated
	 */
	void setModels(GenModel value);

} // EcoreModel
