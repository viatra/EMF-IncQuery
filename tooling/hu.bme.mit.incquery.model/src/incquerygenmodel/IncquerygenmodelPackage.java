/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package incquerygenmodel;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see incquerygenmodel.IncquerygenmodelFactory
 * @model kind="package"
 * @generated
 */
public interface IncquerygenmodelPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "incquerygenmodel";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://incquerygenmodel/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "incquerygenmodel";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	IncquerygenmodelPackage eINSTANCE = incquerygenmodel.impl.IncquerygenmodelPackageImpl.init();

	/**
	 * The meta object id for the '{@link incquerygenmodel.impl.IncQueryGenmodelImpl <em>Inc Query Genmodel</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see incquerygenmodel.impl.IncQueryGenmodelImpl
	 * @see incquerygenmodel.impl.IncquerygenmodelPackageImpl#getIncQueryGenmodel()
	 * @generated
	 */
	int INC_QUERY_GENMODEL = 0;

	/**
	 * The feature id for the '<em><b>Ecore Model</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INC_QUERY_GENMODEL__ECORE_MODEL = 0;

	/**
	 * The number of structural features of the '<em>Inc Query Genmodel</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INC_QUERY_GENMODEL_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link incquerygenmodel.impl.EcoreModelImpl <em>Ecore Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see incquerygenmodel.impl.EcoreModelImpl
	 * @see incquerygenmodel.impl.IncquerygenmodelPackageImpl#getEcoreModel()
	 * @generated
	 */
	int ECORE_MODEL = 1;

	/**
	 * The feature id for the '<em><b>Models</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECORE_MODEL__MODELS = 0;

	/**
	 * The number of structural features of the '<em>Ecore Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECORE_MODEL_FEATURE_COUNT = 1;


	/**
	 * Returns the meta object for class '{@link incquerygenmodel.IncQueryGenmodel <em>Inc Query Genmodel</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Inc Query Genmodel</em>'.
	 * @see incquerygenmodel.IncQueryGenmodel
	 * @generated
	 */
	EClass getIncQueryGenmodel();

	/**
	 * Returns the meta object for the containment reference list '{@link incquerygenmodel.IncQueryGenmodel#getEcoreModel <em>Ecore Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Ecore Model</em>'.
	 * @see incquerygenmodel.IncQueryGenmodel#getEcoreModel()
	 * @see #getIncQueryGenmodel()
	 * @generated
	 */
	EReference getIncQueryGenmodel_EcoreModel();

	/**
	 * Returns the meta object for class '{@link incquerygenmodel.EcoreModel <em>Ecore Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Ecore Model</em>'.
	 * @see incquerygenmodel.EcoreModel
	 * @generated
	 */
	EClass getEcoreModel();

	/**
	 * Returns the meta object for the reference '{@link incquerygenmodel.EcoreModel#getModels <em>Models</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Models</em>'.
	 * @see incquerygenmodel.EcoreModel#getModels()
	 * @see #getEcoreModel()
	 * @generated
	 */
	EReference getEcoreModel_Models();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	IncquerygenmodelFactory getIncquerygenmodelFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link incquerygenmodel.impl.IncQueryGenmodelImpl <em>Inc Query Genmodel</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see incquerygenmodel.impl.IncQueryGenmodelImpl
		 * @see incquerygenmodel.impl.IncquerygenmodelPackageImpl#getIncQueryGenmodel()
		 * @generated
		 */
		EClass INC_QUERY_GENMODEL = eINSTANCE.getIncQueryGenmodel();

		/**
		 * The meta object literal for the '<em><b>Ecore Model</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INC_QUERY_GENMODEL__ECORE_MODEL = eINSTANCE.getIncQueryGenmodel_EcoreModel();

		/**
		 * The meta object literal for the '{@link incquerygenmodel.impl.EcoreModelImpl <em>Ecore Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see incquerygenmodel.impl.EcoreModelImpl
		 * @see incquerygenmodel.impl.IncquerygenmodelPackageImpl#getEcoreModel()
		 * @generated
		 */
		EClass ECORE_MODEL = eINSTANCE.getEcoreModel();

		/**
		 * The meta object literal for the '<em><b>Models</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ECORE_MODEL__MODELS = eINSTANCE.getEcoreModel_Models();

	}

} //IncquerygenmodelPackage
