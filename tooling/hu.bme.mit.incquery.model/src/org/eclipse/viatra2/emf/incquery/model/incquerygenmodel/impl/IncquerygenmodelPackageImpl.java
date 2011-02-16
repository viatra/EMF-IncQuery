/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.impl;

import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.EcoreModel;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncQueryGenmodel;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncquerygenmodelFactory;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncquerygenmodelPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class IncquerygenmodelPackageImpl extends EPackageImpl implements IncquerygenmodelPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass incQueryGenmodelEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass ecoreModelEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncquerygenmodelPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private IncquerygenmodelPackageImpl() {
		super(eNS_URI, IncquerygenmodelFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link IncquerygenmodelPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static IncquerygenmodelPackage init() {
		if (isInited) return (IncquerygenmodelPackage)EPackage.Registry.INSTANCE.getEPackage(IncquerygenmodelPackage.eNS_URI);

		// Obtain or create and register package
		IncquerygenmodelPackageImpl theIncquerygenmodelPackage = (IncquerygenmodelPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof IncquerygenmodelPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new IncquerygenmodelPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		GenModelPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theIncquerygenmodelPackage.createPackageContents();

		// Initialize created meta-data
		theIncquerygenmodelPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theIncquerygenmodelPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(IncquerygenmodelPackage.eNS_URI, theIncquerygenmodelPackage);
		return theIncquerygenmodelPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getIncQueryGenmodel() {
		return incQueryGenmodelEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getIncQueryGenmodel_EcoreModel() {
		return (EReference)incQueryGenmodelEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEcoreModel() {
		return ecoreModelEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEcoreModel_Models() {
		return (EReference)ecoreModelEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IncquerygenmodelFactory getIncquerygenmodelFactory() {
		return (IncquerygenmodelFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		incQueryGenmodelEClass = createEClass(INC_QUERY_GENMODEL);
		createEReference(incQueryGenmodelEClass, INC_QUERY_GENMODEL__ECORE_MODEL);

		ecoreModelEClass = createEClass(ECORE_MODEL);
		createEReference(ecoreModelEClass, ECORE_MODEL__MODELS);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		GenModelPackage theGenModelPackage = (GenModelPackage)EPackage.Registry.INSTANCE.getEPackage(GenModelPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes and features; add operations and parameters
		initEClass(incQueryGenmodelEClass, IncQueryGenmodel.class, "IncQueryGenmodel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getIncQueryGenmodel_EcoreModel(), this.getEcoreModel(), null, "ecoreModel", null, 0, -1, IncQueryGenmodel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(ecoreModelEClass, EcoreModel.class, "EcoreModel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getEcoreModel_Models(), theGenModelPackage.getGenModel(), null, "models", null, 1, 1, EcoreModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //IncquerygenmodelPackageImpl
