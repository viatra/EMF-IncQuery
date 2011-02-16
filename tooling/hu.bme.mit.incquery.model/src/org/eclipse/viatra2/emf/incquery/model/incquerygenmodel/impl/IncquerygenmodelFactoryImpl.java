/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.EcoreModel;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncQueryGenmodel;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncquerygenmodelFactory;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncquerygenmodelPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class IncquerygenmodelFactoryImpl extends EFactoryImpl implements IncquerygenmodelFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static IncquerygenmodelFactory init() {
		try {
			IncquerygenmodelFactory theIncquerygenmodelFactory = (IncquerygenmodelFactory)EPackage.Registry.INSTANCE.getEFactory("http://incquerygenmodel/1.0"); 
			if (theIncquerygenmodelFactory != null) {
				return theIncquerygenmodelFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new IncquerygenmodelFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IncquerygenmodelFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case IncquerygenmodelPackage.INC_QUERY_GENMODEL: return createIncQueryGenmodel();
			case IncquerygenmodelPackage.ECORE_MODEL: return createEcoreModel();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IncQueryGenmodel createIncQueryGenmodel() {
		IncQueryGenmodelImpl incQueryGenmodel = new IncQueryGenmodelImpl();
		return incQueryGenmodel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EcoreModel createEcoreModel() {
		EcoreModelImpl ecoreModel = new EcoreModelImpl();
		return ecoreModel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IncquerygenmodelPackage getIncquerygenmodelPackage() {
		return (IncquerygenmodelPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static IncquerygenmodelPackage getPackage() {
		return IncquerygenmodelPackage.eINSTANCE;
	}

} //IncquerygenmodelFactoryImpl
