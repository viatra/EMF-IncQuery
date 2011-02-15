/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package incquerygenmodel.impl;

import incquerygenmodel.EcoreModel;
import incquerygenmodel.IncquerygenmodelPackage;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Ecore Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link incquerygenmodel.impl.EcoreModelImpl#getModels <em>Models</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EcoreModelImpl extends EObjectImpl implements EcoreModel {
	/**
	 * The cached value of the '{@link #getModels() <em>Models</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModels()
	 * @generated
	 * @ordered
	 */
	protected GenModel models;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EcoreModelImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return IncquerygenmodelPackage.Literals.ECORE_MODEL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GenModel getModels() {
		if (models != null && models.eIsProxy()) {
			InternalEObject oldModels = (InternalEObject)models;
			models = (GenModel)eResolveProxy(oldModels);
			if (models != oldModels) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, IncquerygenmodelPackage.ECORE_MODEL__MODELS, oldModels, models));
			}
		}
		return models;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GenModel basicGetModels() {
		return models;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setModels(GenModel newModels) {
		GenModel oldModels = models;
		models = newModels;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, IncquerygenmodelPackage.ECORE_MODEL__MODELS, oldModels, models));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case IncquerygenmodelPackage.ECORE_MODEL__MODELS:
				if (resolve) return getModels();
				return basicGetModels();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case IncquerygenmodelPackage.ECORE_MODEL__MODELS:
				setModels((GenModel)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case IncquerygenmodelPackage.ECORE_MODEL__MODELS:
				setModels((GenModel)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case IncquerygenmodelPackage.ECORE_MODEL__MODELS:
				return models != null;
		}
		return super.eIsSet(featureID);
	}

} //EcoreModelImpl
